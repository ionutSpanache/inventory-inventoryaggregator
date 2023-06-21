package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleFilterKey {

  private List<ArticleHardKey> subFilterKeys = new ArrayList<>();

  @JsonProperty("filter-keys")
  public List<ArticleHardKey> getSubFilterKeys() {
    return subFilterKeys;
  }
}
