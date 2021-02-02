package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubFilterKey {

    private String key;
    private String type;
    private String query;
    private String language;
    private List<InListOfRange> inListOfRange;

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public String getQuery() {
        return query;
    }

    public String getLanguage() {
        return language;
    }

    @JsonProperty("inlistofranges")
    public List<InListOfRange> getInListOfRange() {
        return inListOfRange;
    }
}
