package inventory.aggregator.multithreading;

import inventory.aggregator.common.article_information.articlecache.client.ArticleCacheClient;
import inventory.aggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import inventory.aggregator.common.model.ArticleNotFoundException;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class ArticleCacheDocumentServiceTask implements Callable<ArticleCacheIhdsTree> {
	
	private static final Logger logger = Logger.getLogger("ArticleCacheDocumentServiceExecutableTask"); // handler for logging messages
	private ArticleCacheClient articleCacheClient;
	private String articleCacheDocumentURL;
	
	public ArticleCacheDocumentServiceTask(
			ArticleCacheClient articleCacheClient,
			String articleCacheDocumentURL) {
		this.articleCacheClient = articleCacheClient;
		this.articleCacheDocumentURL = articleCacheDocumentURL;
	}
	
	public String getIdAsString() {
		return articleCacheDocumentURL;
	}
	
	// Implementation of the mandatory method for the Callable interface
	// Calls the Article Cache client for the attributes of the current object
	// Returns the enhanced ArticleVariant object
	@Override
	public ArticleCacheIhdsTree call() throws Exception{
		try {
			logger.info("Thread " + this.getIdAsString() + " has started");
			ArticleCacheIhdsTree articleCacheDocument = articleCacheClient.getArticleCacheDocumentByURL(articleCacheDocumentURL);
			return articleCacheDocument;
		} catch(Exception e) {
			throw new ArticleNotFoundException();
		}
	}

}
