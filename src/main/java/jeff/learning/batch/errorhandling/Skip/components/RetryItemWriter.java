package jeff.learning.batch.errorhandling.Skip.components;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class RetryItemWriter implements ItemWriter<String>{

	private boolean retry = false;
	private int attemptCount = 0;


	@Override
	public void write(List<? extends String> items) throws Exception {
		for(String item : items){
			System.out.println("writting item "+item);
			if(retry && item.equalsIgnoreCase("-84")){
				attemptCount++;

				System.out.println("Processing item "+item+" failed");
				throw new CustomRetryableException("process failed. Attempt "+attemptCount);
			}
			else{
				System.out.println(item);
			}
		}
	}

	public void setRetry(boolean retry){this.retry = retry;}
}
