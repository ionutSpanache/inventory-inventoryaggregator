package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleIdStockLocation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@AllArgsConstructor
public class MstoreAclClientService {

  @Qualifier("webClientForMstoreAcl")
  private final WebClient webClient;

  @Value("${mstore.inventory.host}")
  private String inventoryHost;

  @Value("${mstore.inventory.mstoreacl.stockLocation.articlelist.url}")
  private String stockLocationArticleListDataUrl;

  @Value("${mstore.inventory.mstoreacl.stockLocation.article.url}")
  private String stockLocationArticleDataUrl;

  public List<ArticleStockLocation> getArticleStockLocation(
      ArticleId articleId, String countryCode, Integer storeNumber) {
    UriComponents url =
        UriComponentsBuilder.fromHttpUrl(inventoryHost + stockLocationArticleDataUrl)
            .buildAndExpand(
                countryCode,
                storeNumber,
                articleId.getArticleNumber(),
                articleId.getVariantNumber(),
                articleId.getBundleNumber());

    ArticleIdStockLocation articleStockLocation =
        webClient
            .get()
            .uri(url.toString())
            .retrieve()
            .bodyToMono(ArticleIdStockLocation.class)
            .onErrorResume(
                error -> {
                  if(error instanceof WebClientResponseException webClientResponseException && HttpStatus.NOT_FOUND != webClientResponseException.getStatusCode()){
                    log.error(
                        "Error on retrieving article stock Location for ArticleList from DataSource. Error={} ",
                        error.getMessage(),
                        error);
                  }
                  return Mono.empty();
                })
            .block();
    return (!ObjectUtils.isEmpty(articleStockLocation))
        ? articleStockLocation.getArticleStockLocationList()
        : Collections.emptyList();
  }

  public List<ArticleIdStockLocation> getArticleStockLocationForArticleList(
      List<ArticleId> articleIdList, String countryCode, Integer storeNo) {
    UriComponents url =
        UriComponentsBuilder.fromHttpUrl(inventoryHost + stockLocationArticleListDataUrl)
            .buildAndExpand(countryCode, storeNo);
    return webClient
        .post()
        .uri(url.toString())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(articleIdList)
        .retrieve()
        .bodyToFlux(ArticleIdStockLocation.class)
        .collectList()
        .onErrorResume(
            error -> {
              if(error instanceof WebClientResponseException webClientResponseException && HttpStatus.NOT_FOUND != webClientResponseException.getStatusCode()){
                log.error(
                    "Error on retrieving article stock Location for ArticleList from DataSource. Error={} ",
                    error.getMessage(),
                    error);
              }
              return Mono.empty();
            })
        .block();
  }
}
