package net.metrosystems.inventoryaggregator.articleattributes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockInformation implements Serializable {
  private static final long serialVersionUID = -5281084687617302420L;
  private Float availableStock; // should be Double
  private String lastGoodReceivingDate;
  private String lastStockCorrectionDate;
  private String lastSalesDate;
  private Double reservedStock;
  private Float stock; // should be Double
  private Double stockMarkdown;
  private Double unavailableStock;
  private Double blockedStock;
  private String stockCoverage;
  private String dailyMeanSales;
  private String dailyMeanSales30;
  private Float openInterstoreQuantity;
  private Float openReturnQuantity;
  private Double openGoodReceiving;
  private Double lastSCQuantity;
  private Double lastSalesQuantity;
  private Double lastGRQuantity;
  private boolean isConsigStock;
  private Float consignmentStock;
  private List<ParkedStock> parkedStock;
  private Float totalSaleQuantity;
  private Float totalGoodsReceivingQuantity;
  private Float totalStockCorrectionQuantity;
  private Float pickedStock;
  private Float confirmedStock;
  private Float mariStock;
}
