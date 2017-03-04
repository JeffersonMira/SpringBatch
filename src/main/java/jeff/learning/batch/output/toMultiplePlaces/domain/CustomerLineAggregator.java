package jeff.learning.batch.output.toMultiplePlaces.domain;

import java.io.IOException;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.batch.item.file.transform.LineAggregator;

/** 
 * To convert to JSON it is going to use JACKSON:
 * https://www.tutorialspoint.com/jackson/jackson_objectmapper.htm
 *
 */
public class CustomerLineAggregator implements LineAggregator<Customer>{

	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public String aggregate(Customer item) {
		try{
			
			return objectMapper.writeValueAsString(item); // this is the guy who transform it to json
			
			
		}catch (JsonProcessingException e) {
			throw new RuntimeException("not able to serialize object", e);
		} catch (IOException e) {
			throw new RuntimeException("not able to serialize object", e);
		}
	}
	
}
