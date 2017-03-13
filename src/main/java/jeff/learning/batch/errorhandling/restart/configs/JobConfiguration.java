package jeff.learning.batch.errorhandling.restart.configs;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
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
	@StepScope // It makes the bean to be have the taskelet scope. So for each step that is created, a 
	// new object will be created. It is different from the common because the normal is the singleton
	// scope
	public Tasklet restartTasklet(){
		return new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();
				
				if(stepExecutionContext.containsKey("ran")){
					System.out.println("This time we'll let it go");
					return RepeatStatus.FINISHED;
				}else{
					System.out.println("I don't think so....");
					chunkContext.getStepContext().getStepExecution().getExecutionContext().put("ran", true);
					throw new RuntimeException("Not this time....");
				}
			}
		};
	}
	
	@Bean
	public Step step1(){
		return stepBuilderFactory.get("step1")
				.tasklet(restartTasklet())
				.build();
	}
	
	@Bean
	public Step step2(){
		return stepBuilderFactory.get("step2")
				.tasklet(restartTasklet())
				.build();
	}
	
	@Bean
	public Job job() throws Exception{
		return jobBuilderFactory.get("job")
				.start(step1())
				.next(step2())
				.build();
	}
}
