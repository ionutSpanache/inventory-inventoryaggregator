package net.metrosystems.inventoryaggregator.articlestoremasterdata.client;

import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.MaximumRetailPrice;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleLocationForArticleId;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.client.model.ArticleStoreMasterDataACLResponse;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AclArticleStoreMasterDataClient {

  @Qualifier("webClientForAcl")
  @Autowired
  private WebClient webClient;

  @Value("${ACL_STORE_MASTER_DATA_HOST}")
  private String aclArticleMasterDataHost;
  @Value("${ACL_STORE_MASTER_DATA_PATH_BY_EAN}")
  private String aclArticleMasterDataPathByEan;
  @Value("${ACL_STORE_MASTER_DATA_PATH_BY_SUBSYS}")
  private String aclArticleMasterDataPathBySubsys;
  @Value("${ACL_STORE_MASTER_DATA_PATH_ARTICLES_BY_ART_VAR_BUNDLE}")
  private String aclArticlesMasterDataPathByArtVarBundle;
  @Value("${ACL_MAXIMUM_RETAIL_PRICE_PATH}")
  private String maximumRetailPricePath;

  public List<ArticleStoreMasterData> getArticleStoreMasterDataByEan(String countryCode,
                                                                     String salesLine,
                                                                     Integer storeNo,
                                                                     String barcode) {
    UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(
            aclArticleMasterDataHost + aclArticleMasterDataPathByEan)
            .buildAndExpand(countryCode.toUpperCase(),
                    salesLine.toUpperCase(),
                    storeNo,
                    barcode);

    return getArticleStoreMasterData(uriComponents);
  }

  public List<ArticleStoreMasterData> getArticleStoreMasterDataBySubsys(String countryCode,
                                                                        String salesLine,
                                                                        Integer storeNo,
                                                                        Integer subsys) {
    UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(
            aclArticleMasterDataHost + aclArticleMasterDataPathBySubsys)
            .buildAndExpand(countryCode.toUpperCase(),
                    salesLine.toUpperCase(),
                    storeNo,
                    subsys);

    return getArticleStoreMasterData(uriComponents);
  }

  public List<ArticleStoreMasterData> getArticleStoreMasterDataByArtVarBundle(
      String countryCode, String salesLine, Integer storeNo, List<ArticleId> articleIdList ) {
    UriComponents uriComponents =
        UriComponentsBuilder.fromHttpUrl(
                aclArticleMasterDataHost + aclArticlesMasterDataPathByArtVarBundle)
            .buildAndExpand(countryCode.toUpperCase(), salesLine.toUpperCase(), storeNo);

    return getArticlesFromStoreMasterData(uriComponents,articleIdList);
  }

  private List<ArticleStoreMasterData> getArticleStoreMasterData(UriComponents uriComponents) {
    return webClient.get()
            .uri(uriComponents.toString())
            .retrieve()
            .bodyToMono(ArticleStoreMasterDataACLResponse.class)
            .map(ArticleStoreMasterDataACLResponse::getArticleStoreMasterData)
            .defaultIfEmpty(Collections.emptyList())
            .onErrorResume(throwable -> {
              log.error("ACL error when calling " + uriComponents.toUriString());
              return Mono.just(Collections.emptyList());
            })
            .block();
  }


  private List<ArticleStoreMasterData> getArticlesFromStoreMasterData(UriComponents uriComponents, List<ArticleId> articleIdList) {
    return webClient.post()
            .uri(uriComponents.toString())
        .bodyValue(articleIdList)
        .retrieve()
        .onStatus(this::isErrorResponse, clientResponse ->
            log(clientResponse, "ArticleStoreMasterData - getArticlesFromStoreMasterData - api call unsuccessful  : status %d"))
        .bodyToFlux(ArticleStoreMasterData.class)
        .collectList()
        .block();
  }

  public MaximumRetailPrice getMaximumRetailPrice(
          String countryCode, String salesLine, Integer storeNumber, ArticleId articleId) {

    UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(
            aclArticleMasterDataHost + maximumRetailPricePath)
            .queryParam("articleNumber", articleId.getArticleNumber())
            .queryParam("variantNumber", articleId.getVariantNumber())
            .queryParam("bundleNumber", articleId.getBundleNumber())
            .buildAndExpand(countryCode, salesLine, storeNumber);

    return webClient.get()
            .uri(uriComponents.toString())
            .retrieve()
            .bodyToMono(MaximumRetailPrice.class)
            .onErrorResume(throwable -> {
              log.error("ACL error when calling " + maximumRetailPricePath);
              return Mono.just(MaximumRetailPrice.builder().build());
            })
            .block();

  }

  private boolean isErrorResponse(HttpStatus httpStatus) {
    return  !httpStatus.is2xxSuccessful();
  }

  private Mono<? extends Throwable> log(ClientResponse clientResponse, String message) {
    log.error(message, clientResponse.statusCode().value());
    return Mono.empty();
  }
}
