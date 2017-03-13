package jeff.learning.batch.scaling.partitioner.configs;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import jeff.learning.batch.processer.composite.domain.Customer;
import jeff.learning.batch.processer.composite.domain.CustomerRowMapper;
import jeff.learning.batch.processer.composite.domain.FilteringItemProcessing;
import jeff.learning.batch.processer.composite.domain.UpperCaseItemProcessor;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource dataSource;
	
//	public ColumnRangePartitioner
	
	
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
	public StaxEventItemWriter<Customer> xmlItemWriter() throws Exception{
		
		XStreamMarshaller marshaller = new XStreamMarshaller();
		Map<String, Class> aliases = new HashMap<>();
		
		aliases.put("customer", Customer.class);
		
		marshaller.setAliases(aliases);
		
		StaxEventItemWriter<Customer> writer = new StaxEventItemWriter<>();
		
		writer.setRootTagName("customers");
		writer.setMarshaller(marshaller);
		
		String customerOutputPath = File.createTempFile("customerOutput", ".out").getAbsolutePath();
		System.out.println(">> output file -  "+customerOutputPath);
	
		writer.setResource(new FileSystemResource(customerOutputPath));
		
		writer.afterPropertiesSet();
		
		return writer;
	}
	
  @Bean
  public CompositeItemProcessor<Customer, Customer> compositeItemProcessor() throws Exception{
	  List<ItemProcessor<Customer, Customer>> delegates = new ArrayList<>();
	  
	  delegates.add(new FilteringItemProcessing());
	  delegates.add(new UpperCaseItemProcessor());
	  
	  CompositeItemProcessor<Customer, Customer> compositeItemProcessor = new CompositeItemProcessor<>();
	  
	  compositeItemProcessor.setDelegates(delegates);
	  compositeItemProcessor.afterPropertiesSet();
	  
	  return compositeItemProcessor;
  }
	
	@Bean
	public Step step1() throws Exception{
		return stepBuilderFactory.get("step1")
				.<Customer,Customer>chunk(10) //10 items per transaction
				.reader(pagingItemReader())
				.processor(compositeItemProcessor())
				.writer(xmlItemWriter())
				.build();
	}
	
	@Bean
	public Job job() throws Exception{
		return jobBuilderFactory.get("job"+LocalDateTime.now())
				.start(step1())
				.build();
	}
}
