package com.gongsik.gsr.job;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TestJob {
	
	
	private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    
    
	 @Bean(name="testSimpleJob")
	  public Job testSimpleJob(){
	    return new JobBuilder("testSimpleJob", jobRepository)
	    		.incrementer(new RunIdIncrementer())
	      .start(testStep())
	      .build();
	  }


	 @Bean(name ="testStep")
	 @JobScope
	  public Step testStep(){
	    return new StepBuilder("testStep", jobRepository)
	      .tasklet(testTasklet(), platformTransactionManager).build();
	  }

	 @Bean
	  public Tasklet testTasklet(){
	    return ((contribution, chunkContext) -> {
	      System.out.println("테스트1");
	      return RepeatStatus.FINISHED;
	    });
	  }
	}

