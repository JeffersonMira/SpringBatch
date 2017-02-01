package jeff.learning.batch.flows.decision.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Bean
	public Step startStep(){
		return stepBuilderFactory.get("StartStep").tasklet(
				(contribution, chunkContext) -> {
					System.out.println("Starting step");
					return RepeatStatus.FINISHED;
				}
		).build();
	}

	@Bean
	public Step evenStep(){
		return stepBuilderFactory.get("EvenStep").tasklet(
				(contribution, chunkContext) -> {
					System.out.println("Even step");
					return RepeatStatus.FINISHED;
				}
		).build();
	}

	@Bean
	public Step oddStep(){
		return stepBuilderFactory.get("OddStep").tasklet(
				(contribution, chunkContext) -> {
					System.out.println("Odd step");
					return RepeatStatus.FINISHED;
				}
		).build();
	}

	@Bean
	public JobExecutionDecider jobExecutionDecider(){
		return new OddDecider();
	}
	
   @Bean
   public Job job(){
	   return jobBuilderFactory.get("job")
			   .start(startStep())
			   .next(jobExecutionDecider())
			   .from(jobExecutionDecider()).on("ODD").to(oddStep())
			   .from(jobExecutionDecider()).on("EVEN").to(evenStep())
			   .from(oddStep()).on("*").to(jobExecutionDecider())
			   .from(jobExecutionDecider()).on("ODD").to(oddStep())
			   .from(jobExecutionDecider()).on("EVEN").to(evenStep())
			   .end()
			   .build();
   }
	
	public static class OddDecider implements JobExecutionDecider{

		private int count =0;

		@Override
		public FlowExecutionStatus decide(JobExecution jobExecution,
										  StepExecution stepExecution) { //last step executed before executiong this step.
			count ++;

			if(count % 2 == 0){
				return new FlowExecutionStatus("EVEN");
			}else{
				return new FlowExecutionStatus("ODD");
			}
		}
	}
}
