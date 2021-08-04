package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterIsuMaster {

    private List<FilterKeyIsuMaster> filterKeys;

    @JsonProperty("filter-keys")
    public List<FilterKeyIsuMaster> getFilterKeys() {
        return filterKeys;
    }

}
