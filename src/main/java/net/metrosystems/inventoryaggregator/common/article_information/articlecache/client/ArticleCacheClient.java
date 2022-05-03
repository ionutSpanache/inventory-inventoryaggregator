package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client;


import java.util.List;
import java.util.Optional;

import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise.IsuItemStoreMerchandise;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
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

	Documents getProductLifeCycleDocumentUrl(String countryCode, ArticleId articleId);

	Documents getChannelDeliveryFlagUrl(String countryCode, ArticleId articleId);

	IsuItemStoreMerchandise getChannelDeliveryFlagDocument(String articlePath);

	Documents getMultiLanguageDocumentUrl(String countryCode, ArticleId articleId);

	IduItemDerivedStandard getIduItemDerivedStandardDocument(String articlePath);

	List<IduItemDerivedStandard> getMultipleIduItemDerivedStandardDocument(List<String> articlePath);

}
