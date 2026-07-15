package net.metrosystems.inventoryaggregator.articlemasterdata.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataSourceClient {
  public static final String DATA_TYPE = "dataType";

  private final WebClient webClientMstoreSecured;

  @Value("${mstore.inventory.datasource.host}")
  private String dataSourceHost;

  @Value("${mstore.inventory.datasource.articlelistdata.url}")
  private String dataSourceArticleListDataUrl;

  @Value("${mstore.inventory.datasource.articledata.url}")
  private String dataSourceArticleDataUrl;

  @Value("${mstore.inventory.datasource.articlecomponent.url}")
  private String dataSourceArticleComponentUrl;

  @Value("${mstore.inventory.datasource.articlecomponents.url}")
  private String dataSourceArticleComponentsUrl;

  public Map<ArticleId, ArticleMasterId> getArticleMasterIdByArticleId(
      List<ArticleId> articleIdList,
      String countryCode,
      String languageCode,
      Integer storeNo,
      List<String> dataTypeList) {

    List<ArticleMasterId> articleMasterIds =
        getArticleMasterDataForArticleIdList(
            articleIdList, countryCode, languageCode, storeNo, dataTypeList);

    Map<ArticleId, ArticleMasterId> articleIdArticleMasterDataMap = new HashMap<>();
    if (!isEmpty(articleMasterIds)) {
      articleMasterIds.forEach(
          articleMasterId ->
              articleIdArticleMasterDataMap.put(articleMasterId.getArticleId(), articleMasterId));
    }

    return articleIdArticleMasterDataMap;
  }

  public List<ArticleMasterId> getArticleMasterDataForArticleIdList(
      List<ArticleId> articleIdList,
      String countryCode,
      String languageCode,
      Integer storeNo,
      List<String> dataTypeList) {

    UriComponentsBuilder uriComponentsBuilder =
        UriComponentsBuilder.fromUriString(dataSourceHost + dataSourceArticleListDataUrl);

    if (!isEmpty(dataTypeList)) {
      for (String dataType : dataTypeList) {
        uriComponentsBuilder.queryParam(DATA_TYPE, escapeHtml4(dataType));
      }
    }

    UriComponents url = uriComponentsBuilder.buildAndExpand(countryCode, languageCode, storeNo);

    return webClientMstoreSecured
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
      ArticleId articleId,
      String countryCode,
      String languageCode,
      Integer storeNo,
      List<String> dataTypeList) {

    UriComponentsBuilder uriComponentsBuilder =
        UriComponentsBuilder.fromUriString(dataSourceHost + dataSourceArticleDataUrl);

    if (!isEmpty(dataTypeList)) {
      for (String dataType : dataTypeList) {
        uriComponentsBuilder.queryParam(DATA_TYPE, escapeHtml4(dataType));
      }
    }

    UriComponents url =
        uriComponentsBuilder.buildAndExpand(
            countryCode,
            languageCode,
            storeNo,
            articleId.getArticleNumber(),
            articleId.getVariantNumber(),
            articleId.getBundleNumber());

    return webClientMstoreSecured
        .get()
        .uri(url.toString())
        .retrieve()
        .onStatus(
            httpStatus -> httpStatus == HttpStatus.NOT_FOUND,
            _ -> Mono.error(new ArticleNotFoundException("Article not found in local cache")))
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
        UriComponentsBuilder.fromUriString(dataSourceHost + dataSourceArticleComponentUrl)
            .buildAndExpand(countryCode, storeNumber, articleNumber);

    return webClientMstoreSecured
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
        UriComponentsBuilder.fromUriString(dataSourceHost + dataSourceArticleComponentsUrl)
            .buildAndExpand(countryCode, storeNumber);

    List<ArticleIdComponent> articleIdComponents =
        webClientMstoreSecured
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
