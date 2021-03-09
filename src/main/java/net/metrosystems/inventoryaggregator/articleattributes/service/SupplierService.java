package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.util.Optional;
import net.metrosystems.inventoryaggregator.articleattributes.model.SupplierInformation;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_aaa.ArticleCacheAAAClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SupplierService {

  private static final int dataGroupId = 200;
  private ArticleCacheAAAClient articleCacheAAAClient;

  @Autowired
  public SupplierService(ArticleCacheAAAClient articleCacheAAAClient) {
    this.articleCacheAAAClient = articleCacheAAAClient;
  }

  public SupplierInformation getSupplierInformation(String countryCode, String salesLine, Optional<String> environment,
      Long supplierNumber, Long commercialSupplier) {
    ArticleCacheIhdsTree articleCacheIhdsTree = articleCacheAAAClient
        .getSupplierInformation(countryCode, salesLine, environment, supplierNumber);
    String generalSupplierName = articleCacheIhdsTree.getTransaction().getArticleCacheIncident().getArticleCacheDataGroup()
        .stream().filter(data -> data.getId() == dataGroupId).findFirst().get().getGeneralSupplierName();

    if (supplierNumber == commercialSupplier) {
      return SupplierInformation.builder()
          .generalSupplierName(generalSupplierName)
          .originalSupplierName(generalSupplierName)
          .build();
    } else {
      ArticleCacheIhdsTree originalSupplier = articleCacheAAAClient
          .getSupplierInformation(countryCode, salesLine, environment, commercialSupplier);
      String originalSupplierName = originalSupplier.getTransaction().getArticleCacheIncident().getArticleCacheDataGroup()
          .stream().filter(data -> data.getId() == dataGroupId).findFirst().get().getGeneralSupplierName();

      return SupplierInformation.builder()
          .generalSupplierName(generalSupplierName)
          .originalSupplierName(originalSupplierName)
          .build();
    }

  }


}
