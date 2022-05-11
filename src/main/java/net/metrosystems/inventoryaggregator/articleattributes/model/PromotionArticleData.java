package net.metrosystems.inventoryaggregator.articleattributes.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionArticleData implements Serializable {

    private String promotionId;
    private Double promotionNetSalesPricePerBundle;
    private Boolean isActive;
}
