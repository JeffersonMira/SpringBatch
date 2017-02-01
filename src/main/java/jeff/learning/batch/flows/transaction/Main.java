package jeff.learning.batch.flows.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


/*
* There are two different job repositories - in memory and JDBC.
* AS it is not specified the job repository, it is suppose to use the in memory one (which is not
* thread safe and not recommended to be used on a 'production like' environment).
*
* #Good information about Spring Batch - http://malsolo.com/blog4java/?p=260
* */
@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})  //Needs it to run and use in memory db
public class Main {
    public static void main(String [] args) {
//        System.exit(SpringApplication.exit(SpringApplication.run( BatchConfiguration.class, args)));
        SpringApplication.run(Main.class, args);

    }
}