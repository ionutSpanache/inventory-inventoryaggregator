package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class FilterKeyIduItemDerivedStandard {

    @JsonProperty("hard-key")
    private String hardKey;
    @JsonIgnore
    private String country;
    @Builder.Default
    private String type = "readers-digest";
    private List<SubFilterKey> subFilterKeys;

    public String getHardKey() {
        return country + "/idu-item-derived-standard";
    }

    @JsonProperty("sub-filter-keys")
    public List<SubFilterKey> getSubFilterKeys() {
        return subFilterKeys;
    }
}
