package net.metrosystems.inventoryaggregator.articleattributes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlelocation.ArticleLocation;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RawArticleData implements Serializable {

    private static final long serialVersionUID = -4203872992143967958L;
    private ArticleId articleId;
    private ArticleMasterData articleMasterData;
    private RawArticleStoreMasterData articleStoreMasterData;
    private StockInformation stockDetails;
    private List<ArticlePlannedDeliveryDate> plannedDeliveryDates;
    private List<ArticleStockLocation> stockLocations;
    private ArticleLocation articleLocation;
    private ArticleComponent articleComponent;
    private ProductLifeCycleData productLifeCycleData;
    private MaximumRetailPrice maximumRetailPrice;

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "RawArticleData [articleId=" + articleId + ", articleMasterData=" + articleMasterData
                + ", stockDetails=" + stockDetails + ", plannedDeliveryDates=" + plannedDeliveryDates
                + ", stockLocations=" + stockLocations + ", articleComponent=" + articleComponent +
                ", maximumRetailPrice=" + maximumRetailPrice + ", util=" + "]";
    }
}
