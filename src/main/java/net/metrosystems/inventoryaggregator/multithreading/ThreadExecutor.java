package net.metrosystems.inventoryaggregator.multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Component;

@Component
public class ThreadExecutor {

	private final ExecutorService executorService;   // thread pool task executors

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
