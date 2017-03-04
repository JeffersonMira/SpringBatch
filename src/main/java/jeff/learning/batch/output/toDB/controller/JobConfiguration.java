	package jeff.learning.batch.output.toDB.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jeff.learning.batch.input.fromFlatFile.domain.Customer;
import jeff.learning.batch.input.fromFlatFile.domain.CustomerFieldSetMapper;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource datasource;
	
	@Bean
	public FlatFileItemReader<Customer> customerItemReader(){
		FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
		
		reader.setLinesToSkip(1); //first line is the headers. Not real data.
		reader.setResource(new ClassPathResource("/data/customer.csv"));
		
		DefaultLineMapper<Customer> customerLineMapper = new DefaultLineMapper<>();
		
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(";"); //what separates each line
		tokenizer.setNames(new String[]{"id", "firstName", "lastName", "birthdate"});
		
		customerLineMapper.setLineTokenizer(tokenizer);
		customerLineMapper.setFieldSetMapper(new CustomerFieldSetMapper()); // there is another implementation
		                                     //that doesnt need to write any code. BeanFieldSetMappper.
											// which uses sets and gets.
		customerLineMapper.afterPropertiesSet();
		
		reader.setLineMapper(customerLineMapper);
		
		return reader;
	}
	
	@Bean
	public JdbcBatchItemWriter<Customer> customerItemWriter(){
		JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();
		
		writer.setDataSource(this.datasource);
		writer.setSql("INSERT INTO CUSTOMER VALUES(:id, :firstName, :lastName, 	:birthdate)");
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Customer>());
		writer.afterPropertiesSet();
		return writer;
	}
	
	@Bean
	public Step step1(){
		return stepBuilderFactory.get("step1")
				.<Customer,Customer>chunk(40)
				.reader(customerItemReader())
				.writer(customerItemWriter())
				.build();
	}
	
	@Bean
	public Job job(){
		return jobBuilderFactory.get("job"+LocalDateTime.now())
				.start(step1())
				.build();
	}
}
