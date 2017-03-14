package jeff.learning.batch.integration.message;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Bean
//	public DirectChannel requests
	
	@Bean
	@StepScope
	public Tasklet tasklet(@Value("#{jobParameters['name']}") String name){
		return (contribution, chunkContext) -> {
			System.out.println(String.format("The job ran for %s", name));
			return RepeatStatus.FINISHED;
		};
	}
	
   @Bean
   public Job job(){
	   return jobBuilderFactory.get("job")
			   .start(stepBuilderFactory.get("step1")
				   		.tasklet(tasklet(null))
			  		 	.build())
			   .build();
   }
}
