package net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleComponent {
  private Integer articleNumber;
  private List<ArticleVariant> variantList;

}
