package net.metrosystems.inventoryaggregator.common.movieacl.client;

import java.util.List;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieAclClient {

  @Value("${mstore.inventory.host}")
  private final String mstoreHost;

  @Value("${mstore.movieacl.stock.data.url}")
  private final String stockDataUrl;

  @Value("${mstore.movieacl.average-sales.url}")
  private final String averageSalesPath;

  private final WebClient webClient;

  public List<StockData> getStockDataList(
      List<ArticleId> articleIdList, String countryCode, String salesLine, Integer storeNumber) {

    UriComponents uri =
        UriComponentsBuilder.fromUriString(mstoreHost + stockDataUrl)
            .buildAndExpand(countryCode, salesLine, storeNumber);

    return webClient
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
        UriComponentsBuilder.fromUriString(mstoreHost)
            .path(averageSalesPath)
            .buildAndExpand(
                countryCode,
                salesLine,
                storeNumber,
                articleId.getArticleNumber(),
                articleId.getVariantNumber(),
                articleId.getBundleNumber())
            .toUriString();

    return webClient
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
