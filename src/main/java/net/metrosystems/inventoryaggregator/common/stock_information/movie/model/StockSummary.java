package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockSummary {

    private Float availableStock;
    private Float availableVirtualStock;
    private Double blockedStock;
    private Float consignmentStock;
    private Double markDownStock;
    private Float normalStock;
    private Float openMovement;
    private Double reservation;
    private Float systemStock;
    private Double openGoodReceiving;
}
