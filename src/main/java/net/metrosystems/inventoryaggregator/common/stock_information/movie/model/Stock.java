package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Stock {


    private Float quantity;
    private String lotNo;
    private String blockedReason;
    private String reservationType;
    private Integer virtualStockType;
    private Float processedQuantity;
    private Float reservedQuantity;
    private String deliveryDate;
    private String orderNumber;
    private String orderDate;
    private Integer promotionId;
    private Integer supplierNumber;
    private String lastGRDate;
    private String lastSalesDate;
    private String lastSCDate;
    private String reason;
    private Double lastSCQuantity;
    private Double lastSalesQuantity;
    private Double lastGRQuantity;
}
