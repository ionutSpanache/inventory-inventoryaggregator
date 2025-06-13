package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client;


import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_promotion_article.PromotionArticle;

public interface ArticleCacheClient {


	Documents getArticleUrlPathByArtVarBundle(Long articleNumber, Long variantNumber, Long bundleNumber, String countryCode,
      String salesLine) throws Exception;


  ArticleCacheIhdsTree getArticleCacheDocumentByURL(String articleUrlPath);


	IduItemDerivedStandard getIduItemDerivedStandardDocument(String articlePath);
	


	PromotionArticle getPromotionArticle(String articleUrlPath);

}
