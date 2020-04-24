package net.metrosystems.inventoryaggregator.articleId.client.article_cache_aaa.document.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Documents {
    private List<String> articleKeys;
    private String nextPageToken;

    @JsonProperty("article-keys")
    public void setArticleKeys(List<String> articleKeys) {
        this.articleKeys = articleKeys;
    }

    @JsonProperty("next-page-token")
    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
}
