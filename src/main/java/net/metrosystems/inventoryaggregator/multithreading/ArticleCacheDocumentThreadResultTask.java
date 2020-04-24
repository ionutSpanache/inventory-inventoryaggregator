package net.metrosystems.inventoryaggregator.multithreading;

import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

//Extends the FutureTask class parameterized with a list of MovieAdapter objects
public class ArticleCacheDocumentThreadResultTask extends FutureTask<ArticleCacheIhdsTree> {
	
	private static final Logger logger = Logger.getLogger("ArticleCacheDocumentThreadResultTask");      // handler for logging messages
	private String name;
	
	// Implement the constructor of the class
	// It has to receive a Callable object as a parameter
	public ArticleCacheDocumentThreadResultTask(
			Callable<ArticleCacheIhdsTree> callable) {
		super(callable);
		this.name = "ArticleCacheDocumentThreadResultTask: " + ((ArticleCacheDocumentServiceTask) callable).getIdAsString();
	}
	
	// Actions to be executed when the current task is finalized
	// Called automatically by the FutureTask class
	@Override
	protected void done() {
		 if (this.isCancelled()) {
			 logger.info(this.name + ": has been canceled");
		 } else if (this.isDone()) {
			 logger.info(this.name + ": has finished");
		 }
	}

}

