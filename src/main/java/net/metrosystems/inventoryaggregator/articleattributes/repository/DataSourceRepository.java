package net.metrosystems.inventoryaggregator.articleattributes.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataSourceRepository {
  private final WebClient webClient;

  @Value("${mstore.inventory.host}")
  private String inventoryHost;

  @Value("${mstore.inventory.datasource.articlelistdata.url}")
  private String dataSourceArticleListDataUrl;

  @Value("${mstore.inventory.datasource.articledata.url}")
  private String dataSourceArticleDataUrl;

  public CompletableFuture<Map<ArticleId, ArticleMasterData>> getArticleMasterDataForArticleIdList(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo) {

    UriComponents url =
        UriComponentsBuilder.fromHttpUrl(inventoryHost + dataSourceArticleListDataUrl)
            .buildAndExpand(countryCode, languageCode, storeNo);

    List<ArticleMasterId> articleMasterIds =
        webClient
            .post()
            .uri(url.toString())
            .bodyValue(articleIdList)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<ArticleMasterId>>() {})
            .onErrorResume(
                error -> {
                  log.error("Error on retrieving article master data from DataSource ", error);
                  return Mono.empty();
                })
            .block();

    Map<ArticleId, ArticleMasterData> articleIdArticleMasterDataMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(articleMasterIds)) {
      articleMasterIds.forEach(
          articleMasterId ->
              articleIdArticleMasterDataMap.put(
                  articleMasterId.getArticleId(), articleMasterId.getArticleMasterData()));
    }

    return CompletableFuture.completedFuture(articleIdArticleMasterDataMap);
  }

  public CompletableFuture<ArticleMasterId> getArticleMasterIdForArticleId(
      ArticleId articleId,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo) {

    UriComponents url =
        UriComponentsBuilder.fromHttpUrl(inventoryHost + dataSourceArticleDataUrl)
            .buildAndExpand(
                countryCode,
                languageCode,
                storeNo,
                articleId.getArticleNumber(),
                articleId.getVariantNumber(),
                articleId.getBundleNumber());

    ArticleMasterId articleMasterId =
        webClient
            .get()
            .uri(url.toString())
            .retrieve()
            .bodyToMono(ArticleMasterId.class)
            .onErrorResume(
                error -> {
                  log.error("Error on retrieving article master data from DataSource ", error);
                  return Mono.empty();
                })
            .block();

    return CompletableFuture.completedFuture(articleMasterId);
  }
}
