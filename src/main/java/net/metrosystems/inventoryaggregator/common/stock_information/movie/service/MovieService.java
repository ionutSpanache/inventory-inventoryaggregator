package net.metrosystems.inventoryaggregator.common.stock_information.movie.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.client.MovieClient;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.ArticleVariantBundle;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.MovieAdapter;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.MovieOpenOrder;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.Stock;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.StockDetails;
import org.apache.commons.lang3.StringUtils;
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

  private MovieAdapter transformStockDetailToMovieAdapter(StockDetails stockDetails) {
    Float openInterstoreQuantity = 0.0f;
    Float openReturnQuantity = 0.0f;
    MovieAdapter movieAdapter = new MovieAdapter();
    movieAdapter.setArticleNo(stockDetails.getArticleNo());
    movieAdapter.setVariantId(stockDetails.getVariant());
    movieAdapter.setBundleId(stockDetails.getBundleNo());
    if (stockDetails.getStockSummary() != null) {
      movieAdapter.setStock(stockDetails.getStockSummary().getSystemStock());
      movieAdapter.setStockMarkDown(stockDetails.getStockSummary().getMarkDownStock());
      movieAdapter.setReservedStock(stockDetails.getStockSummary().getReservation());
      movieAdapter.setBlockedStock(stockDetails.getStockSummary().getBlockedStock());
      movieAdapter.setAvailableStock(stockDetails.getStockSummary().getAvailableStock());
      movieAdapter.setOpenGoodReceiving(stockDetails.getStockSummary().getOpenGoodReceiving());
      movieAdapter.setUnavailableStock(
          stockDetails.getStockSummary().getBlockedStock()
              + stockDetails.getStockSummary().getReservation());
    }
    if (stockDetails.getOpenMovement() != null) {
      for (int i = 0; i < stockDetails.getOpenMovement().size(); i++) {
        if (stockDetails.getOpenMovement().get(i).getReason().equals("IST")) {
          openInterstoreQuantity = stockDetails.getOpenMovement().get(i).getQuantity();
        }
        if (stockDetails.getOpenMovement().get(i).getReason().equals("RET")) {
          openReturnQuantity = stockDetails.getOpenMovement().get(i).getQuantity();
        }
      }
      movieAdapter.setOpenInterstoreQuantity(openInterstoreQuantity);
      movieAdapter.setOpenReturnQuantity(openReturnQuantity);
    }
    if (stockDetails.getSystemStock() != null && !stockDetails.getSystemStock().isEmpty()) {
      setLastDateAttributes(stockDetails, movieAdapter);
    }
    if (stockDetails.getOpenOrders() != null) {
      movieAdapter.setOpenOrders(new ArrayList<>());
      for (Stock openOrder : stockDetails.getOpenOrders()) {
        movieAdapter
            .getOpenOrders()
            .add(
                new MovieOpenOrder(
                    openOrder.getDeliveryDate(),
                    openOrder.getQuantity(),
                    openOrder.getOrderDate()));
      }
    }
    if (stockDetails.getStockRelatedData() != null) {
      movieAdapter.setStockCoverage(stockDetails.getStockRelatedData().getStockCoverage());
      movieAdapter.setDailyMeanSales(stockDetails.getStockRelatedData().getDailyMeanSales());
    }
    if (stockDetails.getConsignmentStock() != null
        && !stockDetails.getConsignmentStock().isEmpty()) {
      if (stockDetails.getConsignmentStock().get(0).getLastChange() != null
          && !stockDetails.getConsignmentStock().get(0).getLastChange().isEmpty()) {
        movieAdapter.setConsignmentStock(stockDetails.getConsignmentStock().get(0).getQuantity());
        movieAdapter.setConsigStock(true);
      }
    }
    movieAdapter.setParkedStock(stockDetails.getParkedStock());

    if (stockDetails.getMovementDetails() != null
        && stockDetails.getMovementDetails().getSystemStock() != null) {
      movieAdapter.setTotalSaleQuantity(
          stockDetails.getMovementDetails().getSystemStock().getSaleQuantity());
      movieAdapter.setTotalGoodsReceivingQuantity(
          stockDetails.getMovementDetails().getSystemStock().getGoodsReceivingQuantity());
      movieAdapter.setTotalStockCorrectionQuantity(
          stockDetails.getMovementDetails().getSystemStock().getStockCorrectionQuantity());
    }

    if (stockDetails.getReservation() != null) {
      stockDetails
          .getReservation()
          .forEach(
              reservation -> {
                if ("COD picked".equals(reservation.getReservationType())) {
                  movieAdapter.setPickedStock(reservation.getQuantity());
                } else if ("COD confirmed".equals(reservation.getReservationType())) {
                  movieAdapter.setConfirmedStock(reservation.getQuantity());
                } else if ("MMS".equals(reservation.getReservationType())) {
                  movieAdapter.setMariStock(reservation.getQuantity());
                }
              });
    }

    return movieAdapter;
  }

  private void setLastDateAttributes(StockDetails stockDetails, MovieAdapter movieAdapter) {
    int lastGrDateIndex = 0;
    int lastSaleDateIndex = 0;
    int lastScdDateIndex = 0;
    String lastGRDateTemp = stockDetails.getSystemStock().get(0).getLastGRDate();
    String lastSaleDateTemp = stockDetails.getSystemStock().get(0).getLastSalesDate();
    String lastSCDDateTemp = stockDetails.getSystemStock().get(0).getLastSCDate();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
    for (int i = 1; i < stockDetails.getSystemStock().size(); i++) {
      if (!StringUtils.isBlank(lastGRDateTemp) && !StringUtils.isBlank(
          stockDetails.getSystemStock().get(i).getLastGRDate())) {
        try {
          if (simpleDateFormat.parse(lastGRDateTemp)
              .after(simpleDateFormat.parse(stockDetails.getSystemStock().get(i).getLastGRDate()))) {
            lastGRDateTemp = stockDetails.getSystemStock().get(i).getLastGRDate();
            lastGrDateIndex = i;
          }
        } catch (Exception error) {
          log.error(
              "Error when compare lastGRDateTemp {}", error.getMessage(), error);
        }
      }
      if (!StringUtils.isBlank(lastSaleDateTemp) && !StringUtils.isBlank(
          stockDetails.getSystemStock().get(i).getLastSalesDate())) {
        try {
          if (simpleDateFormat.parse(lastSaleDateTemp)
              .before(simpleDateFormat.parse(stockDetails.getSystemStock().get(i).getLastSalesDate()))) {
            lastSaleDateTemp = stockDetails.getSystemStock().get(i).getLastSalesDate();
            lastSaleDateIndex = i;
          }
        } catch (Exception error) {
          log.error(
              "Error when compare lastSaleDateTemp {}", error.getMessage(), error);
        }
      }
      if (!StringUtils.isBlank(lastSCDDateTemp) && !StringUtils.isBlank(
          stockDetails.getSystemStock().get(i).getLastSCDate())) {
        try {
          if (simpleDateFormat.parse(lastSCDDateTemp)
              .before(simpleDateFormat.parse(stockDetails.getSystemStock().get(i).getLastSCDate()))) {
            lastSCDDateTemp = stockDetails.getSystemStock().get(i).getLastSCDate();
            lastScdDateIndex = i;
          }
        } catch (Exception error) {
          log.error(
              "Error when compare lastSCDDateTemp {}", error.getMessage(), error);
        }
      }
    }
    movieAdapter.setLastGoodReceivingDate(lastGRDateTemp);
    movieAdapter.setLastSalesDate(lastSaleDateTemp);
    movieAdapter.setLastStockCorrectionDate(lastSCDDateTemp);
    movieAdapter.setLastGRQuantity(
        stockDetails.getSystemStock().get(lastGrDateIndex) != null
            ? stockDetails.getSystemStock().get(lastGrDateIndex).getLastGRQuantity()
            : null);
    movieAdapter.setLastSalesQuantity(
        stockDetails.getSystemStock().get(lastSaleDateIndex) != null
            ? stockDetails.getSystemStock().get(lastSaleDateIndex).getLastSalesQuantity()
            : null);
    movieAdapter.setLastSCQuantity(
        stockDetails.getSystemStock().get(lastScdDateIndex) != null
            ? stockDetails.getSystemStock().get(lastScdDateIndex).getLastSCQuantity()
            : null);
  }

  public MovieAdapter getArticleStockForVariantBundle(
      String countryCode,
      String salesLine,
      int storeNo,
      Long articleNumber,
      Long variant,
      Long bundleNumber) {
    MovieAdapter movieAdapterConsignment = new MovieAdapter();
    List<StockDetails> stockDetailsList =
        movieClient.getStockDetailsForArticleVariantBundle(
            countryCode, salesLine, storeNo, articleNumber, variant, bundleNumber);
    if (stockDetailsList != null && !stockDetailsList.isEmpty()) {
      for (StockDetails stockDetails : stockDetailsList) {
        if (stockDetails.getReturnCode() == 0) {
          movieAdapterConsignment = transformStockDetailToMovieAdapter(stockDetails);
          return movieAdapterConsignment;
        }
      }
    } else {
      return null;
    }
    return movieAdapterConsignment;
  }

  public List<MovieAdapter> getArticleStockListForVariantBundle(
      String countryCode, String salesLine, int storeNo, List<ArticleId> articleIdList) {
    List<StockDetails> stockDetailsList =
        movieClient.getStockDetailsForArticleVariantBundleList(
            countryCode, salesLine, storeNo, articleIdList);
    List<MovieAdapter> movieAdapterList = new ArrayList<>();
    if (stockDetailsList != null && !stockDetailsList.isEmpty()) {
      for (StockDetails stockDetails : stockDetailsList) {
        if (stockDetails.getReturnCode() == 0) {
          movieAdapterList.add(transformStockDetailToMovieAdapter(stockDetails));
        }
      }
    }
    return movieAdapterList;
  }
}
