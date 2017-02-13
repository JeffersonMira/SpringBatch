package jeff.learning.batch.input.fromDB.cotroller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jeff.learning.batch.input.fromDB.domain.Customer;
import jeff.learning.batch.input.fromDB.domain.CustomerRowMapper;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private DataSource dataSource;
	
	/**
	 * It provides a rowmapper, which is reads using a cursor. If something happens while it is being
	 * executed, you can restart the cursor and continue from where you left over.
	 * The problem is that it is not thread safe. If you are using a single thread, there is no problem,
	 * but if there is more than one thread, it will not be save and have a lot of issues.
	 */
	@Bean
	JdbcCursorItemReader<Customer> cursorItemReader(){
		JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<>();
		
		//This part is very important. If I don't order the result list, 
		//there is no guarantee when I restart the job.
		//It just keeps the number of items completed.
		reader.setSql("SELECT id, firstName, lastName, birthdate FROM customer order by lastname, firstname");
		reader.setDataSource(this.dataSource);
		reader.setRowMapper(new CustomerRowMapper());

		return reader;
	}
	
	/**
	 * This guy is thread safe.
	 */
	@Bean
	public JdbcPagingItemReader<Customer> pagingItemReader(){
			JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
			reader.setDataSource(dataSource);
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
	public ItemWriter<Customer> customerItemWritter(){
		return items -> {
			for(Customer item : items){
				System.out.println(item.toString());
			}
		};
	}
	
	@Bean
	public Step step1(){
		return stepBuilderFactory.get("step1")
				.<Customer, Customer>chunk(10)
//				.reader(cursorItemReader())
				.reader(pagingItemReader())
				.writer(customerItemWritter())
				.build();
	}
	
	@Bean
	public Job job(){
		return jobBuilderFactory.get("job"+LocalDateTime.now())
				.start(step1())
				.build();
	}
}
