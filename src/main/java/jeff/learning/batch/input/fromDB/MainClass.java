package jeff.learning.batch.input.fromDB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * There are a lot of ways to read from DB, but the most common is 
 * 1) JDBC cursor item reader;
 * 2) JDBC Page Item reader. 
 * 
 * TODO Page item reader is returning error, just working for the first chunk.
 */ 
@SpringBootApplication
public class MainClass {
	public static void main(String[] args) {
		SpringApplication.run(MainClass.class, args);	
	}
}
