package jeff.learning.batch.output.itemwritter.controller;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jeff.learning.batch.input.itemReader.reader.StatelessItemReader;
import jeff.learning.batch.output.itemwritter.writter.SysOutItemWriter;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public ListItemReader<String> itemReader(){
		List<String> data = new ArrayList<>(100);
		
		for(int i = 0; i < 100 ; i++){
			data.add(String.valueOf(i));
		}
		
		return new ListItemReader<>(data);
	}
	
	@Bean
	public SysOutItemWriter itemWriter(){
		return new SysOutItemWriter();
	}
	
	@Bean
	public Step step1(){
		return stepBuilderFactory.get("step1")
				.<String,String>chunk(40)
				.reader(itemReader())
				.writer(itemWriter())
				.build();
	}
	
	@Bean
	public Job job(){
		return jobBuilderFactory.get("job"+LocalDateTime.now())
				.start(step1())
				.build();
	}
}
