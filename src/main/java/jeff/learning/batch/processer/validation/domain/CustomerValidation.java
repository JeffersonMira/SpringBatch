package jeff.learning.batch.processer.validation.domain;

import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

public class CustomerValidation implements Validator<Customer>{

	@Override
	public void validate(Customer value) throws ValidationException {
		if(value.getFirstName().toUpperCase().startsWith("A")){
			throw new ValidationException("Items with first name Starting with 'A' are invalid : "+value);
		}
	}

}
