package net.metrosystems.inventoryaggregator.rawarticledata.model;

import net.metrosystems.inventoryaggregator.rawarticledata.model.articlelocation.ArticleLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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


    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "RawArticleData [articleId=" + articleId + ", articleMasterData=" + articleMasterData
                + ", stockDetails=" + stockDetails + ", plannedDeliveryDates=" + plannedDeliveryDates
                + ", stockLocations=" + stockLocations + ", articleComponent=" + articleComponent + ", util=" + "]";
    }
}
