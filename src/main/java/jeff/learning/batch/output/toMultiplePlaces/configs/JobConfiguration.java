package jeff.learning.batch.output.toMultiplePlaces.configs;

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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import jeff.learning.batch.output.toMultiplePlaces.domain.Customer;
import jeff.learning.batch.output.toMultiplePlaces.domain.CustomerClassifier;
import jeff.learning.batch.output.toMultiplePlaces.domain.CustomerLineAggregator;
import jeff.learning.batch.output.toMultiplePlaces.domain.CustomerRowMapper;

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
	public FlatFileItemWriter<Customer> jsonItemWriter() throws Exception{
		
		FlatFileItemWriter<Customer> writer = new FlatFileItemWriter<>();
		
		writer.setLineAggregator(new CustomerLineAggregator());
		String customerOutputPath = File.createTempFile("customerOutput", ".out").getAbsolutePath();
		System.out.println(">> output file -  "+customerOutputPath);
		writer.setResource(new FileSystemResource(customerOutputPath));
		writer.afterPropertiesSet();
		
		return writer;
	}
	
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
	
	/*THIS GUY IS NECESSARY TO WRITE FOR BOTH IN THE SAME TIME*/
	@Bean
	public CompositeItemWriter<Customer> itemWriter() throws Exception{
		List<ItemWriter<? super Customer>> writers = new ArrayList<>();
		
		writers.add(xmlItemWriter());
		writers.add(jsonItemWriter());
		
		CompositeItemWriter<Customer> itemWriter = new CompositeItemWriter<>();
		
		itemWriter.setDelegates(writers);
		itemWriter.afterPropertiesSet();
		
		return itemWriter;
	}
	
	/*This guy is to define any specific rule for what is going to be writen and where*/
	@Bean
	public ClassifierCompositeItemWriter<Customer> itemWriterClassifier() throws Exception{
		ClassifierCompositeItemWriter<Customer> writer = new ClassifierCompositeItemWriter<>();
		writer.setClassifier(new CustomerClassifier(xmlItemWriter(), jsonItemWriter()));
		
		return writer;
	}
	
	
	@Bean
	public Step step1() throws Exception{
		return stepBuilderFactory.get("step1")
				.<Customer,Customer>chunk(40)
				.reader(pagingItemReader())
//				.writer(itemWriter())  //It already implements itemstream
				.writer(itemWriterClassifier())  // it does not, that is why it needs to declare the stream.
				.stream(xmlItemWriter())
				.stream(jsonItemWriter())
				.build();
	}
	
	@Bean
	public Job job() throws Exception{
		return jobBuilderFactory.get("job"+LocalDateTime.now())
				.start(step1())
				.build();
	}
}
