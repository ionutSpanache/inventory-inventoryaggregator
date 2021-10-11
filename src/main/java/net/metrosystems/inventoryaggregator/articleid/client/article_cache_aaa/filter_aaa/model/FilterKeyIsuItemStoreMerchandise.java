package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterKeyIsuItemStoreMerchandise {

    @JsonProperty("hard-key")
    private String hardKey;
    @JsonIgnore
    private String countryCode;
    @JsonIgnore
    private Long articleNumber;
    @JsonIgnore
    private Long bundleNumber;
    @JsonIgnore
    private Long variantNumber;

    public String getHardKey() {
        return "/" + countryCode + "/isu-item-store-merchandise?company=mcc&articlenumber=" + articleNumber
                + "&variantnumber=" + variantNumber
                + "&bundlenumber=" + bundleNumber;
    }
}
