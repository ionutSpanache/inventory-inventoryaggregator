package net.metrosystems.inventoryaggregator.stocklocation.service;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.stocklocation.model.ArticleStockLocations;
import net.metrosystems.inventoryaggregator.stocklocation.model.StockLocation;
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
public class StockLocationClientService {

  @Qualifier("webClientForMstoreAcl")
  private final WebClient webClient;

  @Value("${mstore.inventory.mstoreacl.host}")
  private String mStoreAclHost;

  @Value("${mstore.inventory.mstoreacl.stockLocation.articlelist.url}")
  private String stockLocationArticleListDataUrl;

  @Value("${mstore.inventory.mstoreacl.stockLocation.article.url}")
  private String stockLocationArticleDataUrl;

  public List<StockLocation> getArticleStockLocation(
      ArticleId articleId, String countryCode, Integer storeNumber) {
    UriComponents url =
        UriComponentsBuilder.fromUriString(mStoreAclHost + stockLocationArticleDataUrl)
            .buildAndExpand(
                countryCode,
                storeNumber,
                articleId.getArticleNumber(),
                articleId.getVariantNumber(),
                articleId.getBundleNumber());

    ArticleStockLocations articleStockLocation =
        webClient
            .get()
            .uri(url.toString())
            .retrieve()
            .bodyToMono(ArticleStockLocations.class)
            .onErrorResume(
                error -> {
                  if (error instanceof WebClientResponseException webClientResponseException
                      && HttpStatus.NOT_FOUND != webClientResponseException.getStatusCode()) {
                    log.error(
                        "Error on retrieving articleStockLocation for articleId={} from MstoreAcl. Error={} ",
                        articleId,
                        error.getMessage(),
                        error);
                  }
                  return Mono.empty();
                })
            .block();
    return (!ObjectUtils.isEmpty(articleStockLocation))
        ? articleStockLocation.getStockLocations()
        : Collections.emptyList();
  }

  public List<ArticleStockLocations> getArticleStockLocationForArticleList(
      List<ArticleId> articleIds, String countryCode, Integer storeNo) {
    UriComponents url =
        UriComponentsBuilder.fromUriString(mStoreAclHost + stockLocationArticleListDataUrl)
            .buildAndExpand(countryCode, storeNo);
    return webClient
        .post()
        .uri(url.toString())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(articleIds)
        .retrieve()
        .bodyToFlux(ArticleStockLocations.class)
        .collectList()
        .onErrorResume(
            error -> {
              if (error instanceof WebClientResponseException webClientResponseException
                  && HttpStatus.NOT_FOUND != webClientResponseException.getStatusCode()) {
                log.error(
                    "Error on retrieving articleStockLocation for ArticleList from MstoreAcl. Error={} ",
                    error.getMessage(),
                    error);
              }
              return Mono.empty();
            })
        .block();
  }
}
