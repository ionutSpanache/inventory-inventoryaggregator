package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client;


import java.util.Optional;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;

public interface ArticleCacheClient {

	Documents getArticleUrlPathByEan(String ean, String countryCode, String salesLine, String languageCode) throws Exception;

	Documents getArticleUrlPathByMmsArtNo(Long mmsArtNo, String countryCode, String salesLine, String languageCode) throws Exception;

	Documents getArticleUrlPathByArtVarBundle(Long articleNumber, Long variantNumber, Long bundleNumber, String countryCode,
      String salesLine, String languageCode) throws Exception;

	Documents getArticleUrlPathBySubsysArtNo(Long subsysArtNo, String countryCode, String salesLine, String languageCode) throws Exception;

	Documents findArticleUrlPathByMmsArtNo(Long mmsArtNo, String countryCode, String salesLine, String languageCode) throws Exception;

	ArticleCacheIhdsTree getArticleCacheDocumentByURL(String articleUrlPath);

	Optional<ArticleCacheIhdsTree> getSupplierInformation(String countryCode, String salesLine, Optional<String> environment, Long supplierNumber);

}
