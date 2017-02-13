package jeff.learning.batch.input.itemReader.reader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class StatelessItemReader implements ItemReader<String> {

	private final Iterator<String> data;
	
	public StatelessItemReader(List<String> data) {
		this.data = data.iterator();
	}
	
	/**
	 * It will execute until read return null. In case it doesn't, it will return f
	 */
	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if(this.data.hasNext()){
			return this.data.next();
		}else{
//			return "one more execution >>>>>>>>";
			return null;
		}
	}

}
