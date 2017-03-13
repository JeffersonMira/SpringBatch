package jeff.learning.batch.errorhandling.Skip.components;

import org.springframework.batch.item.ItemProcessor;

public class RetryItemProcessor implements ItemProcessor<String, String>{

	private boolean retry = false;
	private int attemptCount = 0;
	
	
	@Override
	public String process(String item) throws Exception {
		System.out.println(">>> processing item "+item);
		if(retry && item.equalsIgnoreCase("42")){
			attemptCount++;
				
			System.out.println("Processing item "+item+" failed");
			throw new CustomRetryableException("process failed. Attempt "+attemptCount);
		}
		else{
			return String.valueOf(Integer.valueOf(item)* -1);
		}
	}

	public void setRetry(boolean retry){this.retry = retry;}
}
