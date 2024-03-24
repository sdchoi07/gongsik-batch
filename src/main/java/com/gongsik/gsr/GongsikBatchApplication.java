package com.gongsik.gsr;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.gongsik.gsr.config.BatchAutoConfig;
//import org.springframework.context.annotation.Import;

//import com.gongsik.gsr.config.BatchAutoConfig;

//@EnableBatchProcessing(dataSourceRef = "batchDataSource", transactionManagerRef = "batchTransactionManager")
//@Import({BatchAutoConfig.class})
@SpringBootApplication
public class GongsikBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(GongsikBatchApplication.class, args);
	}

}
