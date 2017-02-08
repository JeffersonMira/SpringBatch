package jeff.learning.batch.flows.jobparameters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * 
 * In this job, the parameters are goning to be passed on the run. like, 
 * java -jar target/job-parameters-0.0.1-SNAPSHOT.jar message=hello!
 * 
 * However, if I try the same call using the same parameter I'm going to receive an error.
 * It happens to prevent calling the same job. 
 * It also happens due to restartability */
@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}
}
