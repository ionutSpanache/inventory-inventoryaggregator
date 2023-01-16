package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.util.Optional;
import net.metrosystems.inventoryaggregator.articleattributes.model.SupplierInformation;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_aaa.ArticleCacheAAAClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheDataGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SupplierService {

  private static final int DATA_GROUP_ID = 200;
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
      Long commercialSupplier) throws Exception {
    Optional<ArticleCacheIhdsTree> articleCacheIhdsTree =
        articleCacheAAAClient.getSupplierInformation(
            countryCode, salesLine, environment, supplierNumber);
    String generalSupplierName = null;
    if (articleCacheIhdsTree.isPresent()) {
      generalSupplierName = getSupplierNameFromArticleCache(articleCacheIhdsTree.get());
    }

    if (supplierNumber.equals(commercialSupplier)) {
      return SupplierInformation.builder()
          .generalSupplierName(generalSupplierName)
          .originalSupplierName(generalSupplierName)
          .build();
    } else {
      Optional<ArticleCacheIhdsTree> originalSupplier =
          articleCacheAAAClient.getSupplierInformation(
              countryCode, salesLine, environment, commercialSupplier);
      String originalSupplierName = null;
      if (originalSupplier.isPresent())
        originalSupplierName = getSupplierNameFromArticleCache(originalSupplier.get());

      return SupplierInformation.builder()
          .generalSupplierName(generalSupplierName)
          .originalSupplierName(originalSupplierName)
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
}
