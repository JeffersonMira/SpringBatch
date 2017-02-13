package jeff.learning.batch.input.fromXml.configuration;

import jeff.learning.batch.input.fromXml.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * Created by dc-user on 2/13/2017.
 */
@Configuration
@EnableBatchProcessing
public class JobConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public StaxEventItemReader<Customer> customerItemReader(){
//        Xstreammarshaller
        //TODO needs to download the dependency on pom.xml in order to be able
        //to use xstreammarshaller.

        return null;
    }

    @Bean
    public ItemWriter<Customer> customerItemWritter(){
        return items -> {
            for(Customer item : items){
                System.out.println(item.toString());
            }
        };
    }

    @Bean
    private Step step1(){
        return stepBuilderFactory.get("step1")
                .<Customer, Customer>chunk(10)
                .reader(null)
                .writer(customerItemWritter())
                .build();
    }

    @Bean
    private Job job1(){
        return jobBuilderFactory.get("job1"+ LocalDateTime.now())
                .start(step1())
                .build();
    }

}
