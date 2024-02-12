package net.metrosystems.inventoryaggregator.articlemasterdata.client;

import static java.util.stream.Collectors.toMap;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleIdComponent;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataSourceClient {
  private final WebClient webClient;

  @Value("${mstore.inventory.host}")
  private String inventoryHost;

  @Value("${mstore.inventory.datasource.articlelistdata.url}")
  private String dataSourceArticleListDataUrl;

  @Value("${mstore.inventory.datasource.articledata.url}")
  private String dataSourceArticleDataUrl;

  @Value("${mstore.inventory.datasource.articlecomponent.url}")
  private String dataSourceArticleComponentUrl;

  @Value("${mstore.inventory.datasource.articlecomponents.url}")
  private String dataSourceArticleComponentsUrl;

  public Map<ArticleId, ArticleMasterData> getMapArticleMasterDataForArticleIdList(
      List<ArticleId> articleIdList, String countryCode, String languageCode, Integer storeNo) {

    List<ArticleMasterId> articleMasterIds =
        getArticleMasterDataForArticleIdList(articleIdList, countryCode, languageCode, storeNo);

    Map<ArticleId, ArticleMasterData> articleIdArticleMasterDataMap = new HashMap<>();
    if (!isEmpty(articleMasterIds)) {
      articleMasterIds.forEach(
          articleMasterId ->
              articleIdArticleMasterDataMap.put(
                  articleMasterId.getArticleId(), articleMasterId.getArticleMasterData()));
    }

    return articleIdArticleMasterDataMap;
  }

  public List<ArticleMasterId> getArticleMasterDataForArticleIdList(
      List<ArticleId> articleIdList, String countryCode, String languageCode, Integer storeNo) {

    UriComponents url =
        UriComponentsBuilder.fromHttpUrl(inventoryHost + dataSourceArticleListDataUrl)
            .buildAndExpand(countryCode, languageCode, storeNo);

    return webClient
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
  }

  public ArticleMasterId getArticleMasterIdForArticleId(
      ArticleId articleId, String countryCode, String languageCode, Integer storeNo) {

    UriComponents url =
        UriComponentsBuilder.fromHttpUrl(inventoryHost + dataSourceArticleDataUrl)
            .buildAndExpand(
                countryCode,
                languageCode,
                storeNo,
                articleId.getArticleNumber(),
                articleId.getVariantNumber(),
                articleId.getBundleNumber());

    return webClient
        .get()
        .uri(url.toString())
        .retrieve()
        .onStatus(
            httpStatus -> httpStatus == HttpStatus.NOT_FOUND,
            error -> Mono.error(new ArticleNotFoundException("Article not found in local cache")))
        .bodyToMono(ArticleMasterId.class)
        .onErrorResume(
            error -> {
              if (error instanceof ArticleNotFoundException e) {
                throw e;
              }
              log.error("Error on retrieving article master data from DataSource ", error);
              return Mono.empty();
            })
        .block();
  }

  public ArticleComponent getArticleComponent(
      String countryCode, Integer storeNumber, Long articleNumber) {

    UriComponents url =
        UriComponentsBuilder.fromHttpUrl(inventoryHost + dataSourceArticleComponentUrl)
            .buildAndExpand(countryCode, storeNumber, articleNumber);

    return webClient
        .get()
        .uri(url.toString())
        .retrieve()
        .bodyToMono(ArticleComponent.class)
        .onErrorResume(
            error -> {
              log.error("Error on retrieving article component from DataSource ", error);
              return Mono.empty();
            })
        .block();
  }

  public Map<ArticleId, ArticleComponent> getArticleComponents(
      String countryCode, Integer storeNumber, List<ArticleId> articleIds) {

    UriComponents url =
        UriComponentsBuilder.fromHttpUrl(inventoryHost + dataSourceArticleComponentsUrl)
            .buildAndExpand(countryCode, storeNumber);

    List<ArticleIdComponent> articleIdComponents =
        webClient
            .post()
            .uri(url.toString())
            .bodyValue(articleIds)
            .retrieve()
            .bodyToFlux(ArticleIdComponent.class)
            .collectList()
            .onErrorResume(
                error -> {
                  log.error("Error on retrieving article component from DataSource ", error);
                  return Mono.empty();
                })
            .block();

    Map<ArticleId, ArticleComponent> articleComponentMap = new HashMap<>();
    if (!isEmpty(articleIdComponents)) {
      articleComponentMap =
          articleIdComponents.stream()
              .collect(
                  toMap(ArticleIdComponent::getArticleId, ArticleIdComponent::getArticleComponent));
    }
    return articleComponentMap;
  }
}
