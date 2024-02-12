package net.metrosystems.inventoryaggregator.articlemasterdata.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterId;
import net.metrosystems.inventoryaggregator.articlemasterdata.service.ArticleMasterDataService;
import net.metrosystems.inventoryaggregator.config.logging.AddLogs;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/inventoryaggregator/api/", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Article master data controller")
public class ArticleMasterDataControllerV1 {

  private final ArticleMasterDataService articleMasterDataService;

  @AddLogs
  @GetMapping(
      value =
          "v1/countrycode/{countryCode}/languagecode/{languageCode}/storenumber/{storeNumber}/articlemasterdata/articlenumber/{articleNumber}/variantnumber/{variantNumber}/bundlenumber/{bundleNumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ArticleMasterData getArticleMasterDataByArtVarBundle(
      @PathVariable("countryCode") String countryCode,
      @PathVariable("languageCode") String languageCode,
      @PathVariable("storeNumber") Integer storeNo,
      @PathVariable("articleNumber") Long articleNumber,
      @PathVariable("variantNumber") Long variantNumber,
      @PathVariable("bundleNumber") Long bundleNumber)
      throws Exception {
    ArticleId articleId =
        ArticleId.builder()
            .articleNumber(articleNumber)
            .variantNumber(variantNumber)
            .bundleNumber(bundleNumber)
            .build();
    return articleMasterDataService.getArticleMasterIdForArticleId(
        articleId, countryCode, languageCode, storeNo);
  }

  @PostMapping(
      value =
          "v1/countrycode/{countryCode}/languagecode/{languageCode}/storenumber/{storeNumber}/articlemasterdata/articleids",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ArticleMasterId> getArticleMasterDataListByArtVarBundle(
      @PathVariable("countryCode") String countryCode,
      @PathVariable("languageCode") String languageCode,
      @PathVariable("storeNumber") Integer storeNumber,
      @RequestBody List<ArticleId> articleIds)
      throws Exception {
    return articleMasterDataService.getArticleMasterDataForArticleIdList(
        articleIds, countryCode, languageCode, storeNumber);
  }
}
