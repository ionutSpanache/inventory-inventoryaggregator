package net.metrosystems.inventoryaggregator.common.movieacl.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieAclClient {

  @Value("${mstore.movieacl.host}")
  private final String movieAclHost;

  @Value("${mstore.movieacl.stock.data.url}")
  private final String stockDataUrl;

  @Value("${mstore.movieacl.average-sales.url}")
  private final String averageSalesPath;

  private final WebClient webClientMstoreSecured;

  public List<StockData> getStockDataList(
      List<ArticleId> articleIdList, String countryCode, String salesLine, Integer storeNumber) {

    UriComponents uri =
        UriComponentsBuilder.fromUriString(movieAclHost + stockDataUrl)
            .buildAndExpand(countryCode, salesLine, storeNumber);

    return webClientMstoreSecured
        .post()
        .uri(uri.toUriString())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(articleIdList)
        .retrieve()
        .bodyToFlux(StockData.class)
        .onErrorResume(
            error -> {
              log.error("Movie stock details - api call unsuccessful for {}", articleIdList, error);
              return Mono.empty();
            })
        .collectList()
        .block();
  }

  public String getAverageSales(
      ArticleId articleId, String countryCode, String salesLine, Integer storeNumber) {
    String uri =
        UriComponentsBuilder.fromUriString(movieAclHost)
            .path(averageSalesPath)
            .buildAndExpand(
                countryCode,
                salesLine,
                storeNumber,
                articleId.getArticleNumber(),
                articleId.getVariantNumber(),
                articleId.getBundleNumber())
            .toUriString();

    return webClientMstoreSecured
        .get()
        .uri(uri)
        .retrieve()
        .bodyToMono(String.class)
        .onErrorResume(
            error -> {
              log.error(
                  "#getAverageSales unsuccessful call for country:{},storeNumber:{}, articleId:{}",
                  countryCode,
                  storeNumber,
                  articleId,
                  error);
              return Mono.empty();
            })
        .block();
  }
}
