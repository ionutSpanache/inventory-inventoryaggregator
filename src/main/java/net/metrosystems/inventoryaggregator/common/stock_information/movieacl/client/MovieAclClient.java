package net.metrosystems.inventoryaggregator.common.stock_information.movieacl.client;

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
  private String stockDataUrl;

  private final WebClient webClient;

  public List<StockData> getStockDataList(
      List<ArticleId> articleIdList, String countryCode, String salesLine, Integer storeNumber) {

    UriComponents uri =
        UriComponentsBuilder.fromHttpUrl(mstoreHost + stockDataUrl)
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
}
