package jeff.learning.batch.processer.composite;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * It is a huge Item processes that is used when you need more than one item - like filtering and a 
 * validation.
 */
@SpringBootApplication
public class MainClass {
	public static void main(String[] args) {
		SpringApplication.run(MainClass.class, args);
	}
	
}
