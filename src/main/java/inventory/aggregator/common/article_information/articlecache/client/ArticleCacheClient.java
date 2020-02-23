package inventory.aggregator.common.article_information.articlecache.client;


import inventory.aggregator.articleId.client.article_cache_aaa.document.model.Documents;
import inventory.aggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;

public interface ArticleCacheClient {

	public Documents getArticleUrlPathByEan(String ean, String countryCode, String salesLine, String languageCode) throws Exception;

	public Documents getArticleUrlPathByMmsArtNo(Long mmsArtNo, String countryCode, String salesLine, String languageCode) throws Exception;

	public Documents getArticleUrlPathByArtVarBundle(Long articleNumber, Long variantNumber, Long bundleNumber, String countryCode, String salesLine, String languageCode) throws Exception;

	public Documents getArticleUrlPathBySubsysArtNo(Long subsysArtNo, String countryCode, String salesLine, String languageCode) throws Exception;

	public Documents findArticleUrlPathByMmsArtNo(Long mmsArtNo, String countryCode, String salesLine, String languageCode) throws Exception;

	public ArticleCacheIhdsTree getArticleCacheDocumentByURL(String articleUrlPath);

}
