package net.metrosystems.inventoryaggregator.articleattributes.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDataFromMovie {
	private StockInformation articleStockData;
	private List<ArticlePlannedDeliveryDate> articlePlannedDeliveryDates;

}
