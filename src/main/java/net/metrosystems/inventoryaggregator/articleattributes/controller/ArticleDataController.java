package net.metrosystems.inventoryaggregator.articleattributes.controller;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.service.ArticleDetailsService;
import net.metrosystems.inventoryaggregator.articleattributes.v2.service.ArticleDetailsServiceV2;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import net.metrosystems.inventoryaggregator.config.logging.AddLogs;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.AddTimer;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.TagField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.metrics.annotation.Timed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed("ArticleData")
@RequiredArgsConstructor
@RequestMapping(
    value =
        "/inventoryaggregator/countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/stores/{storeno}/articles",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticleDataController {

  private final ArticleDetailsService articleDetailsService;
  private final ArticleDetailsServiceV2 articleDetailsServiceV2;

  @Value("#{'${countriesV2DataSource:null}'}")
  private List<String> countriesV2DataSource;

  @AddTimer
  @AddLogs
  @GetMapping(value = "/ean/{ean}", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<RawArticleData> getArticlesByEan(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
      @PathVariable("languageCode") String languageCode,
      @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
      @PathVariable("ean") String ean,
      @RequestParam("dataType") Optional<List<String>> dataTypeList)
      throws Exception {
    return articleDetailsService.getArticleDataListByEan(
        ean, countryCode, salesLine, languageCode, storeNo, dataTypeList);
  }

  @AddTimer
  @AddLogs
  @GetMapping(
      value = "/articlenumber/{articlenumber}/variant/{variant}/bundlenumber/{bundlenumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<RawArticleData> getArticleByArtVarBundle(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
      @PathVariable("languageCode") String languageCode,
      @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
      @PathVariable("articlenumber") Long articleNumber,
      @PathVariable("variant") Long variant,
      @PathVariable("bundlenumber") Long bundleNumber,
      @RequestParam("dataType") Optional<List<String>> dataTypeList)
      throws Exception {
    boolean dataSource =
        !isEmpty(countriesV2DataSource)
            && (countriesV2DataSource.contains(countryCode)
                || "ALL".equals(countriesV2DataSource.get(0)));

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
    if (dataSource) {
      return articleDetailsServiceV2.getArticleDataListByArticleVariantBundle(
          articleId, storeId, languageCode, dataTypeList);
    } else {
      return articleDetailsService.getArticleDataListByArticleVariantBundle(
          articleId, countryCode, salesLine, languageCode, storeNo, dataTypeList);
    }
  }

  @AddTimer
  @AddLogs
  @GetMapping(
      value = "/subsystemarticlenumber/{subsystemarticlenumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<RawArticleData> getArticleBySubsys(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
      @PathVariable("languageCode") String languageCode,
      @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
      @PathVariable("subsystemarticlenumber") Long subsystemArticleNumber,
      @RequestParam("dataType") Optional<List<String>> dataTypeList)
      throws Exception {
    return articleDetailsService.getArticleDataListBySubsys(
        subsystemArticleNumber, countryCode, salesLine, languageCode, storeNo, dataTypeList);
  }
}
