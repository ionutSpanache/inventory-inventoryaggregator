package net.metrosystems.inventoryaggregator.articleattributes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockInformation implements Serializable {
	private static final long serialVersionUID = -5281084687617302420L;
	private Float  availableStock;      // should be Double
	private String  lastGoodReceivingDate;
	private String  lastStockCorrectionDate;
	private String  lastSalesDate;
	private Double  reservedStock;
	private Float   stock;             // should be Double
	private Double  stockMarkdown;
	private Double  unavailableStock;
	private Double blockedStock;
	private String stockCoverage;
	private String dailyMeanSales;
    private Float openInterstoreQuantity;
    private Float openReturnQuantity; 

}
