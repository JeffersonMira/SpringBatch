package jeff.learning.batch.output.toFlatFile.controller;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jeff.learning.batch.output.toFlatFile.domain.Customer;
import jeff.learning.batch.output.toFlatFile.domain.CustomerRowMapper;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	
	@Autowired
	private DataSource datasource;
	
	public JdbcPagingItemReader<Customer> pagingItemReader(){
		JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
		reader.setDataSource(datasource);
		reader.setFetchSize(10); //Number of record per page
		reader.setRowMapper(new CustomerRowMapper());
		
		//You needs to provides it.
		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("id, firstName, lastName, birthdate");
		queryProvider.setFromClause("from customer");
		//In this case, the id is important, as the page takes care of last id that was processed.
		//As the firstname/lastname may not be unique, they are not valid for this usage.	
		Map<String, Order> sortKeys = new HashMap<>();
		sortKeys.put("id", Order.ASCENDING);
		
		queryProvider.setSortKeys(sortKeys);
		reader.setQueryProvider(queryProvider);
		
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
				.reader(pagingItemReader())
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
