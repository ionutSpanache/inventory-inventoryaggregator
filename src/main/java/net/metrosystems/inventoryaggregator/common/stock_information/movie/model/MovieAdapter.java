package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieAdapter {

  private Long articleNo;
  private Long variantId;
  private Long bundleId;
  private Float stock;
  private String lastGoodReceivingDate;
  private String lastStockCorrectionDate;
  private String lastSalesDate;
  private Double blockedStock;
  private Double stockMarkDown;
  private Double reservedStock;
  private List<MovieOpenOrder> openOrders;
  private Double unavailableStock;
  private String stockCoverage;
  private Float availableStock;
  private String dailyMeanSales;
  private Float openInterstoreQuantity;
  private Float openReturnQuantity;
  private Double openGoodReceiving;
}
