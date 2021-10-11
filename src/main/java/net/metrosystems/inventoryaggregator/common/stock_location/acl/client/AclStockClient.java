package net.metrosystems.inventoryaggregator.common.stock_location.acl.client;

import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.AclArticleStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleLocationForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlelocation.ArticleLocation;
import net.metrosystems.inventoryaggregator.common.stock_location.acl.model.AclBundleStockLocation;
import net.metrosystems.inventoryaggregator.common.stock_location.acl.model.PaginatedAclBundleStockLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class AclStockClient {


  private WebClient webClient;

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

  @Autowired
  public  AclStockClient(@Qualifier("webClientForAcl") WebClient webClient) {
        this.webClient = webClient;
  }

  public List<AclBundleStockLocation> getStockLocationsForArticleVariant(
      String countryCode, String salesLine, Integer storeNo, Long articleNo, Long variantNo) {
    UriComponents uri =
            UriComponentsBuilder.fromHttpUrl(aclStockLocationHost + aclStockLocationPathForVariant)
                    .buildAndExpand(
                            countryCode.toUpperCase(),
                            salesLine.toUpperCase(),
                            storeNo,
                            articleNo,
                            variantNo);


    log.debug("uriStockLocation=" + uri.toString());
    PaginatedAclBundleStockLocation paginatedAclBundleStockLocation =
            webClient.get()
                    .uri(uri.toString())
                    .retrieve()
                    .bodyToMono(PaginatedAclBundleStockLocation.class)
                    .block();
    try {
      return paginatedAclBundleStockLocation.getStockLocations();
    } catch (Exception e) {
      return null;
    }
  }

  public List<AclArticleStockLocation> getStockLocationsForArticleList(
      String countryCode, String salesLine, Integer storeNo, List<ArticleId> articleIdList) {
    UriComponents urlInUse =
        UriComponentsBuilder.fromHttpUrl(aclStockLocationHost + aclStockLocationPathForArticleList)
            .buildAndExpand(countryCode.toUpperCase(), salesLine.toUpperCase(), storeNo);
    log.debug("uriStockLocation=" + urlInUse);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    List<AclArticleStockLocation> aclArticleStockLocations = webClient.post()
            .uri(urlInUse.toUriString())
            .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
            .bodyValue(articleIdList)
            .retrieve()
            .onStatus(this::isErrorResponse, clientResponse -> log(clientResponse, "StockLocation - api call unsuccessful  : status %d"))
            .bodyToFlux(AclArticleStockLocation.class)
            .collectList()
            .block();


    return !Objects.requireNonNull(aclArticleStockLocations).isEmpty() ? aclArticleStockLocations : Collections.emptyList();
  }

  public ArticleLocation getArticleLocationsForArticleVariant(
      String countryCode, String salesLine, Integer storeNo, ArticleId articleId) {
    UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(
            aclStockLocationHost + aclArticleLocationPath)
            .buildAndExpand(
                    countryCode.toUpperCase(),
                    salesLine.toUpperCase(),
                    storeNo,
                    articleId.getArticleNumber(),
                    articleId.getVariantNumber(),
                    articleId.getBundleNumber());
    log.debug("uriArticleLocation=" + uriComponents.toString());

    try {
        return webClient.get()
                .uri(uriComponents.toUriString())
                .retrieve()
                .bodyToMono(ArticleLocation.class)
                .block();

    } catch (Exception e) {
      return null;
    }
  }

  public List<ArticleLocationForArticleId> getArticleLocationsForArticleList(
      String countryCode, String salesLine, Integer storeNo, List<ArticleId> articleIdList) {
    UriComponents urlInUse =
        UriComponentsBuilder.fromHttpUrl(
                aclStockLocationHost + aclArticleLocationForArticleListPath)
            .buildAndExpand(countryCode.toUpperCase(), salesLine.toUpperCase(), storeNo);
    log.debug("uriArticleLocation=" + urlInUse.toUriString());

    try {
      List<ArticleLocationForArticleId> articleLocationForArticleIds = webClient.post()
              .uri(urlInUse.toUriString())
              .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
              .bodyValue(articleIdList)
              .retrieve()
              .onStatus(this::isErrorResponse, clientResponse -> log(clientResponse, "ArticleLocation - getArticleLocationsForArticleList - api call unsuccessful  : status %d"))
              .bodyToFlux(ArticleLocationForArticleId.class)
              .collectList()
              .block();

      return !Objects.requireNonNull(articleLocationForArticleIds).isEmpty() ? articleLocationForArticleIds : Collections.emptyList();
    } catch (Exception e) {
      log.error("AclStockClient.getArticleLocationsForArticleList - error {} ", e.getMessage(), e);
      return Collections.emptyList();
    }
  }

  private boolean isErrorResponse(HttpStatus httpStatus) {
    return  !httpStatus.is2xxSuccessful();
  }

  private Mono<? extends Throwable> log(ClientResponse clientResponse, String message) {
    log.error(message, clientResponse.statusCode().value());
    return Mono.empty();
  }


}
