package net.metrosystems.inventoryaggregator.common.stock_information.movie.service;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.client.MovieClient;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.ArticleVariantBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MovieService {

  private final MovieClient movieClient;
  private static final String dateFormat = "yyyy-MM-dd";

  @Autowired
  public MovieService(MovieClient pMovieClient) {
    this.movieClient = pMovieClient;
  }

  @Async("asyncExecutor")
  public CompletableFuture<String> getTotalAverageSales(
      String countryName, String salesLine, Integer storeNumber, ArticleId articleId) {

    String totalAverageSales =
        movieClient.getTotalAverageSales(
            countryName,
            salesLine,
            storeNumber,
            ArticleVariantBundle.builder()
                .articleNo(articleId.getArticleNumber())
                .variant(articleId.getVariantNumber())
                .bundleNo(articleId.getBundleNumber())
                .build(),
            30);
    return CompletableFuture.completedFuture(totalAverageSales);
  }
}
