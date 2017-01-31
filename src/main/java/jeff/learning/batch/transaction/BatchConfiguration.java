package jeff.learning.batch.transaction;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /*
    * Represents an specific state.
    * There are two types:
    *   - tasklet : simpler step that is just one process,
    *   - chunk : there are three parts of it: Item Read, Item process and Item write.
    *
    * */
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(new Tasklet() {
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
                        System.out.print(">> Execution step 1!!");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
    
    @Bean
    public Step step2() {
    	
    	//Using lambda expressions ->  It basically means: 
    	//"Create an object that supply the needs for this call (which is a Tasklet object in this case)
    	// and as I know the constructor needs two parameters, here follows the names of it!"
    	//Then lambda already finds what are the classes and whatever it is needed.
    	//If no parameter is needed for this constructor, just passing an "() -> {//someCodeHere}" is enough. 
        return stepBuilderFactory.get("step2")
                .tasklet((contribution, chunkContext)  -> {
                        System.out.print(">> Execution step 2!!");
                        return RepeatStatus.FINISHED;
                    }
                )
                .build();
    }
    
    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
                .tasklet(new Tasklet() {
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
                        System.out.print(">> Execution step 3!!");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }


    /*
    * Represents a group of states (that are the steps) and the iteration between them.
    * */
    @Bean
    public Job job(Step step1) throws Exception {
        return goToOtherOnStatus();
//    	return linearFlow(step1);
    }
    
    //Simplier job execution - first, then second and so on.
    private Job linearFlow(Step step1) throws Exception{
    	 return jobBuilderFactory.get("transitionJobNext1")
                 .incrementer(new RunIdIncrementer())  // Not really necessary for a hello world
                 .start(step1())
                 .next(step2())
                 .next(step3())
                 .next(step2())
                 .build();
    }
    
    //It is also possible to create a flow, for some specific status. D
    private Job goToOtherOnStatus(){
    	return jobBuilderFactory.get("transitionJobNext2")
    			.start(step1())
    			.on("COMPLETED").to(step2())
//    			.from(step2()).on("COMPLETED").stopAndRestart(step3()) //dois 'runs' vão acontecer neste caso
    			.from(step2()).on("COMPLETED").to(step3())
    			.from(step3()).end()
    			.build();
    }
    
}