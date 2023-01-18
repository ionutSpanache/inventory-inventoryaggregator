package net.metrosystems.inventoryaggregator.common.article_information.articlecache.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsTradeItem {
    private Boolean value;
    private Date validFrom;
}
