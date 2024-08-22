package net.metrosystems.inventoryaggregator.common.stock_information.movie.client;

import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.ArticleVariantBundle;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.MovieAverageRequest;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.MovieLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class MovieClient {

  @Value("${MOVIE_TOTAL_AVERAGE_SALES_HOST}")
  private String movieTotalAverageSalesHost;

  @Value("${MOVIE_TOTAL_AVERAGE_PATH}")
  private String movieTotalAveragePath;

  private final WebClient webClient;

  @Autowired
  public MovieClient(@Qualifier("webClientMovie") WebClient webClient) {
    this.webClient = webClient;
  }

  public String getTotalAverageSales(
      String countryName,
      String salesLine,
      Integer storeNumber,
      ArticleVariantBundle articleVariantBundle,
      int daysBefore) {
    MovieAverageRequest locationRequestBody =
        MovieAverageRequest.builder()
            .location(
                MovieLocation.builder()
                    .country(countryName)
                    .salesLine(salesLine)
                    .locationId(storeNumber)
                    .build())
            .article(articleVariantBundle)
            .daysBefore(daysBefore)
            .build();

    try {
      ResponseEntity<String> responseEntity =
          webClient
              .post()
              .uri(movieTotalAverageSalesHost + movieTotalAveragePath)
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(locationRequestBody)
              .retrieve()
              .toEntity(String.class)
              .onErrorResume(
                  error -> {
                    log.error(
                        "Movie total sales details- api call unsuccessful for {}",
                        locationRequestBody,
                        error);
                    return Mono.empty();
                  })
              .block();

      return responseEntity == null ? null : responseEntity.getBody();
    } catch (Exception e) {
      log.error("Error while calling MOVIE getTotalAverageSales", e);
      return null;
    }
  }
}
