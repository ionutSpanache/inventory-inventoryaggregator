package net.metrosystems.inventoryaggregator.common.stock_information.movie.client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.Article;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.ArticleVariantBundle;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.Bundle;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.MovieAverageRequest;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.MovieDataGroups;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.MovieLocation;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.MovieRequestBody;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.StockDetails;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.StockDetailsResponse;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class MovieClient {

  private final RestTemplate restTemplate;

  @Value("${MOVIE_STOCK_HOST}")
  private String movieStockServiceHost;

  @Value("${MOVIE_STOCK_PATH_FOR_VARIANT}")
  private String movieStockPathForVariant;

  @Value("${MOVIE_TOTAL_AVERAGE_SALES_HOST}")
  private String movieTotalAverageSalesHost;

  @Value("${MOVIE_TOTAL_AVERAGE_PATH}")
  private String movieTotalAveragePath;


  private final WebClient webClient;

  @Autowired
  public MovieClient(
      /*@Qualifier("restTemplateMovie")*/ RestTemplate restTemplate,  @Qualifier("webClientMovie") WebClient webClient) {
    this.restTemplate = restTemplate;
    this.webClient = webClient;
  }

  public List<StockDetails> getStockDetailsForArticleVariant(
      String countryCode, String salesLine, int storeNo, Long mmsArtNo, Long variantNo) {
    String urlInUse =
        String.format(
            movieStockServiceHost + movieStockPathForVariant,
            countryCode.toUpperCase(),
            salesLine.toUpperCase(),
            storeNo,
            mmsArtNo,
            variantNo);
    try {
      //      return restTemplate.getForObject(urlInUse,
      // StockDetailsResponse.class).getStockDetails();
      return webClient
          .get()
          .uri(urlInUse)
          .retrieve()
          .bodyToMono(StockDetailsResponse.class)
          .block()
          .getStockDetails();
    } catch (Exception e) {
      log.error("Error while calling MOVIE input{} exception{}", urlInUse, e);
      return null;
    }
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

  public List<StockDetails> getStockDetailsForArticleVariantBundle(
      String countryCode,
      String salesLine,
      int storeNo,
      Long articleNumber,
      Long variant,
      Long bundleNumber) {

    Article article = new Article();
    article.setArticleNo(articleNumber);
    article.setVariant(variant);

    Bundle bundle = new Bundle();
    bundle.setBundleNo(bundleNumber);
    List<Bundle> bundleList = new ArrayList<>();
    bundleList.add(bundle);
    article.setBundles(bundleList);

    List<Article> articleList = new ArrayList<>();
    articleList.add(article);
    return getDataFromMovie(countryCode, salesLine, storeNo, articleList);
  }

  public List<StockDetails> getStockDetailsForArticleVariantBundleList(
      String countryCode, String salesLine, int storeNo, List<ArticleId> articleIdList) {
    List<Article> articleList = new ArrayList<>();

    articleIdList.forEach(
        articleId -> {
          Article article = new Article();
          article.setArticleNo(articleId.getArticleNumber());
          article.setVariant(articleId.getVariantNumber());

          Bundle bundle = new Bundle();
          bundle.setBundleNo(articleId.getBundleNumber());
          List<Bundle> bundleList = new ArrayList<>();
          bundleList.add(bundle);
          article.setBundles(bundleList);

          articleList.add(article);
        });

    List<List<Article>> partitions = ListUtils.partition(articleList, 500);
    return partitions.parallelStream()
        .map(a -> getDataFromMovie(countryCode, salesLine, storeNo, a))
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private List<StockDetails> getDataFromMovie(
      String countryCode, String salesLine, int storeNo, List<Article> articleList) {
    String urlInUse = String.format("%s/cache", movieStockServiceHost);
    List<String> reasonsList = new ArrayList<>();
    reasonsList.add("IST");
    reasonsList.add("RET");

    List<Integer> storeNumber = new ArrayList<>();
    storeNumber.add(storeNo);

    List<MovieDataGroups> dataGroupsList = new ArrayList<>();
    dataGroupsList.add(new MovieDataGroups("OPOR", -1));
    dataGroupsList.add(new MovieDataGroups("OPMO", reasonsList));
    dataGroupsList.add(new MovieDataGroups("SYST"));
    dataGroupsList.add(new MovieDataGroups("STRD"));
    if ("IN".equals(countryCode) && "MCC".equals(salesLine)) {
      dataGroupsList.add(new MovieDataGroups("PARK"));
    }
    dataGroupsList.add(new MovieDataGroups("MODE", LocalDate.now()));
    dataGroupsList.add(new MovieDataGroups("RESV"));

    MovieRequestBody movieRequest = new MovieRequestBody();
    movieRequest.setConsumer("MSTORE");
    movieRequest.setCountry(countryCode.toUpperCase());
    movieRequest.setSalesLine(salesLine.toUpperCase());
    movieRequest.setLocationId(storeNumber);
    movieRequest.setArticles(articleList);
    movieRequest.setDataGroups(dataGroupsList);

    try {
      StockDetailsResponse response =
          webClient
              .post()
              .uri(urlInUse)
              .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
              .bodyValue(movieRequest)
              .retrieve()
              .onStatus(
                  this::isErrorResponse,
                  clientResponse ->
                      log(
                          clientResponse,
                          "Movie stock details - api call unsuccessful  : status %d"))
              .bodyToMono(StockDetailsResponse.class)
              .block();

      return response == null ? Collections.emptyList() : response.getStockDetails();
    } catch (Exception e) {
      log.error("Error while calling MOVIE input{} exception{}", movieRequest, e);
      return null;
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
