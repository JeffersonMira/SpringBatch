package jeff.learning.batch.processer.filtering.configs;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import jeff.learning.batch.processer.filtering.domain.Customer;
import jeff.learning.batch.processer.filtering.domain.CustomerLineAggregator;
import jeff.learning.batch.processer.filtering.domain.CustomerRowMapper;
import jeff.learning.batch.processer.filtering.domain.FilteringItemProcessing;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource dataSource;
	
	/*CONFIGURING READER. NOTHING IMPORTANT HERE*/
	@Bean
	public JdbcPagingItemReader<Customer> pagingItemReader(){
		
		JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
		reader.setDataSource(this.dataSource);
		reader.setFetchSize(10);
		reader.setRowMapper(new CustomerRowMapper());
		
		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("id, firstName, lastName, birthdate");
		queryProvider.setFromClause("from customer");
		
		Map<String, Order> sortKeys = new HashMap<>();
		sortKeys.put("id", Order.ASCENDING);
		
		queryProvider.setSortKeys(sortKeys);
		
		reader.setQueryProvider(queryProvider);
		
		return reader;
	}
	
	/*CONFIGURING THE WRITERS. FIRST FOR JSON AND THEN FOR XML*/
	@Bean
	public FlatFileItemWriter<Customer> ItemWriter() throws Exception{
		FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();
		
		itemWriter.setLineAggregator(new PassThroughLineAggregator<>()); //just calls the toString of each item.
//		itemWriter.setLineAggregator(new CustomerLineAggregator());
		String customerOutputPath = File.createTempFile("customerOutput", ".out").getAbsolutePath();
		System.out.println(">> output file -  "+customerOutputPath);
		itemWriter.setResource(new FileSystemResource(customerOutputPath));
		itemWriter.afterPropertiesSet();
		
		return itemWriter;
	}
	
  @Bean
  public FilteringItemProcessing itemProcessor(){
	  return new FilteringItemProcessing();
  }
	
	
	@Bean
	public Step step1() throws Exception{
		return stepBuilderFactory.get("step1")
				.<Customer,Customer>chunk(10) //10 items per transaction
				.reader(pagingItemReader())
				.processor(itemProcessor())
				.writer(ItemWriter())
				.build();
	}
	
	@Bean
	public Job job() throws Exception{
		return jobBuilderFactory.get("job"+LocalDateTime.now())
				.start(step1())
				.build();
	}
}
