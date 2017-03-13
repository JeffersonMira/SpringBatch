package jeff.learning.batch.errorhandling.restart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * By default, if there is any uncaught exception happens, the job stops. Then
 * the job should be able to restart in the same point where it stopped.
 *  */
@SpringBootApplication
public class MainClass {
	public static void main(String[] args) {
		SpringApplication.run(MainClass.class, args);
	}
	
}
