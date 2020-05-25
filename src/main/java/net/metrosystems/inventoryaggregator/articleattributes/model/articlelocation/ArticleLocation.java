package net.metrosystems.inventoryaggregator.articleattributes.model.articlelocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleLocation {

    private List<SpacemanArticleLocation> spacemanArticleLocationList;
    private List<StocktakingArticleLocation> stocktakingArticleLocationList;

}
