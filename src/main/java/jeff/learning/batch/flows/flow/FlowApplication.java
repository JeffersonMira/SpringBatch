package jeff.learning.batch.flows.flow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/* 
 * Flow is made of a group of steps or other flows. 
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class FlowApplication {
	public static void main(String[] args) {
		SpringApplication.run(FlowApplication.class, args);
	}
}
