package net.metrosystems.inventoryaggregator.stocklocation.acl.service;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.stocklocation.acl.model.AclArticleStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleLocationForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlelocation.ArticleLocation;
import net.metrosystems.inventoryaggregator.stocklocation.acl.model.PaginatedAclBundleStockLocation;
import net.metrosystems.inventoryaggregator.stocklocation.acl.model.AclBundleStockLocation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AclStockClient {

  @Qualifier("webClientForAcl")
  private final WebClient webClient;

  @Value("${ACL_STOCK_SERVICE_HOST}")
  private String aclStockLocationHost;

  @Value("${ACL_STOCK_LOCATION_PATH_FOR_VARIANT}")
  private String aclStockLocationPathForVariant;

  @Value("${ACL_STOCK_ARTICLE_LOCATION}")
  private String aclArticleLocationPath;

  @Value("${ACL_STOCK_LOCATION_PATH_FOR_ARTICLE_LIST}")
  private String aclStockLocationPathForArticleList;

  @Value("${ACL_STOCK_ARTICLE_LOCATION_FOR_ARTICLE_LIST}")
  private String aclArticleLocationForArticleListPath;

  public List<AclBundleStockLocation> getStockLocationsForArticleVariant(
      String countryCode, String salesLine, Integer storeNo, Long articleNo, Long variantNo) {
    UriComponents uri =
        UriComponentsBuilder.fromHttpUrl(aclStockLocationHost + aclStockLocationPathForVariant)
            .buildAndExpand(
                countryCode.toUpperCase(), salesLine.toUpperCase(), storeNo, articleNo, variantNo);

    log.debug("uriStockLocation={}", uri);
    PaginatedAclBundleStockLocation paginatedAclBundleStockLocation =
        webClient
            .get()
            .uri(uri.toString())
            .retrieve()
            .bodyToMono(PaginatedAclBundleStockLocation.class)
            .block();

    if (paginatedAclBundleStockLocation != null) {
      return paginatedAclBundleStockLocation.getStockLocations();
    }
    return null;
  }

  public List<AclArticleStockLocation> getStockLocationsForArticleList(
      String countryCode, String salesLine, Integer storeNo, List<ArticleId> articleIdList) {
    UriComponents uriComponents =
        UriComponentsBuilder.fromHttpUrl(aclStockLocationHost + aclStockLocationPathForArticleList)
            .buildAndExpand(countryCode.toUpperCase(), salesLine.toUpperCase(), storeNo);
    log.debug("uriStockLocation=" + uriComponents);

    List<AclArticleStockLocation> aclArticleStockLocations =
        webClient
            .post()
            .uri(uriComponents.toUriString())
            .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
            .bodyValue(articleIdList)
            .retrieve()
            .onStatus(
                this::isErrorResponse,
                clientResponse ->
                    log(clientResponse, "StockLocation - api call unsuccessful  : status %d"))
            .bodyToFlux(AclArticleStockLocation.class)
            .collectList()
            .block();

    return !Objects.requireNonNull(aclArticleStockLocations).isEmpty()
        ? aclArticleStockLocations
        : emptyList();
  }


  public List<ArticleLocationForArticleId> getArticleLocationsForArticleList(
      String countryCode, String salesLine, Integer storeNo, List<ArticleId> articleIdList) {
    UriComponents urlInUse =
        UriComponentsBuilder.fromHttpUrl(
                aclStockLocationHost + aclArticleLocationForArticleListPath)
            .buildAndExpand(countryCode.toUpperCase(), salesLine.toUpperCase(), storeNo);
    log.debug("uriArticleLocation=" + urlInUse.toUriString());

    try {
      List<ArticleLocationForArticleId> articleLocationForArticleIds =
          webClient
              .post()
              .uri(urlInUse.toUriString())
              .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
              .bodyValue(articleIdList)
              .retrieve()
              .onStatus(
                  this::isErrorResponse,
                  clientResponse ->
                      log(
                          clientResponse,
                          "ArticleLocation - getArticleLocationsForArticleList - api call unsuccessful  : status %d"))
              .bodyToFlux(ArticleLocationForArticleId.class)
              .collectList()
              .block();

      return !Objects.requireNonNull(articleLocationForArticleIds).isEmpty()
          ? articleLocationForArticleIds
          : emptyList();
    } catch (Exception e) {
      log.error("AclStockClient.getArticleLocationsForArticleList - error {} ", e.getMessage(), e);
      return emptyList();
    }
  }

  private boolean isErrorResponse(HttpStatus httpStatus) {
    return !httpStatus.is2xxSuccessful();
  }

  private Mono<? extends Throwable> log(ClientResponse clientResponse, String message) {
    log.error(message, clientResponse.statusCode().value());
    return Mono.empty();
  }
}
