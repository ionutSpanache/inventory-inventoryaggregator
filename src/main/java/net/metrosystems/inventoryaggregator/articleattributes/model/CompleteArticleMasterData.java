package net.metrosystems.inventoryaggregator.articleattributes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteArticleMasterData {
    private ArticleMasterData articleMasterData;
    private RawArticleStoreMasterData articleStoreMasterData;
}
