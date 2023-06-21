package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleHardKey {

    private String country;
    private String salesLine;
    private String mmsArtNo;
    private String variantNumber=null;
    private String bundleNumber=null;

    @JsonProperty("hard-key")
    public String getHardKey() {
        String hardKey = String.format("/%s/erp_ihds_tree?company=%s&mmsArtNo=%s",
                country.toUpperCase(), salesLine.toLowerCase(), mmsArtNo);

        if (variantNumber!=null && bundleNumber!=null) {
            hardKey = String.format("%s&variantNo=%s&bundleNo=%s", hardKey, variantNumber, bundleNumber);
        }

        return hardKey;
    }

    @JsonIgnore
    public String getCountry() {
        return country;
    }

    @JsonIgnore
    public String getSalesLine() {
        return salesLine;
    }

    @JsonIgnore
    public String getMmsArtNo() {
        return mmsArtNo;
    }

    @JsonIgnore
    public String getVariantNumber() {
        return variantNumber;
    }

    @JsonIgnore
    public String getBundleNumber() {
        return bundleNumber;
    }
}
