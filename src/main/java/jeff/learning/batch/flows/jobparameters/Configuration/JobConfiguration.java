package jeff.learning.batch.flows.jobparameters.Configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	@StepScope /*creates a new scope that is available in springbatch. It is telling to spring batch to instantiate it
	on the step that is calling it uses it. Lazyly instantiated. It means that it will be intantiates just when the
	step1 is executed (not like the other beans that are instantiated in the load of application, on a singleton scope.*/
	public Tasklet helloWorldTasklet(@Value("#{jobParameters['message']}") String message){
		return (stepContribution, chunkContext) ->
		{
			System.out.println(message);
			return RepeatStatus.FINISHED;
		};
		
	}
	
	/**
	 * It is never going to be called this way.
	 */
	@Bean
	public Step step1(){
		return stepBuilderFactory.get("step1")
				.tasklet(helloWorldTasklet(null)).build();
	}
	
	@Bean	
	public Job jobParametersJob(){
		return jobBuilderFactory.get("jobParametersJob")
				.start(step1()).build();
	}
}
