package jeff.learning.batch.input.fromMultipleDataSources.Configuration;

import jeff.learning.batch.input.fromMultipleDataSources.domain.Customer;
import jeff.learning.batch.input.fromMultipleDataSources.domain.CustomerFieldSetMapper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

	@Autowired private JobBuilderFactory jobBuilderFactory;
	@Autowired private StepBuilderFactory stepBuilderFactory;

//	@Value()
//	""
//	private Resource[]  inputFiles;


	
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
//				.reader(customerItemReader())
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
