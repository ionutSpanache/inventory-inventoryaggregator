package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client;

import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;

public interface ArticleCacheClient {

  ArticleCacheIhdsTree getArticleCacheDocumentByURL(String articleUrlPath);
}
