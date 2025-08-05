package net.metrosystems.inventoryaggregator.articlestoremasterdata.client;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.AclStoreMasterData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@Component
@Slf4j
@RequiredArgsConstructor
public class AclArticleStoreMasterDataClient {

  @Qualifier("webClientForAcl")
  private final WebClient webClient;

  @Value("${ACL_STORE_MASTER_DATA_HOST}")
  private final String aclArticleMasterDataHost;

  @Value("${ACL_STORE_MASTER_DATA_RU_HOST}")
  private final String aclArticleMasterDataRuHost;

  @Value("${ACL_STORE_MASTER_DATA_PATH_ARTICLES_BY_ART_VAR_BUNDLE}")
  private final String aclArticlesMasterDataPathByArtVarBundle;

  public List<AclStoreMasterData> getArticleStoreMasterDataByArtVarBundle(
      String countryCode, String salesLine, Integer storeNo, List<ArticleId> articleIdList) {

    String host =
        "RU".equalsIgnoreCase(countryCode) ? aclArticleMasterDataRuHost : aclArticleMasterDataHost;

    UriComponents uriComponents =
        UriComponentsBuilder.fromUriString(host + aclArticlesMasterDataPathByArtVarBundle)
            .buildAndExpand(countryCode.toUpperCase(), salesLine.toUpperCase(), storeNo);

    return getArticlesFromStoreMasterData(uriComponents, articleIdList);
  }

  private List<AclStoreMasterData> getArticlesFromStoreMasterData(
      UriComponents uriComponents, List<ArticleId> articleIdList) {
    return webClient
        .post()
        .uri(uriComponents.toString())
        .bodyValue(articleIdList)
        .retrieve()
        .bodyToFlux(AclStoreMasterData.class)
        .onErrorResume(
            error -> {
              log.error(
                  "ArticleStoreMasterData - getArticlesFromStoreMasterData - api call unsuccessful {}",
                  error.getMessage(),
                  error);
              return Flux.empty();
            })
        .collectList()
        .block();
  }
}
