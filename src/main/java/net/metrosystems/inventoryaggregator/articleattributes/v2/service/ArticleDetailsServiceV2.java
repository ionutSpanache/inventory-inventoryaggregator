package net.metrosystems.inventoryaggregator.articleattributes.v2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleVariantBundleIdentifier;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleDetailsServiceV2 {
  private final RawArticleDataService rawArticleDataService;
  private final RawArticleDataListService rawArticleDataListService;
  private final ArticleCacheService articleCacheService;

  public List<RawArticleData> getArticleDataListByArticleVariantBundle(
      ArticleId articleId,
      StoreId storeId,
      String languageCode,
      Optional<List<String>> dataTypeList)
      throws Exception {
    return getRawArticleDataListForArticleVariantBundleIdentifierList(
        articleId, storeId, languageCode, dataTypeList);
  }

  public List<RawArticleData> getArticleDataListByArticleVariantBundle(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      Integer storeNo,
      Optional<List<String>> dataTypeList)
      throws Exception {
    return getRawArticleDataListForArticleVariantBundleIdentifierList(
        articleIdList, countryCode, salesLine, storeNo, dataTypeList);
  }

  private List<RawArticleData> getRawArticleDataListForArticleVariantBundleIdentifierList(
      ArticleId articleId,
      StoreId storeId,
      String languageCode,
      Optional<List<String>> dataTypeList)
      throws Exception {
    List<RawArticleData> rawArticleDataList = new ArrayList<>();


    rawArticleDataService.buildRawArticleDataList(
        articleId,
        storeId,
        languageCode,
        rawArticleDataList,
        dataTypeList);
    return rawArticleDataList;
  }

  private List<RawArticleData> getRawArticleDataListForArticleVariantBundleIdentifierList(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      Integer storeNo,
      Optional<List<String>> dataTypeList)
      throws Exception {
    List<RawArticleData> rawArticleDataList = new ArrayList<>();

    articleIdList.forEach(
        articleId -> {
          try {
            rawArticleDataList.add(RawArticleData.builder().articleId(articleId).build());
          } catch (Exception e) {
            log.error(
                "ArticleListDetailsService.getRawArticleDataListForArticleVariantBundleIdentifierList - articleIdList - error: {}",
                e.getMessage(),
                e);
          }
        });
    rawArticleDataListService.buildRawArticleDataList(
        articleIdList, countryCode, salesLine, storeNo, rawArticleDataList, dataTypeList);
    return rawArticleDataList;
  }
}
