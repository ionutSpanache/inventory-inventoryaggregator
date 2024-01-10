package net.metrosystems.inventoryaggregator.articleattributes.controller;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.PromotionArticleDataForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawIsTradeItemExcludedFromDiscounts;
import net.metrosystems.inventoryaggregator.articleattributes.service.ArticleListDetailsService;
import net.metrosystems.inventoryaggregator.articleattributes.v2.service.ArticleDetailsServiceV2;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import net.metrosystems.inventoryaggregator.config.logging.AddLogs;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.AddTimer;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.TagField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.metrics.annotation.Timed;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed("ArticleData")
@RequiredArgsConstructor
@RequestMapping(value = "/inventoryaggregator", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticleListDataController {

  private final ArticleListDetailsService articleListDetailsService;
  private final ArticleDetailsServiceV2 articleDetailsServiceV2;

  @Value("#{'${countriesV2DataSource:null}'}")
  private List<String> countriesV2DataSource;

  @AddLogs
  @PostMapping(
      value =
          "countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/stores/{storeno}/articles",
      consumes = "application/json",
      produces = "application/json")
  public List<RawArticleData> getArticleByArtVarBundle(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
      @PathVariable("languageCode") String languageCode,
      @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
      @RequestParam("dataType") Optional<List<String>> dataTypeList,
      @RequestBody List<ArticleId> articleIdList)
      throws Exception {

    boolean dataSource =
        !isEmpty(countriesV2DataSource) && (countriesV2DataSource.contains(countryCode) || "ALL".equals(countriesV2DataSource.get(0)));
    if (dataSource) {
      return articleDetailsServiceV2.getArticleDataListByArticleVariantBundle(
          articleIdList, countryCode, salesLine, storeNo, dataTypeList);
    } else {
      return articleListDetailsService.getArticleDataListByArticleVariantBundle(
          articleIdList, countryCode, salesLine, languageCode, storeNo, dataTypeList);
    }
  }

  @AddLogs
  @PostMapping(
      value = "countries/{countryCode}/saleslines/{salesLine}/articles/isexcludedfromdiscounts",
      consumes = "application/json",
      produces = "application/json")
  public List<RawIsTradeItemExcludedFromDiscounts>
      getIsTradeItemExcludedFromDiscountsByArtVarBundle(
          @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
          @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
          @RequestBody List<ArticleId> articleIdList) {
    return articleListDetailsService.isTradeItemExcludedFromDiscounts(
        articleIdList, countryCode, salesLine);
  }

  @AddLogs
  @PostMapping(
      value =
          "country/{countryCode}/salesline/{salesLine}/storenumber/{storeNumber}/articles/future-promotions",
      consumes = "application/json",
      produces = "application/json")
  public List<PromotionArticleDataForArticleId> getFuturePromotionsByArticleIds(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
      @TagField(name = "storeNumber") @PathVariable("storeNumber") Integer storeNumber,
      @RequestBody List<ArticleId> articleIdList) {
    return articleListDetailsService.getFuturePromotionsForArticleIds(
        articleIdList,
        StoreId.builder()
            .countryCode(countryCode)
            .salesLine(salesLine)
            .storeNumber(storeNumber)
            .build());
  }
}
