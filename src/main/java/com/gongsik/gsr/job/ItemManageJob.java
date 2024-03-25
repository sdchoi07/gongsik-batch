package com.gongsik.gsr.job;

import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.gongsik.gsr.dto.ItemListDto;
import com.gongsik.gsr.entity.ChemistryEntity;
import com.gongsik.gsr.entity.InventoryEntity;
import com.gongsik.gsr.entity.ProductEntity;
import com.gongsik.gsr.entity.SeedEntity;
import com.gongsik.gsr.repository.ChemistryRepository;
import com.gongsik.gsr.repository.InventoryRepository;
import com.gongsik.gsr.repository.ProductRepository;
import com.gongsik.gsr.repository.SeedRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ItemManageJob {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private SeedRepository seedRepository;

	@Autowired
	private ChemistryRepository chemistryRepository;

	@Autowired
	private InventoryRepository inventoryRepository; 

	@Autowired
	private DataSource dataSource;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;

	private final String filePath = "C:/batch/"; // 원하는 파일의 경로
	
//	@Value("#{jobParameters['date']}") 
//	String date;
//
//	@Value("#{jobParameters['fileNm']}") 
//	String fileNm;
	
	@Bean(name = "itemListManageJob")
	public Job itemListManageJob() {
		return new JobBuilder("itemListManageJob", jobRepository).incrementer(new RunIdIncrementer())
				.start(itemListManageStep()).build();
	}

	@Bean(name = "itemListManageStep")
	@JobScope
	public Step itemListManageStep() {
		return new StepBuilder("itemListManageStep", jobRepository)
				.<ItemListDto, ItemListDto>chunk(100, platformTransactionManager).reader(itemReader(null,null))
				.writer(itemWriters()).build();
//			      .tasklet(fileReadingTasklet(), platformTransactionManager).build();
	}

	@Bean
	@StepScope
	public ItemWriter<ItemListDto> itemWriters() {
		return items -> {
			for (ItemListDto item : items) {
				String gubun1 = item.getInvenLClsNo();
				String gubun2 = item.getInvenMClsNo();
				String gubun3 = item.getInvenSClsNo();
//				log.info("itemList : {}", item);
				//재고
				Optional<InventoryEntity> inventoryEntity = inventoryRepository.findByInvenLClsNoAndInvenMClsNoAndInvenSClsNo(gubun1,gubun2,gubun3);
				if(inventoryEntity.isPresent()) {
					InventoryEntity inventory = inventoryEntity.get();
					inventory.setInvenLClsNm(item.getInvenLClsNm());
					inventory.setInvenMClsNm(item.getInvenMClsNm());
					inventory.setInvenSClsNm(item.getInvenSClsNm());
					inventory.setInvenCnt(item.getInvenCnt());
					inventoryRepository.save(inventory);
				}else {
					InventoryEntity inventory = new InventoryEntity();
					inventory.setInvenLClsNm(item.getInvenLClsNm());
					inventory.setInvenMClsNm(item.getInvenMClsNm());
					inventory.setInvenSClsNm(item.getInvenSClsNm());
					inventory.setInvenLClsNo(item.getInvenLClsNo());
					inventory.setInvenMClsNo(item.getInvenMClsNo());
					inventory.setInvenSClsNo(item.getInvenSClsNo());
					inventory.setInvenCnt(item.getInvenCnt());
					inventoryRepository.save(inventory);
					
				}
				
				//상풍
				if (item.getInvenLClsNo().startsWith("1")) { // 상품
					Optional<ProductEntity> productEntity = productRepository.findByProductNo(gubun3);
					if (productEntity.isPresent()) {
						// ItemListDto에서 필요한 정보를 Product로 매핑
						ProductEntity product = productEntity.get();
						product.setProductNm(item.getInvenSClsNm());
						int price = Integer.parseInt(item.getInvenPrice().replaceAll(",", ""));
						product.setProductPrice(price);
						productRepository.save(product); // Spring Data JPA를 통해 엔티티를 저장
					} else {
						ProductEntity product = new ProductEntity();
						product.setProductNm(item.getInvenSClsNm());
						product.setProductNo(item.getInvenSClsNo());
						int price = Integer.parseInt(item.getInvenPrice().replaceAll(",", ""));
						product.setProductPrice(price);
						productRepository.save(product); // Spring Data JPA를 통해 엔티티를 저장
					}
				} else if (item.getInvenLClsNo().startsWith("2")) { // 씨앗
					Optional<SeedEntity> seedEntity = seedRepository.findBySeedNo(gubun3);
					if (seedEntity.isPresent()) {
						SeedEntity seed = seedEntity.get();
						// ItemListDto에서 필요한 정보를 Seed로 매핑
						seed.setSeedNm(item.getInvenMClsNm());
						int price = Integer.parseInt(item.getInvenPrice().replaceAll(",", ""));
						seed.setSeedPrice(price);
						seedRepository.save(seed); // Spring Data JPA를 통해 엔티티를 저장
					} else {
						SeedEntity seed = new SeedEntity();
						// ItemListDto에서 필요한 정보를 Seed로 매핑
						seed.setSeedNm(item.getInvenMClsNm());
						seed.setSeedNo(item.getInvenSClsNo());
						int price = Integer.parseInt(item.getInvenPrice().replaceAll(",", ""));
						seed.setSeedPrice(price);
						seedRepository.save(seed); // Spring Data JPA를 통해 엔티티를 저장
					}
				} else if (item.getInvenLClsNo().startsWith("3")) { // 약품
					Optional<ChemistryEntity> chemistryEntity = chemistryRepository.findByChemistryNo(gubun3);
					if (chemistryEntity.isPresent()) {
						ChemistryEntity chemistry = chemistryEntity.get();
						// ItemListDto에서 필요한 정보를 Chemistry로 매핑
						chemistry.setChemistryNm(item.getInvenMClsNm());
						int price = Integer.parseInt(item.getInvenPrice().replaceAll(",", ""));
						chemistry.setChemistryPrice(price);
						chemistryRepository.save(chemistry); // Spring Data JPA를 통해 엔티티를 저장
					}else {
						ChemistryEntity chemistry = new ChemistryEntity();
						// ItemListDto에서 필요한 정보를 Chemistry로 매핑
						chemistry.setChemistryNm(item.getInvenMClsNm());
						chemistry.setChemistryNo(item.getInvenSClsNo());
						int price = Integer.parseInt(item.getInvenPrice().replaceAll(",", ""));
						chemistry.setChemistryPrice(price);
						chemistryRepository.save(chemistry); // Spring Data JPA를 통해 엔티티를 저장
					}
				}
			}
		};
	}
	
	@Bean
	@StepScope
	public FlatFileItemReader<ItemListDto> itemReader(@Value("#{jobParameters['fileNm']}") String fileNm,@Value("#{jobParameters['date']}") 
	String date) {
		
		
//		JobParameters parameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
//		String fileNm = "itemList";
//		String date = "20240320";

		FlatFileItemReader<ItemListDto> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setResource(new FileSystemResource(filePath + fileNm + "_" + date + ".csv"));
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setEncoding("UTF-8"); // 인토딩 설정

		/* defaultLineMapper: 읽으려는 데이터 LineMapper을 통해 Dto로 매핑 */
		DefaultLineMapper<ItemListDto> defaultLineMapper = new DefaultLineMapper<>();

		/* delimitedLineTokenizer : csv 파일에서 구분자 지정하고 구분한 데이터 setNames를 통해 각 이름 설정 */
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(","); // csv 파일에서 구분자
		delimitedLineTokenizer.setNames("invenLClsNo", "invenMClsNo", "invenSClsNo", "invenLClsNm", "invenMClsNm",
				"invenSClsNm", "invenCnt", "invenPrice"); // 행으로 읽은 데이터 매칭할 데이터 각 이름
		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer); // lineTokenizer 설정

		/* beanWrapperFieldSetMapper: 매칭할 class 타입 지정 */
		BeanWrapperFieldSetMapper<ItemListDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
		beanWrapperFieldSetMapper.setTargetType(ItemListDto.class);

		defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper); // fieldSetMapper 지정

		flatFileItemReader.setLineMapper(defaultLineMapper); // lineMapper 지정
		return flatFileItemReader;
	}
    // ItemReader
//    public Tasklet fileReadingTasklet() {
//        return (contribution, chunkContext) -> {
//        	log.info(">>>>> This is Step1");
//        	 System.out.println("테스트21");
//            try {
//                JobParameters parameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
//                String fileNm = parameters.getString("fileNm");
//                String date = parameters.getString("date");
//                
//                FlatFileItemReader<ItemListDto> flatFileItemReader = new FlatFileItemReader<>();
//                flatFileItemReader.setResource(new FileSystemResource(filePath + fileNm + "_" + date + ".csv"));
//                flatFileItemReader.setLinesToSkip(1);
//                flatFileItemReader.setEncoding("UTF-8"); //인토딩 설정
//
//                /* defaultLineMapper: 읽으려는 데이터 LineMapper을 통해 Dto로 매핑 */
//                DefaultLineMapper<ItemListDto> defaultLineMapper = new DefaultLineMapper<>();
//
//                /* delimitedLineTokenizer : csv 파일에서 구분자 지정하고 구분한 데이터 setNames를 통해 각 이름 설정 */
//                DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(","); //csv 파일에서 구분자
//                delimitedLineTokenizer.setNames("invenLClsNo","invenMClsNo","invenSClsNo","invenLClsNm","invenMClsNm","invenSClsNm","invenCnt","invenPrice"); //행으로 읽은 데이터 매칭할 데이터 각 이름
//                defaultLineMapper.setLineTokenizer(delimitedLineTokenizer); //lineTokenizer 설정
//
//                /* beanWrapperFieldSetMapper: 매칭할 class 타입 지정 */
//                BeanWrapperFieldSetMapper<ItemListDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
//                beanWrapperFieldSetMapper.setTargetType(ItemListDto.class);
//
//                defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper); //fieldSetMapper 지정
//
//                flatFileItemReader.setLineMapper(defaultLineMapper); //lineMapper 지정
//
//                flatFileItemReader.open(new ExecutionContext());
//
//                // 파일에서 읽은 데이터를 데이터베이스에 저장
//                ItemListDto item;
//                while ((item = flatFileItemReader.read()) != null) {
//                	String sql;
//                	//재고
//                	sql = (
//                			  "			INSERT INTO GS_INVENTORY_MST 				"
//                			+ "            		( INVEN_L_CLS_NO					"
//                			+ "                  ,INVEN_M_CLS_NO					"
//                			+ "					 ,INVEN_S_CLS_NO					"
//                			+ "					 ,INVEN_L_CLS_NM					"
//                			+ "					 ,INVEN_M_CLS_NM					"
//                			+ "					 ,INVEN_S_CLS_NM					"
//                			+ "					 ,INVEN_CNT							"
//                			+ "					 ,CRG_DATE							"
//                			+ "					 ,END_DATE)							"
//                			+ "			VALUES  									"
//                			+ "				    ( :invenLClsNo						"
//                			+ "                  ,:invenMClsNo						"
//                			+ "					 ,:invenSClsNo						"
//                			+ "					 ,:invenLClsNm						"
//                			+ "					 ,:invenMClsNm						"
//                			+ "					 ,:invenSClsNm						"
//                			+ "					 ,:invenCnt							"
//                			+ "					 ,'" + date + "'	  				"
//                			+ "				     ,'99991231')						"
//                			+ "		ON DUPLICATE KEY UPDATE							"
//                			+ "				INVEN_L_CLS_NO = :invenLClsNo			"
//                			+ "            ,INVEN_M_CLS_NO = :invenMClsNo			"
//                			+ "            ,INVEN_S_CLS_NO = :invenSClsNo			"
//                			+ "            ,INVEN_L_CLS_NM = :invenLClsNm			"
//                			+ "            ,INVEN_M_CLS_NM = :invenMClsNm			"
//                			+ "            ,INVEN_S_CLS_NM = :invenSClsNm			"
//                			+ "            ,INVEN_CNT = :invenCnt				"
//                			+ "            ,CRG_DATE = '" + date + "'				"
//                			+ "             END_DATE = '99991231')					");
//                	
//                    // 조건에 따라 다른 테이블에 저장하는 writer 설정
//                    JdbcBatchItemWriter<ItemListDto> writer = new JdbcBatchItemWriterBuilder<ItemListDto>()
//                            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
//                            .dataSource(dataSource)
//                            .sql(sql) // SQL 쿼리 설정
//                            .itemPreparedStatementSetter((itemToSet, ps) -> {
//                                ps.setString(1, itemToSet.getInvenLClsNo());
//                                ps.setString(2, itemToSet.getInvenMClsNo());
//                                ps.setString(3, itemToSet.getInvenSClsNo()); 
//                                ps.setString(4, itemToSet.getInvenLClsNm()); 
//                                ps.setString(5, itemToSet.getInvenMClsNm()); 
//                                ps.setString(6, itemToSet.getInvenSClsNm()); 
//                                ps.setInt(7, itemToSet.getInvenCnt()); 
//                                ps.setString(8, date); 
//                                ps.setString(9, "99991231"); 
//                                ps.setInt(10, itemToSet.getInvenCnt()); 
//                                // 나머지 매개변수 설정
//                            })
//                            .build();
//
//                    List<ItemListDto> itemList = Collections.singletonList(item);
//                    Chunk<ItemListDto> chunk = new Chunk<>(itemList);
//                    writer.write(chunk);
//                    
//                    
//                    // 조건에 따라 다른 테이블에 저장
//                    if (item.getInvenLClsNo().startsWith("1")) { //상품
//                    	sql = ("   										"
//                        		+ "		INSERT INTO GS_PRODUCT_INF 				"
//                        		+ "            (								"
//                        		+ "				 PRODUCT_NM						"
//                        		+ "				,PRODUCT_NO						"
//                        		+ "				,PRODUCT_PRICE					"
//                        		+ "				)								"
//                        		+ "	    VALUES									"
//                        		+ "			    (								"
//                        		+ "				 :invenSClsNm					"
//                        		+ "				,:invenSClsNo					"
//                        		+ "				,:invenPrice						"
//                        		+ "				)								"
//                        		+ "   ON DUPLICATE KEY UPDATE					"
//                        		+ "            PRODUCT_NM = :invenSClsNm		"
//                        		+ "            ,PRODUCT_NO = :invenSClsNo		"
//                        		+ "            ,PRODUCT_PRICE = :invenCnt		"	);
//                    } else if (item.getInvenLClsNo().startsWith("2")) {//씨앗
//                    	sql = ("   										"
//                         		+ "		INSERT INTO GS_SEED_INF 				"
//                         		+ "            (								"
//                         		+ "				 SEED_NM						"
//                         		+ "				,SEED_NO						"
//                         		+ "				,SEED_PRICE					"
//                         		+ "				)								"
//                         		+ "	    VALUES									"
//                         		+ "			    (								"
//                         		+ "				 :invenSClsNm					"
//                         		+ "				,:invenSClsNo					"
//                         		+ "				,:invenPrice						"
//                         		+ "				)								"
//                         		+ "   ON DUPLICATE KEY UPDATE					"
//                         		+ "            SEED_NM = :invenSClsNm		"
//                         		+ "            ,SEED_NO = :invenSClsNo		"
//                         		+ "            ,SEED_PRICE = :invenCnt		"	);
//                    } else if (item.getInvenLClsNo().startsWith("3")) {//약품
//                    	sql = ("   										"
//                         		+ "		INSERT INTO GS_CHEMISTRY_INF 				"
//                         		+ "            (								"
//                         		+ "				 CHEMISTRY_NM						"
//                         		+ "				,CHEMISTRY_NO						"
//                         		+ "				,CHEMISTRY_PRICE					"
//                         		+ "				)								"
//                         		+ "	    VALUES									"
//                         		+ "			    (								"
//                         		+ "				 :invenSClsNm					"
//                         		+ "				,:invenSClsNo					"
//                         		+ "				,:invenPrice						"
//                         		+ "				)								"
//                         		+ "   ON DUPLICATE KEY UPDATE					"
//                         		+ "            CHEMISTRY_NM = :invenSClsNm		"
//                         		+ "            ,CHEMISTRY_NO = :invenSClsNo		"
//                         		+ "            ,CHEMISTRY_PRICE = :invenCnt		"	);
//                    }
//                 // 조건에 따라 다른 테이블에 저장하는 writer 설정
//                   writer = new JdbcBatchItemWriterBuilder<ItemListDto>()
//                            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
//                            .dataSource(dataSource)
//                            .sql(sql) // SQL 쿼리 설정
//                            .itemPreparedStatementSetter((itemToSet, ps) -> {
//                                ps.setString(1, itemToSet.getInvenSClsNo()); 
//                                ps.setString(2, itemToSet.getInvenSClsNm()); 
//                                ps.setString(3, itemToSet.getInvenPrice()); 
//                                // 나머지 매개변수 설정
//                            })
//                            .build();
//
//                    itemList = Collections.singletonList(item);
//                    chunk = new Chunk<>(itemList);
//                    writer.write(chunk);
//
//                }
//
//                flatFileItemReader.close();
//            } catch (Exception e) {
//                // 예외 처리
//                e.printStackTrace();
//            }
//
//            return RepeatStatus.FINISHED;
//        };
//    }
}