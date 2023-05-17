package net.metrosystems.inventoryaggregator.articleattributes.controller;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.service.ArticleDetailsService;
import net.metrosystems.inventoryaggregator.config.logging.AddLogs;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.AddTimer;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.TagField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.metrics.annotation.Timed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed("ArticleData")
@RequestMapping(
    value =
        "/inventoryaggregator/countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/stores/{storeno}/articles",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticleDataController {

  private final ArticleDetailsService articleDetailsService;

  @Value("#{'${countriesV2DataSource:null}'}")
  private  List<String> countriesV2DataSource;

  public ArticleDataController(ArticleDetailsService articleDetailsService) {
    this.articleDetailsService = articleDetailsService;
  }

  @AddTimer
  @AddLogs
  @GetMapping(value = "/ean/{ean}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getArticlesByEan(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
      @PathVariable("languageCode") String languageCode,
      @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
      @PathVariable("ean") String ean)
      throws Exception {
    return ResponseEntity.ok(
        articleDetailsService.getArticleDataListByEan(
            ean, countryCode, salesLine, languageCode, storeNo));
  }

  @AddTimer
  @AddLogs
  @GetMapping(
      value = "/articlenumber/{articlenumber}/variant/{variant}/bundlenumber/{bundlenumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getArticleByArtVarBundle(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
      @PathVariable("languageCode") String languageCode,
      @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
      @PathVariable("articlenumber") Long articleNumber,
      @PathVariable("variant") Long variant,
      @PathVariable("bundlenumber") Long bundleNumber)
      throws Exception {
    boolean dataSource = false;
    if (!isEmpty(countriesV2DataSource) && countriesV2DataSource.contains(countryCode)) {
      dataSource = true;
    }

    return ResponseEntity.ok(
        articleDetailsService.getArticleDataListByArticleVariantBundle(
            articleNumber,
            variant,
            bundleNumber,
            countryCode,
            salesLine,
            languageCode,
            storeNo,
            dataSource));
  }

  @AddTimer
  @AddLogs
  @GetMapping(
      value = "/subsystemarticlenumber/{subsystemarticlenumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getArticleBySubsys(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
      @PathVariable("languageCode") String languageCode,
      @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
      @PathVariable("subsystemarticlenumber") Long subsystemArticleNumber)
      throws Exception {
    return ResponseEntity.ok(
        articleDetailsService.getArticleDataListBySubsys(
            subsystemArticleNumber, countryCode, salesLine, languageCode, storeNo));
  }
}
