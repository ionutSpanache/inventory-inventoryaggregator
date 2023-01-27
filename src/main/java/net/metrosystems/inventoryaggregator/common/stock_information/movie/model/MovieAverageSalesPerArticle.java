package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
public class MovieAverageSalesPerArticle {
  private MovieLocation location;
  private List<MovieAverageSale> averages;
}
