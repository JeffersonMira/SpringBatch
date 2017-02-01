package jeff.learning.batch.flows.nestedjob.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dc-user on 2/1/2017.
 */
@Configuration
@EnableBatchProcessing
public class ChildJobConfiguration {

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Bean
    public Step step1a(){
        return stepBuilderFactory.get("step1a").tasklet(
                ((contribution, chunkContext) -> {
                    System.out.println(">> This is the step 1a");
                    return RepeatStatus.FINISHED;
                })
        ).build();
    }

    @Bean
    public Job childJob(){
        return jobBuilderFactory.get("childJobFactory").
                start(step1a())
                .build();
    }
}
