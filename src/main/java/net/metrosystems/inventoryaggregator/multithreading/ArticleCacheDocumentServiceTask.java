package net.metrosystems.inventoryaggregator.multithreading;

import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.ArticleCacheClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;

import java.util.concurrent.Callable;

public class ArticleCacheDocumentServiceTask implements Callable<ArticleCacheIhdsTree> {

	private final ArticleCacheClient articleCacheClient;
	private final String articleCacheDocumentURL;

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
			return articleCacheClient.getArticleCacheDocumentByURL(articleCacheDocumentURL);
		} catch(Exception e) {
			throw new ArticleNotFoundException();
		}
	}

}
