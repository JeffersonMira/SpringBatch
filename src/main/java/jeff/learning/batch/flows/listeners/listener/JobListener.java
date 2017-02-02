package jeff.learning.batch.flows.listeners.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * Created by dc-user on 2/1/2017.
 */
public class JobListener implements JobExecutionListener {

	
    @Override
    public void beforeJob(JobExecution jobExecution) {
    	String jobName = jobExecution.getJobInstance().getJobName();
    	
    	System.out.println(">> Starting "+jobName);
    	
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
    	String jobName = jobExecution.getJobInstance().getJobName();
    	
    	System.out.println("<< Completing "+jobName);
    }
}
