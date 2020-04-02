package inventory.aggregator.common.stock_information.movie.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
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
