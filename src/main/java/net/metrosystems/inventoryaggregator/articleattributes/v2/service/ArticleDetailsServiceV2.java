package net.metrosystems.inventoryaggregator.articleattributes.v2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleVariantBundleIdentifier;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
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
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo)
      throws Exception {
    return getRawArticleDataListForArticleVariantBundleIdentifierList(
        articleId, countryCode, salesLine, languageCode, storeNo);
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
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo)
      throws Exception {
    List<RawArticleData> rawArticleDataList = new ArrayList<>();

    ArticleComponent articleComponent =
        transformArticleVariantBundleListToArticleComponentByArticle(
            getArticleVariantBundleIdentifierListForArticleNumber(
                articleId.getArticleNumber(), countryCode, salesLine),
            articleId.getArticleNumber());

      rawArticleDataService.buildRawArticleDataList(
          articleId,
          countryCode,
          salesLine,
          languageCode,
          storeNo,
          rawArticleDataList,
          articleComponent);
    return rawArticleDataList;
  }

  private ArticleComponent transformArticleVariantBundleListToArticleComponentByArticle(
      List<ArticleVariantBundleIdentifier> articleVariantBundleList, Long articleNumber) {
    ArticleComponent articleComponent = new ArticleComponent();
    for (ArticleVariantBundleIdentifier articleVariantBundleIdentifier : articleVariantBundleList) {
      if (articleVariantBundleIdentifier.getArticleNumber().equals(articleNumber)) {
        articleComponent.addVariantBundle(
            articleVariantBundleIdentifier.getVariantNumber(),
            articleVariantBundleIdentifier.getBundleNumber());
      }
    }
    return articleComponent;
  }

  private List<ArticleVariantBundleIdentifier>
  getArticleVariantBundleIdentifierListForArticleNumber(
      Long articleNumber, String countryCode, String salesLine)
      throws Exception {
    return articleCacheService.getArticleVariantBundleListForArticleNumber(
        articleNumber, countryCode, salesLine);
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
          articleIdList,
          countryCode,
          salesLine,
          storeNo,
          rawArticleDataList,
          dataTypeList);
    return rawArticleDataList;
  }


}
