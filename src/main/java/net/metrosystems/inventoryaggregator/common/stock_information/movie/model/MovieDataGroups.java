package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties
public class MovieDataGroups {

    String dataGroup;
    Integer nextOpenOrders;
    List<String> reasons;

    public MovieDataGroups(String dataGroup, Integer nextOpenOrders) {
        this.dataGroup = dataGroup;
        this.nextOpenOrders = nextOpenOrders;
    }

    public MovieDataGroups(String dataGroup, List<String> reasons) {
        this.dataGroup = dataGroup;
        this.reasons = reasons;
    }

    public MovieDataGroups(String dataGroup) {
        this.dataGroup = dataGroup;
    }
}
