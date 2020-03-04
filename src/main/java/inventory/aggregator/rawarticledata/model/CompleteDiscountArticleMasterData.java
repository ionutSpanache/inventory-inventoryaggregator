package inventory.aggregator.rawarticledata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteDiscountArticleMasterData {
    private ArticleMasterData articleMasterData;
    private DiscountArticleStoreMasterData discountArticleStoreMasterData;
}
