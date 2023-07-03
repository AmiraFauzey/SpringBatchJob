package com.mira.springbatchjob.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import com.mira.springbatchjob.entity.AccountInformation;
import com.mira.springbatchjob.repository.AccountInformationRepository;

import lombok.AllArgsConstructor;


@Configuration
@AllArgsConstructor
public class SpringBatchConfig {
	
	private JobRepository jobRepository;
	private PlatformTransactionManager transactionManager;
	private AccountInformationRepository accountInformationRepository;
	
	@Bean
	public ItemReader<AccountInformation> itemReader(){
        FlatFileItemReader<AccountInformation> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("src/main/resources/DataSource.csv"));
        reader.setName("csvReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper());
        System.out.println("reader >>" + reader);
        return reader;
    }
	
	//we need to tell this line mapper this is what deliminater we are using as comma
	//we just extract it and map to the object which is accountInformation object
	private LineMapper<AccountInformation> lineMapper() {
		// TODO Auto-generated method stub
		DefaultLineMapper<AccountInformation> lineMapper = new DefaultLineMapper<>();
		//tokenizer will extract value from csv file
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setDelimiter(",");
		tokenizer.setStrict(false);
		//tokenizer.setNames("ACCOUNT_ID,ACCOUNT_NUMBER,TRX_AMOUNT,DESCRIPTION,TRX_DATE,TRX_TIME,CUSTOMER_ID");
		tokenizer.setNames("accountId","accountNumber","trxAmount","description","date","trxTime","customerId");
		//tokenizer.setNames("accountId");
		
		//fieldSetMapper will map that value to the target class
		BeanWrapperFieldSetMapper<AccountInformation> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(AccountInformation.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        System.out.println("lineMapper >>" + lineMapper);
        return lineMapper;
	}
	
	@Bean
    public ItemProcessor<AccountInformation, AccountInformation> itemProcessor() {
        return new AccountInformationProcessor();
    }
	
	 @Bean
	 public RepositoryItemWriter<AccountInformation> writer() {
	    RepositoryItemWriter<AccountInformation> writer = new RepositoryItemWriter<>();
	    writer.setRepository(accountInformationRepository);
	    writer.setMethodName("save");
	    return writer;
	 }
	 
	 @Bean
	 public Step step1() {
	     	 return new StepBuilder("csv-step", jobRepository)
	         .<AccountInformation, AccountInformation>chunk(10,transactionManager)
	         .reader(itemReader())
	         .processor(itemProcessor())
	         .writer(writer())
	         .build();
	     }
	 
	 @Bean
	 public Job runJob() {
	     return new JobBuilder("importAccountInformation", jobRepository)
	         .start(step1())
	         .build();
	 }
}
