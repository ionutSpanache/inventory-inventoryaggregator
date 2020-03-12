package inventory.aggregator.rawarticledata.model.articlelocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StocktakingArticleLocation {

    private Date stocktakingDate;
    private Integer shelf;
    private Integer aisle;

    public Boolean isEmpty() {

        return this.stocktakingDate == null && this.shelf == null && this.aisle == null;

    }

}
