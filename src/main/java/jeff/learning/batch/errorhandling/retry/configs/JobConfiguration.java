package jeff.learning.batch.errorhandling.retry.configs;


import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import jeff.learning.batch.errorhandling.retry.components.CustomRetryableException;
import jeff.learning.batch.errorhandling.retry.components.RetryItemProcessor;
import jeff.learning.batch.errorhandling.retry.components.RetryItemWriter;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	@StepScope
	public ListItemReader<String> reader(){
		List<String> items = new ArrayList<>();
		
		for(int i=0; i < 100; i++){
			items.add(String.valueOf(i));
		}
		
		return new ListItemReader<>(items);
	}
	
	@Bean
	@StepScope
	public RetryItemProcessor processor(@Value("#{jobParameters['retry']}") String retry){
		RetryItemProcessor processor = new RetryItemProcessor();
		
		processor.setRetry(StringUtils.hasText(retry) && retry.equalsIgnoreCase("processor"));
		
		return processor;
	}
	
	
	@Bean
	@StepScope
	public RetryItemWriter writer(@Value("#{jobParameters['retry']}") String retry){
		RetryItemWriter writer = new RetryItemWriter();
		
		writer.setRetry(StringUtils.hasText(retry) && retry.equalsIgnoreCase("writer"));
		
		return writer;
	}
	
	@Bean
	public Step Step1(){
		return stepBuilderFactory.get("step1")
				.<String,String>chunk(10)
				.reader(reader())
				.processor(processor(null))
				.writer(writer(null))
				/*I have to define this guy before using the retry or even the skip logic.*/
				.faultTolerant()
				//It will only allows to retry if the exception that is returned is the
				//CustomRetryableException.class
				.retry(CustomRetryableException.class) 
				.retryLimit(15)
				.build();
	}
	
	
	@Bean
	public Job job() throws Exception{
		return jobBuilderFactory.get("job")
				.start(Step1())
				.build();
	}
}
