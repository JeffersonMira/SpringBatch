package jeff.learning.batch.processer.composite.domain;


import org.springframework.batch.item.ItemProcessor;

public class FilteringItemProcessing implements ItemProcessor<Customer, Customer>{

	@Override
	public Customer process(Customer item) throws Exception {
		
		if(item.getId() % 2 == 0){
			return null;
		}else{
			return item;
		}
		
	}

}
