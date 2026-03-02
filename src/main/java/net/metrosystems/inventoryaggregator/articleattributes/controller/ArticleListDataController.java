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
@RequestMapping(value = "/inventoryaggregator", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticleListDataController {

  private final ArticleDetailsServiceV2 articleDetailsServiceV2;

  @AddLogs
  @PostMapping(
      value =
          "countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/stores/{storeno}/articles",
      consumes = "application/json",
      produces = "application/json")
  public List<RawArticleData> getArticleByArtVarBundle(
      @PathVariable String countryCode,
      @PathVariable String salesLine,
      @PathVariable String languageCode,
      @PathVariable("storeno") Integer storeNo,
      @RequestParam("dataType") Optional<List<String>> dataTypeList,
      @RequestBody List<ArticleId> articleIdList)
      throws Exception {

    log.debug("languageCode: {}", languageCode);
    StoreId storeId = new StoreId(countryCode, salesLine, storeNo);
    return articleDetailsServiceV2.getArticleDataListByArticleVariantBundle(
        articleIdList, storeId, dataTypeList);
  }
}
