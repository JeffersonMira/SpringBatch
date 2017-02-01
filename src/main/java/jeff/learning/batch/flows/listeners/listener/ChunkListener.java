package jeff.learning.batch.flows.listeners.listener;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

/**
 * Created by dc-user on 2/1/2017.
 */
public class ChunkListener {
    @BeforeChunk
    public void beforeChunk(ChunkContext context){
        System.out.println(">> Before the chunk");
    }

    @AfterChunk
    public void afterChunk(ChunkContext context){
        System.out.println("<< After the chunk");
    }
}
