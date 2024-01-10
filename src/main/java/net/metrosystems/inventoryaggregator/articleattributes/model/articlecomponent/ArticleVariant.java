package net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent;

import io.swagger.v3.oas.models.security.SecurityScheme.In;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleVariant {
  private Integer variantNumber;
  private List<ArticleBundle> bundleList;

}
