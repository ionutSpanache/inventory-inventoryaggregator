package net.metrosystems.inventoryaggregator.articleattributes.repository;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleComponentForArticleId;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleVariantBundleIdentifier;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ArticleComponentRepository {

  private final ArticleCacheService articleCacheService;

  @Qualifier("ForkJoinCustomPool")
  private final ForkJoinPool forkJoinPoolCustom;

  @Async("asyncExecutor")
  public CompletableFuture<List<ArticleComponentForArticleId>> getArticleComponentForArticleIdList(
      List<ArticleId> articleIdList, String countryCode, String salesLine, String languageCode) throws Exception {
    List<ArticleComponentForArticleId> articleComponentForArticleIdList = forkJoinPoolCustom
        .submit(() -> articleIdList.parallelStream()
                        .map(
                            articleId -> {
                              List<ArticleVariantBundleIdentifier>
                                  articleVariantBundleIdentifierList;
                              try {
                                articleVariantBundleIdentifierList =
                                    getArticleVariantBundleIdentifierListForArticleNumber(
                                        articleId.getArticleNumber(),
                                        countryCode,
                                        salesLine,
                                        languageCode);
                              } catch (Exception e) {
                                log.error(
                                    "ArticleComponentRepository.getArticleComponentForArticleIdList error: {}",
                                    e.getMessage(),
                                    e);
                                return null;
                              }
                              return ArticleComponentForArticleId.builder()
                                  .articleId(articleId)
                                  .articleComponent(
                                      transformArticleVariantBundleListToArticleComponentByArticle(
                                          articleVariantBundleIdentifierList,
                                          articleId.getArticleNumber()))
                                  .build();
                            }).filter(Objects::nonNull)
                        .collect(Collectors.toList()))
            .get();
    return CompletableFuture.completedFuture(articleComponentForArticleIdList);
  }

  private List<ArticleVariantBundleIdentifier>
      getArticleVariantBundleIdentifierListForArticleNumber(
          Long articleNumber, String countryCode, String salesLine, String languageCode)
          throws Exception {
    return articleCacheService.getArticleVariantBundleListForArticleNumber(
        articleNumber, countryCode, salesLine, languageCode);
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

}
