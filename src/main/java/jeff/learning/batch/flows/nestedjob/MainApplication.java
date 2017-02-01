package jeff.learning.batch.flows.nestedjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * Created by dc-user on 2/1/2017.
 */

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class MainApplication {
    public static void main(String [] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
