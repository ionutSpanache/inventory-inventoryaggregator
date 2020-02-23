package inventory.aggregator.rawarticledata.model;

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
    private ArticleComponent articleComponent;

//    public RawArticleData(ArticleId articleId, ArticleMasterData articleMasterData,
//                          StockInformation stockDetails, List<ArticlePlannedDeliveryDate> plannedDeliveryDates,
//                          List<ArticleStockLocation> stockLocations,
//                          ArticleComponent articleComponent, String findBy) {
//        super();
//        this.articleId = articleId;
//        this.articleMasterData = articleMasterData;
//        this.stockDetails = stockDetails;
//        this.plannedDeliveryDates = plannedDeliveryDates;
//        this.stockLocations = stockLocations;
//        this.articleComponent = articleComponent;
//    }

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
