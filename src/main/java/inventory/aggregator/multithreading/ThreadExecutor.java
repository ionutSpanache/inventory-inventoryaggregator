package inventory.aggregator.multithreading;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ThreadExecutor {
	
	private ExecutorService executorService;   // thread pool task executors
	
	public ThreadExecutor() {
		executorService = (ExecutorService) Executors.newCachedThreadPool();
	}
	
	public void submitThread(ThreadResultTask threadResultTask) {
		this.executorService.submit(threadResultTask);
	}
	
	public void submitThread(ArticleCacheDocumentThreadResultTask threadResultTask) {
		this.executorService.submit(threadResultTask);
	}

}
