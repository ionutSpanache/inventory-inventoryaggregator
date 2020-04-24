package net.metrosystems.inventoryaggregator.articleId.client.article_cache_aaa.filter_aaa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Filter {

    private List<FilterKey> filterKeys;

    @JsonProperty("filter-keys")
    public List<FilterKey> getFilterKeys() {
        return filterKeys;
    }
}
