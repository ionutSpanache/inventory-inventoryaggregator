package net.metrosystems.inventoryaggregator.articleattributes.service;

import net.metrosystems.inventoryaggregator.articleattributes.model.SupplierInformation;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_aaa.ArticleCacheAAAClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheDataGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SupplierService {

  private static final int DATA_GROUP_ID = 200;
  private static final int SUPPLIER_STORE_DATA_GROUP_ID = 400;
  private final ArticleCacheAAAClient articleCacheAAAClient;

  @Autowired
  public SupplierService(ArticleCacheAAAClient articleCacheAAAClient) {
    this.articleCacheAAAClient = articleCacheAAAClient;
  }

  public SupplierInformation getSupplierInformation(
      String countryCode,
      String salesLine,
      Optional<String> environment,
      Long supplierNumber,
      Long commercialSupplier,
      Optional<Integer> storeNo
  ) throws Exception {
    Optional<ArticleCacheIhdsTree> articleCacheIhdsTree =
        articleCacheAAAClient.getSupplierInformation(
            countryCode, salesLine, environment, supplierNumber);
    String generalSupplierName = null;
    Integer returnCode = null;
    if (articleCacheIhdsTree.isPresent()) {
      generalSupplierName = getSupplierNameFromArticleCache(articleCacheIhdsTree.get());
      returnCode = getReturnCode(articleCacheIhdsTree, storeNo);
    }

    if (supplierNumber.equals(commercialSupplier)) {
      return SupplierInformation.builder()
          .generalSupplierName(generalSupplierName)
          .originalSupplierName(generalSupplierName)
          .returnCode(returnCode)
          .build();
    } else {
      Optional<ArticleCacheIhdsTree> originalSupplier =
          articleCacheAAAClient.getSupplierInformation(
              countryCode, salesLine, environment, commercialSupplier);
      String originalSupplierName = null;
      if (originalSupplier.isPresent()) {
        originalSupplierName = getSupplierNameFromArticleCache(originalSupplier.get());
        returnCode = getReturnCode(articleCacheIhdsTree, storeNo);
      }

      return SupplierInformation.builder()
          .generalSupplierName(generalSupplierName)
          .originalSupplierName(originalSupplierName)
          .returnCode(returnCode)
          .build();
    }
  }

  private String getSupplierNameFromArticleCache(ArticleCacheIhdsTree articleCacheIhdsTree) {
    return articleCacheIhdsTree
        .getTransaction()
        .getArticleCacheIncident()
        .getArticleCacheDataGroup()
        .stream()
        .filter(data -> data.getId() == DATA_GROUP_ID)
        .findFirst()
        .orElse(new ArticleCacheDataGroup())
        .getGeneralSupplierName();
  }

  private Integer getReturnCode(
      Optional<ArticleCacheIhdsTree> optionalArticleCacheIhdsTree, Optional<Integer> storeNo) {

    return optionalArticleCacheIhdsTree
        .map(
            articleCacheIhdsTree ->
                storeNo
                    .map(
                        storeNumber ->
                            articleCacheIhdsTree
                                .getTransaction()
                                .getArticleCacheIncident()
                                .getArticleCacheDataGroup()
                                .stream()
                                .filter(data -> data.getId() == SUPPLIER_STORE_DATA_GROUP_ID)
                                .filter(
                                    articleCacheDataGroup ->
                                        articleCacheDataGroup.getStore().equals(storeNumber))
                                .findFirst()
                                .orElse(new ArticleCacheDataGroup())
                                .getReturnCode())
                    .orElse(null))
        .orElse(null);
  }
}
