package net.metrosystems.inventoryaggregator.articleattributes.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.service.DocumentsService;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleVariantBundleIdentifier;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ArticleComponentRepository {

  private final ArticleCacheService articleCacheService;
  private final DocumentsService documentsService;

  @Async("asyncExecutor")
  public CompletableFuture<Map<ArticleId, ArticleComponent>> getArticleComponentForArticleIdList(
      List<ArticleId> articleIdList, String countryCode, String salesLine, String languageCode)
      throws Exception {
    Map<ArticleId, ArticleComponent> articleComponentMap =
        documentsService.getArticleComponentByArticleIdList(
            countryCode, salesLine, languageCode, articleIdList);
    return CompletableFuture.completedFuture(articleComponentMap);
  }

  private List<ArticleVariantBundleIdentifier>
      getArticleVariantBundleIdentifierListForArticleNumber(
          Long articleNumber, String countryCode, String salesLine, String languageCode)
          throws Exception {
    return articleCacheService.getArticleVariantBundleListForArticleNumber(
        articleNumber, countryCode, salesLine, languageCode);
  }

  private ArticleComponent transformArticleVariantBundleListToArticleComponentByArticle(
      Set<ArticleVariantBundleIdentifier> articleVariantBundleList, Long articleNumber) {
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
