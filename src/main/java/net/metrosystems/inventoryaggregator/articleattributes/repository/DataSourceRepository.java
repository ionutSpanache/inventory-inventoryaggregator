package net.metrosystems.inventoryaggregator.articleattributes.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
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

  public CompletableFuture<Map<ArticleId, ArticleMasterData>> getArticleMasterDataForArticleIdList(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo) {

    UriComponents url =
        UriComponentsBuilder.fromHttpUrl(inventoryHost + dataSourceArticleListDataUrl)
            .buildAndExpand(countryCode, languageCode, storeNo);

    Map<ArticleId, ArticleMasterData> articleIdArticleMasterDataMap =
        webClient
            .post()
            .uri(url.toString())
            .bodyValue(articleIdList)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<ArticleId, ArticleMasterData>>() {})
            .onErrorResume(
                error -> {
                  log.error("Error on retrieving article master data from DataSource ", error);
                  return Mono.empty();
                })
            .block();
    return CompletableFuture.completedFuture(articleIdArticleMasterDataMap);
  }
}
