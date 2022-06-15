package net.metrosystems.inventoryaggregator.articleattributes.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.service.ArticleListDetailsService;
import net.metrosystems.inventoryaggregator.config.logging.AddLogs;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.AddTimer;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.TagField;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.metrics.annotation.Timed;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed("ArticleData")
@RequiredArgsConstructor
@RequestMapping(value = "/inventoryaggregator", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticleListDataController {

  private final ArticleListDetailsService articleListDetailsService;

  @AddTimer
  @AddLogs
  @PostMapping(
      value =
          "countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/stores/{storeno}/articles",
      consumes = "application/json",
      produces = "application/json")
  public ResponseEntity<?> getArticleByArtVarBundle(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
      @PathVariable("languageCode") String languageCode,
      @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
      @RequestBody List<ArticleId> articleIdList)
      throws Exception {
      return ResponseEntity.ok(
          articleListDetailsService.getArticleDataListByArticleVariantBundle(
              articleIdList, countryCode, salesLine, languageCode, storeNo));
  }

  @AddTimer
  @AddLogs
  @PostMapping(
          value =
                  "countries/{countryCode}/saleslines/{salesLine}/articles/isexcludedfromdiscounts",
          consumes = "application/json",
          produces = "application/json")
  public ResponseEntity<?> getIsTradeItemExcludedFromDiscountsByArtVarBundle(
          @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
          @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
          @RequestBody List<ArticleId> articleIdList) {
    return ResponseEntity.ok(
              articleListDetailsService.isTradeItemExcludedFromDiscounts(articleIdList, countryCode, salesLine));
  }

}
