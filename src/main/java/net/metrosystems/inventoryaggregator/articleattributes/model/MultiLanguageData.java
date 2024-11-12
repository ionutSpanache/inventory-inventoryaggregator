package net.metrosystems.inventoryaggregator.articleattributes.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiLanguageData {
  private List<LanguageData> articleDescriptions;
  private List<VariantDescription> variantDescriptions;
  private List<LanguageData> presentationMerchandiseGroupDescriptions;
  private List<LanguageData> presentationMainMerchandiseGroupDescriptions;
  private List<LanguageData> presentationSubMerchandiseGroupDescriptions;
}
