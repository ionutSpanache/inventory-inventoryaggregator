package net.metrosystems.inventoryaggregator.common.stock_information.movie.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieClient {

  private final RestTemplate restTemplate;

  @Value("${MOVIE_STOCK_HOST}")
  private String movieStockServiceHost;

  @Value("${MOVIE_STOCK_PATH_FOR_VARIANT}")
  private String movieStockPathForVariant;

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
      return restTemplate.getForObject(urlInUse, StockDetailsResponse.class).getStockDetails();
    } catch (Exception e) {
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
    String urlInUse = String.format(movieStockServiceHost + "/cache");

    List<String> reasonsList = new ArrayList<>();
    reasonsList.add("IST");
    reasonsList.add("RETURN");

    List<Integer> storeNumber = new ArrayList<>();
    storeNumber.add(storeNo);

    List<MovieDataGroups> dataGroupsList = new ArrayList<>();
    dataGroupsList.add(new MovieDataGroups("OPOR", -1));
    dataGroupsList.add(new MovieDataGroups("OPMO", reasonsList));
    dataGroupsList.add(new MovieDataGroups("SYST"));
    dataGroupsList.add(new MovieDataGroups("STRD"));

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
      return restTemplate
              .postForObject(urlInUse, request, StockDetailsResponse.class)
              .getStockDetails();
    } catch (Exception e) {
      return null;
    }
  }

}
