package jeff.learning.batch.input.itemReader.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jeff.learning.batch.input.itemReader.reader.StatelessItemReader;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public StatelessItemReader statelessItemReader(){
		List<String> data = new ArrayList<>();
		
		data.add("Foo");
		data.add("Bar");
		data.add("Baz");
		
		return new StatelessItemReader(data);
	}
	
	@Bean
	public Step step1(){
		return stepBuilderFactory.get("step1")
				.<String,String>chunk(2)
				.reader(statelessItemReader())
				.writer(list->{
					for (String string : list) {
						System.out.println(">> CurItem = "+string);
					}
				}).build();
	}
	
	@Bean
	public Job job(){
		return jobBuilderFactory.get("job"+LocalDateTime.now())
				.start(step1())
				.build();
	}
}
