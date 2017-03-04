package jeff.learning.batch.output.itemwritter.writter;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class SysOutItemWriter implements ItemWriter<String> {

	@Override
	public void write(List<? extends String> items) throws Exception {
		System.out.println("The size of this chunk is : "+items.size());
		
		//foreach using lambda expressions. In this case it is the implementation for a list.
		items.forEach(item->System.out.println(" >> "+item));
	}

}
