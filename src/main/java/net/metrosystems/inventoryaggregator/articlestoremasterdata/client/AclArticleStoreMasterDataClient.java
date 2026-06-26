package net.metrosystems.inventoryaggregator.articlestoremasterdata.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.AclStoreMasterData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AclArticleStoreMasterDataClient {

  private final WebClient webClientForAcl;

  @Value("${ACL_STORE_MASTER_DATA_HOST}")
  private final String aclArticleMasterDataHost;

  @Value("${ACL_STORE_MASTER_DATA_PATH_ARTICLES_BY_ART_VAR_BUNDLE}")
  private final String aclArticlesMasterDataPathByArtVarBundle;

  public List<AclStoreMasterData> getArticleStoreMasterDataByArtVarBundle(
      String countryCode, String salesLine, Integer storeNo, List<ArticleId> articleIdList) {

    UriComponents uriComponents =
        UriComponentsBuilder.fromUriString(
                aclArticleMasterDataHost + aclArticlesMasterDataPathByArtVarBundle)
            .buildAndExpand(countryCode.toUpperCase(), salesLine.toUpperCase(), storeNo);

    return webClientForAcl
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
