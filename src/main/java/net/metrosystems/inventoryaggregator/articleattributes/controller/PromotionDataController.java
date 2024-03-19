package net.metrosystems.inventoryaggregator.articleattributes.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.PromotionArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.service.PromotionService;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import net.metrosystems.inventoryaggregator.config.logging.AddLogs;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.TagField;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/inventoryaggregator")
public class PromotionDataController {

  private final PromotionService promotionService;

  public PromotionDataController(PromotionService promotionService) {
    this.promotionService = promotionService;
  }

  @AddLogs
  @GetMapping(
      value =
          "/promotion/countryCode/{countryCode}/salesLine/{salesLine}/storeNumber/{storeNumber}/articleNumber/{articleNumber}/variantNumber/{variantNumber}/bundleNumber/{bundleNumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PromotionArticleData> getAllPromotionArticleData(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
      @PathVariable("storeNumber") Integer storeNumber,
      @PathVariable("articleNumber") Long articleNumber,
      @PathVariable("variantNumber") Long variantNumber,
      @PathVariable("bundleNumber") Long bundleNumber) {
    ArticleId articleId =
        ArticleId.builder()
            .articleNumber(articleNumber)
            .variantNumber(variantNumber)
            .bundleNumber(bundleNumber)
            .build();
    StoreId storeId =
        StoreId.builder()
            .countryCode(countryCode)
            .salesLine(salesLine)
            .storeNumber(storeNumber)
            .build();
    return promotionService.getAllArticlePromotionsForArticleId(articleId, storeId);
  }
}
