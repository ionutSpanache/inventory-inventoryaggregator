package net.metrosystems.inventoryaggregator.common.stock_information.movie.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.MovieLocationRequestBody;
import net.metrosystems.inventoryaggregator.articleattributes.model.MovieLocationResponse;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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


  private WebClient webClient;

  @Autowired
  public  MovieClient(/*@Qualifier("restTemplateMovie")*/ RestTemplate restTemplate,WebClient webClient) {
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
//      return restTemplate.getForObject(urlInUse, StockDetailsResponse.class).getStockDetails();
      return webClient.get()
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

  public Integer getTotalAverageSales(
     String country, String salesLine, String locationId, List<Article> articles, int daysBefore ) {
    MovieLocationRequestBody locationRequestBody = new MovieLocationRequestBody();
    String urlInUse = String.format(movieTotalAverageSalesHost + movieTotalAveragePath);
    ObjectMapper mapper = new ObjectMapper();
    String jsonStringMovie = null;
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    try {
      jsonStringMovie = mapper.writeValueAsString(locationRequestBody);
      HttpEntity<String> request = new HttpEntity<>(jsonStringMovie, headers);
      MovieLocationResponse response = webClient.post()
              .uri(urlInUse)
              .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
              .bodyValue(locationRequestBody)
              .retrieve()
              .onStatus(this::isErrorResponse, clientResponse -> log(clientResponse, "Movie total sales details- api call unsuccessful  : status %d"))
              .bodyToMono(MovieLocationResponse.class)
              .block();

      return response == null ? null : response.getDaysBefore();
    } catch (Exception e) {
      log.error("Error while calling MOVIE input{} exception{}", jsonStringMovie, e);
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
    return getDataFromMovie(countryCode, salesLine, storeNo, articleList);
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

    ObjectMapper mapper = new ObjectMapper();
    String jsonStringMovie = null;
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    try {
      jsonStringMovie = mapper.writeValueAsString(movieRequest);
      HttpEntity<String> request = new HttpEntity<>(jsonStringMovie, headers);
//      StockDetailsResponse response = restTemplate.postForObject(urlInUse, request, StockDetailsResponse.class);
      StockDetailsResponse response = webClient.post()
              .uri(urlInUse)
              .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
              .bodyValue(movieRequest)
              .retrieve()
              .onStatus(this::isErrorResponse, clientResponse -> log(clientResponse, "Movie stock details - api call unsuccessful  : status %d"))
              .bodyToMono(StockDetailsResponse.class)
              .block();

      return response == null ? Collections.emptyList() : response.getStockDetails();
    } catch (Exception e) {
      log.error("Error while calling MOVIE input{} exception{}", jsonStringMovie, e);
      return null;
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
