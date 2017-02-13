package jeff.learning.batch.flows.nestedjob.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Created by dc-user on 2/1/2017.
 */
@Configuration
@EnableBatchProcessing
public class ParentJobConfiguration {

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public Job childJob;

    @Autowired
    public JobLauncher jobLauncher;

    @Bean
    public Step ste1(){
        return stepBuilderFactory.get(">> This is step 1").tasklet(
                (contribution, chunkContext) -> {
                    return RepeatStatus.FINISHED;
                }
        ).build();
    }

    @Bean
    public Job parentJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        Step child = new JobStepBuilder(new StepBuilder("childJobStep"))
                .job(childJob)
                .launcher(jobLauncher)
                .transactionManager(platformTransactionManager)
                .build();

        return jobBuilderFactory.get("parentJob")
                .start(ste1())
                .next(child)
                .build();
    }
}
