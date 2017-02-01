package jeff.learning.batch.flows.listeners;

import jeff.learning.batch.flows.transaction.Main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by dc-user on 2/1/2017.
 */

/*
* Allow the developer to control and manager a more complex flow of execution, acting on the junction points.
* Just neeeds to implement the necessay interface.
* */
@SpringBootApplication
public class MainComponent {
    public static void main(String [] args) {
        SpringApplication.run(MainComponent.class, args);
    }
}
