package jeff.learning.batch.input.fromFlatFile.Configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jeff.learning.batch.input.fromFlatFile.domain.Customer;
import jeff.learning.batch.input.fromFlatFile.domain.CustomerFieldSetMapper;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

	@Autowired private JobBuilderFactory jobBuilderFactory;
	@Autowired private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public FlatFileItemReader<Customer> customerItemReader(){
		FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
		
		reader.setLinesToSkip(1); //first line is the headers. Not real data.
		reader.setResource(new ClassPathResource("/data/customer.csv"));
		
		DefaultLineMapper<Customer> customerLineMapper = new DefaultLineMapper<>();
		
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(";");
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
				.<Customer,Customer>chunk(10)
				.reader(customerItemReader())
				.writer(customerItemWritter())
				.build();
	}
	
	@Bean
	public Job job1(){
		return jobBuilderFactory.get("job1")
				.start(step1())
				.build();
	}
	
	
}
