package net.metrosystems.inventoryaggregator.articleId.client.article_cache_aaa.filter_aaa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterKey {


    private String hardKeyDocument = "/erp_ihds_tree";
    private String country;
    private String salesLine;
    private String type = "readers-digest";
    private List<SubFilterKey> subFilterKeys;

    @JsonProperty("hard-key")
    public String getHardKey() {
        return "/" + country + hardKeyDocument + "?company=" + salesLine;
    }

    @JsonIgnore
    public String getHardKeyDocument() {
        return hardKeyDocument;
    }

    @JsonIgnore
    public String getCountry() {
        return country;
    }

    @JsonIgnore
    public String getSalesLine() {
        return salesLine;
    }

    public String getType() {
        return type;
    }

    @JsonProperty("sub-filter-keys")
    public List<SubFilterKey> getSubFilterKeys() {
        return subFilterKeys;
    }
}
