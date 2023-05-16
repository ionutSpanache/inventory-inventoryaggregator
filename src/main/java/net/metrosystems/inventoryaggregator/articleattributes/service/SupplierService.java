package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import net.metrosystems.inventoryaggregator.articleattributes.model.SupplierInformation;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_aaa.ArticleCacheAAAClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheDataGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;

import net.metrosystems.inventoryaggregator.common.countryconfiguration.CountryProperties;
import net.metrosystems.inventoryaggregator.common.countryconfiguration.model.CountrySalesLineCode;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SupplierService {

  private static final int DATA_GROUP_ID = 200;
  private static final int SUPPLIER_STORE_DATA_GROUP_ID = 400;

  private static final int SUPPLIER_SALES_LINE_DATA = 300;
  private final ArticleCacheAAAClient articleCacheAAAClient;
  private final CountryProperties countryProperties;

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
    Integer goodsReceivingDetailedCheckRequirement  = null;
    boolean isPlatformSupplier = false;

    if (articleCacheIhdsTree.isPresent()) {
      generalSupplierName = getSupplierNameFromArticleCache(articleCacheIhdsTree.get());
      returnCode = getReturnCode(articleCacheIhdsTree, storeNo);
      goodsReceivingDetailedCheckRequirement = getGoodsEntryDetailCheck(articleCacheIhdsTree, storeNo);
      isPlatformSupplier = isWarehouseSupplier(articleCacheIhdsTree.get(), countryCode, salesLine);
    }

    if (supplierNumber.equals(commercialSupplier)) {
      return SupplierInformation.builder()
          .generalSupplierName(generalSupplierName)
          .originalSupplierName(generalSupplierName)
          .returnCode(returnCode)
          .goodsReceivingDetailedCheckRequirement(goodsReceivingDetailedCheckRequirement)
          .isPlatformSupplier(isPlatformSupplier)
          .build();
    } else {
      Optional<ArticleCacheIhdsTree> originalSupplier =
          articleCacheAAAClient.getSupplierInformation(
              countryCode, salesLine, environment, commercialSupplier);
      String originalSupplierName = null;
      if (originalSupplier.isPresent()) {
        originalSupplierName = getSupplierNameFromArticleCache(originalSupplier.get());
        returnCode = getReturnCode(articleCacheIhdsTree, storeNo);
        goodsReceivingDetailedCheckRequirement = getGoodsEntryDetailCheck(articleCacheIhdsTree, storeNo);
      }

      return SupplierInformation.builder()
          .generalSupplierName(generalSupplierName)
          .originalSupplierName(originalSupplierName)
          .returnCode(returnCode)
          .goodsReceivingDetailedCheckRequirement(goodsReceivingDetailedCheckRequirement)
          .isPlatformSupplier(isPlatformSupplier)
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

  private Integer getGoodsEntryDetailCheck(
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
                                .getGoodsEntryDetailCheck())
                    .orElse(null))
        .orElse(null);
  }

  private boolean isWarehouseSupplier(
      ArticleCacheIhdsTree articleCacheIhdsTree, String countryCode, String salesLine) {
    Stream<ArticleCacheDataGroup> supplierSalesLineData =
        articleCacheIhdsTree
            .getTransaction()
            .getArticleCacheIncident()
            .getArticleCacheDataGroup()
            .stream()
            .filter(data -> data.getId() == SUPPLIER_SALES_LINE_DATA);

    Optional<CountrySalesLineCode> countrySalesLineCode =
        countryProperties.getCountrySalesLineCode(countryCode, salesLine);

    if (countrySalesLineCode.isPresent()) {
      supplierSalesLineData =
          supplierSalesLineData.filter(
              data ->
                  Objects.equals(
                      countrySalesLineCode.get().getSalesLineCode(), data.getSalesLine()));
    }
    return supplierSalesLineData
        .findFirst()
        .orElse(new ArticleCacheDataGroup())
        .getWarehouseSupplierFlag()
        .equals(1);
  }
}
