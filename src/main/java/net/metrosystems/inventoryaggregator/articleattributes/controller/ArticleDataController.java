package net.metrosystems.inventoryaggregator.articleattributes.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.v2.service.ArticleDetailsServiceV2;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import net.metrosystems.inventoryaggregator.config.logging.AddLogs;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(
    value =
        "/inventoryaggregator/countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/stores/{storeno}/articles",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticleDataController {

  private final ArticleDetailsServiceV2 articleDetailsServiceV2;

  @AddLogs
  @GetMapping(
      value = "/articlenumber/{articlenumber}/variant/{variant}/bundlenumber/{bundlenumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<RawArticleData> getArticleByArtVarBundle(
      @PathVariable String countryCode,
      @PathVariable String salesLine,
      @PathVariable String languageCode,
      @PathVariable("storeno") Integer storeNo,
      @PathVariable("articlenumber") Long articleNumber,
      @PathVariable Long variant,
      @PathVariable("bundlenumber") Long bundleNumber,
      @RequestParam("dataType") Optional<List<String>> dataTypeList)
      throws Exception {

    ArticleId articleId =
        ArticleId.builder()
            .articleNumber(articleNumber)
            .variantNumber(variant)
            .bundleNumber(bundleNumber)
            .build();
    StoreId storeId =
        StoreId.builder()
            .countryCode(countryCode)
            .salesLine(salesLine)
            .storeNumber(storeNo)
            .build();
    return articleDetailsServiceV2.getArticleDataListByArticleVariantBundle(
        articleId, storeId, dataTypeList);
  }
}
