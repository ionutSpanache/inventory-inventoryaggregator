package net.metrosystems.inventoryaggregator.articleattributes.repository;

import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleDataFromMovie;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticlePlannedDeliveryDate;
import net.metrosystems.inventoryaggregator.articleattributes.model.StockInformation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.MovieAdapter;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.MovieOpenOrder;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public class ArticleDataFromMovieRepository {
  private final MovieService movieService;

  @Autowired
  public ArticleDataFromMovieRepository(MovieService movieService) {
    this.movieService = movieService;
  }


  @Async("asyncExecutor")
  public CompletableFuture<ArticleDataFromMovie> getArticleDataFromMovie(
      ArticleId articleId, String countryCode, String salesLine, Integer storeNo) {
    MovieAdapter movieAdapter =
        movieService.getArticleStockForVariantBundle(
            countryCode,
            salesLine,
            storeNo,
            articleId.getArticleNumber(),
            articleId.getVariantNumber(),
            articleId.getBundleNumber());
    ArticleDataFromMovie articleDataFromMovie =
        transformMovieAdapterToRawArticleDataFromMovieByArtVarBundle(
            movieAdapter,
            articleId.getArticleNumber(),
            articleId.getVariantNumber(),
            articleId.getBundleNumber());

    return CompletableFuture.completedFuture(articleDataFromMovie);
  }

  @Async("asyncExecutor")
  public CompletableFuture<List<StockData>> getArticleListDataFromMovie(
      List<ArticleId> articleIdList, String countryCode, String salesLine, Integer storeNo) {
    List<MovieAdapter> movieAdapterList =
        movieService.getArticleStockListForVariantBundle(
            countryCode,
            salesLine,
            storeNo,
            articleIdList);
    List<StockData> stockDataList = new ArrayList<>();
    movieAdapterList.forEach(movieAdapter -> {
      StockData stockData = new StockData();
      stockData.setArticleId(ArticleId.builder()
          .articleNumber(movieAdapter.getArticleNo())
          .variantNumber(movieAdapter.getVariantId())
          .bundleNumber(movieAdapter.getBundleId())
          .build());
      stockData.setArticleStockData(
          transformMovieAdapterToArticleStockData(movieAdapter));
      stockData.setArticlePlannedDeliveryDates(
          transformMovieAdapterToArticlePlannedDeliveryDateList(movieAdapter));
      stockDataList.add(stockData);
    });

    return CompletableFuture.completedFuture(stockDataList);
  }

  private ArticleDataFromMovie transformMovieAdapterToRawArticleDataFromMovieByArtVarBundle(
      MovieAdapter movieAdapter, Long articleNumber, Long variantNumber, Long bundleNumber) {
    ArticleDataFromMovie articleDataFromMovie = new ArticleDataFromMovie();
    if (movieAdapter != null) {
      if ((articleNumber.equals(movieAdapter.getArticleNo()))
          && (variantNumber.equals(movieAdapter.getVariantId()))
          && (bundleNumber.equals(movieAdapter.getBundleId()))) {
        articleDataFromMovie.setArticleStockData(
            transformMovieAdapterToArticleStockData(movieAdapter));
        articleDataFromMovie.setArticlePlannedDeliveryDates(
            transformMovieAdapterToArticlePlannedDeliveryDateList(movieAdapter));
      }
      return articleDataFromMovie;
    }
    return null;
  }

  private StockInformation transformMovieAdapterToArticleStockData(MovieAdapter movieAdapter) {
    return new StockInformation(
        movieAdapter.getAvailableStock(),
        movieAdapter.getLastGoodReceivingDate(),
        movieAdapter.getLastStockCorrectionDate(),
        movieAdapter.getLastSalesDate(),
        movieAdapter.getReservedStock(),
        movieAdapter.getStock(),
        movieAdapter.getStockMarkDown(),
        movieAdapter.getUnavailableStock(),
        movieAdapter.getBlockedStock(),
        movieAdapter.getStockCoverage(),
        movieAdapter.getDailyMeanSales(),
        movieAdapter.getOpenInterstoreQuantity(),
        movieAdapter.getOpenReturnQuantity(),
        movieAdapter.getOpenGoodReceiving(),
        movieAdapter.getLastSCQuantity(),
        movieAdapter.getLastSalesQuantity(),
        movieAdapter.getLastGRQuantity(),
        movieAdapter.isConsignmentstock());
  }

  private List<ArticlePlannedDeliveryDate> transformMovieAdapterToArticlePlannedDeliveryDateList(
      MovieAdapter movieAdapter) {
    if ((movieAdapter != null)
        && (movieAdapter.getOpenOrders() != null)
        && (!movieAdapter.getOpenOrders().isEmpty())) {
      List<ArticlePlannedDeliveryDate> articlePlannedDeliveryDateList = new ArrayList<>();
      for (MovieOpenOrder movieOpenOrder : movieAdapter.getOpenOrders()) {
        if (articlePlannedDeliveryDateList == null) {
          articlePlannedDeliveryDateList =
              new ArrayList<>(
                  Collections.singletonList(
                      transformMovieOpenOrderToArticlePlannedDeliveryDate(movieOpenOrder)));
        } else {
          articlePlannedDeliveryDateList.add(
              transformMovieOpenOrderToArticlePlannedDeliveryDate(movieOpenOrder));
        }
      }
      return articlePlannedDeliveryDateList;
    }
    return null;
  }

  private ArticlePlannedDeliveryDate transformMovieOpenOrderToArticlePlannedDeliveryDate(
      MovieOpenOrder movieOpenOrder) {
    return new ArticlePlannedDeliveryDate(
        movieOpenOrder.getQuantity(),
        movieOpenOrder.getDeliveryDate(),
        movieOpenOrder.getOrderDate());
  }
}
