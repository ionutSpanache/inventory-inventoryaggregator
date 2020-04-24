package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties
public class Article {

    Long articleNo;
    Long variant;
    List<Bundle> bundles;
}
