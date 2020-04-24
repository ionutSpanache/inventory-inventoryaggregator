package net.metrosystems.inventoryaggregator.rawarticledata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleId{
	private Long articleNumber;
	private Long variantNumber;
	private Long bundleNumber;
}
