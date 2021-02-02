package net.metrosystems.inventoryaggregator.common.stock_location.acl.client;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.AclArticleStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleLocationForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlelocation.ArticleLocation;
import net.metrosystems.inventoryaggregator.common.stock_location.acl.model.AclBundleStockLocation;
import net.metrosystems.inventoryaggregator.common.stock_location.acl.model.PaginatedAclBundleStockLocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
@RequiredArgsConstructor
public class AclStockClient {

  private final RestTemplate restTemplate;

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
    String urlInUse =
        String.format(
            aclStockLocationHost + aclStockLocationPathForVariant,
            countryCode.toUpperCase(),
            salesLine.toUpperCase(),
            storeNo,
            articleNo,
            variantNo);
    log.debug("uriStockLocation=" + urlInUse);
    PaginatedAclBundleStockLocation paginatedAclBundleStockLocation =
        restTemplate.getForObject(urlInUse, PaginatedAclBundleStockLocation.class);
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
    HttpEntity<?> request = new HttpEntity<>(articleIdList, headers);
    ResponseEntity<List<AclArticleStockLocation>> response =
        restTemplate.exchange(
            urlInUse.toUriString(),
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<List<AclArticleStockLocation>>() {});
    if (!response.getStatusCode().is2xxSuccessful()) {
      String message =
          String.format(
              "StockLocation - api call unsuccessful  : status %d", response.getStatusCodeValue());
      log.error(message);
      return Collections.emptyList();
    }
    return !Objects.requireNonNull(response.getBody()).isEmpty() ? response.getBody() : Collections.emptyList();
  }

  public ArticleLocation getArticleLocationsForArticleVariant(
      String countryCode, String salesLine, Integer storeNo, ArticleId articleId) {
    String urlInUse =
        String.format(
            aclStockLocationHost + aclArticleLocationPath,
            countryCode.toUpperCase(),
            salesLine.toUpperCase(),
            storeNo,
            articleId.getArticleNumber(),
            articleId.getVariantNumber(),
            articleId.getBundleNumber());
    log.debug("uriArticleLocation=" + urlInUse);

    try {
        return restTemplate.getForObject(urlInUse, ArticleLocation.class);

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
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<?> request = new HttpEntity<>(articleIdList, headers);
      ResponseEntity<List<ArticleLocationForArticleId>> response =
          restTemplate.exchange(
              urlInUse.toUriString(),
              HttpMethod.POST,
              request,
              new ParameterizedTypeReference<List<ArticleLocationForArticleId>>() {});
      if (!response.getStatusCode().is2xxSuccessful()) {
        String message =
            String.format(
                "ArticleLocation - getArticleLocationsForArticleList - api call unsuccessful  : status %d",
                response.getStatusCodeValue());
        log.error(message);
        return Collections.emptyList();
      }
      return !Objects.requireNonNull(response.getBody()).isEmpty() ? response.getBody() : Collections.emptyList();
    } catch (Exception e) {
      log.error("AclStockClient.getArticleLocationsForArticleList - error {} ", e.getMessage(), e);
      return Collections.emptyList();
    }
  }
}
