package jeff.learning.batch.split.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class BatchConfiguration {

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Bean
	public Tasklet tasket(){
		return new CountingTasklet();
	}
	
	@Bean
	public Flow flow1(){
		return new FlowBuilder<Flow>("flow1")
				.start(stepBuilderFactory.get("step1").tasklet(tasket()).build()).build();
	}
	
	@Bean
	public Flow flow2(){
		return new FlowBuilder<Flow>("flow2")
				.start(stepBuilderFactory.get("step2").tasklet(tasket()).build())
				.next(stepBuilderFactory.get("step3").tasklet(tasket()).build()).
				build();
	}
	
   @Bean
   public Job job(){
	   return jobBuilderFactory.get("job")
			   .start(flow1())
			   .split(new SimpleAsyncTaskExecutor()).add(flow2()) // while executing the first one, execute the second one in paralel
			   .end()
			   .build();
   }
	
	public static class CountingTasklet implements Tasklet{

		//It is used to count and show exactly what step is being executed by which thread
		@Override
		public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
			System.out.println(String.format("%s has been executed on thread %s", chunkContext.getStepContext().getStepName(),
					Thread.currentThread().getName()));
			return RepeatStatus.FINISHED;
		}
		
	}
}
