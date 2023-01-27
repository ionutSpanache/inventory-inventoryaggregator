package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
public class MovieAverageSale {
  private ArticleVariantBundle avb;
  private String value;
}
