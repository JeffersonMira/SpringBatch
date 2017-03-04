package jeff.learning.batch.processer.itemprocessor.processor;

import org.springframework.batch.item.ItemProcessor;

import jeff.learning.batch.processer.itemprocessor.domain.Customer;

/**First is the item to be received and then what is going to be returned.*/
public class UpperCaseItemProcessor implements ItemProcessor<Customer, Customer>{

	/**It returns a new object, because the same object could be processed more than one time
	 * (depending on the strategy). So it will not accumulate results.*/
	@Override
	public Customer process(Customer item) throws Exception {
		return new Customer(item.getId(), 
				item.getFirstName().toUpperCase(), 
				item.getLastName().toUpperCase(), 
				item.getBirthdate());
	}

}
