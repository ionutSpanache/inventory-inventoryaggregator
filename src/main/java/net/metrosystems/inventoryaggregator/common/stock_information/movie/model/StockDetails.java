package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDetails {

    private String country;
    private String salesLine;
    private Integer locationId;
    private Long articleNo;
    private Long variant;
    private Long bundleNo;
    private String bundleContent;
    private List<Stock> blockedStock;
    private MovementDetails movementDetails;
    private List<Stock> openMovement;
    private List<Stock> openOrders;
    private List<Stock> reservation;
    private List<Stock> systemStock;
    private List<Stock> markdownStock;
    private List<Stock> virtualStock;
    private StockSummary stockSummary;
    private StockRelatedData stockRelatedData;
    private String message;
    private Integer returnCode;
}
