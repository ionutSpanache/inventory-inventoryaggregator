package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FilterIduItemDerivedStandard {

    private List<FilterKeyIduItemDerivedStandard> filterKeys;

    @JsonProperty("filter-keys")
    public List<FilterKeyIduItemDerivedStandard> getFilterKeys() {
        return filterKeys;
    }
}
