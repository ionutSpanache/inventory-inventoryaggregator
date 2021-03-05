package net.metrosystems.inventoryaggregator.multithreading;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleVariant;

//Extends the FutureTask class parameterized with a list of MovieAdapter objects
public class ThreadResultTask extends FutureTask<ArticleVariant> {

	private static final Logger logger = Logger.getLogger("ThreadResultTask");      // handler for logging messages
	private String name;                                                            // the name of the task
	private final Integer articleVariantIndex;                                            // the index of the variant in the list

	// Implement the constructor of the class
	// It has to receive a Callable object as a parameter
	public ThreadResultTask(
			Callable<ArticleVariant> callable,
			Integer articleVariantIndex) {
		super(callable);
		try {
			this.name = "MovieServiceThreadResultTask: " + ((MovieServiceExecutableTask) callable).getIdAsString();
		}catch(Exception e1) {
					try {
						this.name = "AclStockLocationThreadResultTask: " + ((AclStockLocationServiceExecutableTask) callable).getIdAsString();
					}catch (Exception e4) {
						try {
							this.name = "AclArticleStoreMasterDataThreadResultTask: " + ((AclArticleStoreMasterDataServiceExecutableTask) callable).getIdAsString();
						}catch(Exception e5){
							this.name = "Unknown task";
						}
					}
		}
		this.articleVariantIndex = articleVariantIndex;
	}

	// Getters section
	public Integer getArticleVariantIndex() {
		return articleVariantIndex;
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
