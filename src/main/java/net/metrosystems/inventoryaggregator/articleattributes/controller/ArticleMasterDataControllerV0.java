package net.metrosystems.inventoryaggregator.articleattributes.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.CompleteArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.service.ArticleDetailsService;
import net.metrosystems.inventoryaggregator.config.logging.AddLogs;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.AddTimer;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.TagField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(
    value =
        "/inventoryaggregator/countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/stores/{storeno}",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticleMasterDataControllerV0 {

  private final ArticleDetailsService articleDetailsService;

  @Autowired
  public ArticleMasterDataControllerV0(ArticleDetailsService articleDetailsService) {
    this.articleDetailsService = articleDetailsService;
  }

  @AddTimer
  @AddLogs
  @GetMapping(
      value =
          "/completearticlemasterdata/articlenumber/{articlenumber}/variant/{variant}/bundlenumber/{bundlenumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public CompleteArticleMasterData getArticleMasterDataByArtVarBundle(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
      @PathVariable("languageCode") String languageCode,
      @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
      @PathVariable("articlenumber") Long articleNumber,
      @PathVariable("variant") Long variant,
      @PathVariable("bundlenumber") Long bundleNumber)
      throws Exception {
    return articleDetailsService.getCompleteArticleMasterDataByArticleVariantBundle(
        articleNumber, variant, bundleNumber, countryCode, salesLine, languageCode, storeNo);
  }

  @AddTimer
  @AddLogs
  @PostMapping(
      value = "/articlemasterdata/articleids",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<RawArticleData> getArticleMasterDataListByArtVarBundle(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
      @PathVariable("languageCode") String languageCode,
      @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
      @RequestBody List<ArticleId> articleIds)
      throws Exception {
    return articleDetailsService.getArticleMasterDataListByArticleIds(
        articleIds, countryCode, salesLine, languageCode, storeNo);
  }
}
