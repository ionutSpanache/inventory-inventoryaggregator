package net.metrosystems.inventoryaggregator.articleid.controller;

import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleid.service.ArticleIdService;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import net.metrosystems.inventoryaggregator.config.logging.AddLogs;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.TagField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/inventoryaggregator")
@Slf4j
public class ArticleIdController {

  private final ArticleIdService articleIdService;

  private final ArticleMasterDataRepository articleMasterDataRepository;

  @Autowired
  public ArticleIdController(
      ArticleIdService articleIdService, ArticleMasterDataRepository articleMasterDataRepository) {
    this.articleIdService = articleIdService;
    this.articleMasterDataRepository = articleMasterDataRepository;
  }

  @AddLogs
  @GetMapping(
      value =
          "/countries/{countryCode}/saleslines/{salesline}/languages/{languageCode}/stores/{storeno}/article-id/barcode/{barcode}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ArticleId>> getArticleIdByBarcode(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesline") @PathVariable("salesline") String salesLine,
      @PathVariable("languageCode") String languageCode,
      @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
      @PathVariable("barcode") String barCode) {
    List<ArticleId> articleIds;
    try {
      articleIds =
          articleIdService.getArticleIdByBarcode(
              countryCode, salesLine, languageCode, storeNo, barCode);
    } catch (Exception e) {
      throw new ArticleNotFoundException(e.getMessage(), e);
    }
    if (articleIds == null || articleIds.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(articleIds);
  }

  @AddLogs
  @GetMapping(
      value =
          "/countries/{countryCode}/saleslines/{salesline}/languages/{languageCode}/stores/{storeno}/article-id/subsys-article-nunmber/{subsys-article-number}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ArticleId>> getArticleIdBySubsysArtNo(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesline") @PathVariable("salesline") String salesLine,
      @PathVariable("languageCode") String languageCode,
      @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
      @PathVariable("subsys-article-number") Long subsysArticleNumber) {
    List<ArticleId> articleIds;
    try {
      articleIds =
          articleIdService.getArticleIdBySubsysArticleNumber(
              countryCode, salesLine, languageCode, storeNo, subsysArticleNumber);
    } catch (Exception e) {
      throw new ArticleNotFoundException(e.getMessage(), e);
    }
    if (articleIds == null || articleIds.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(articleIds);
  }

  @AddLogs
  @GetMapping(
      value =
          "/countries/{countryCode}/saleslines/{salesline}/languages/{languageCode}/stores/{storeno}/article-id/article-number/{articleNumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ArticleId>> getArticleIdByArticleNumber(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesline") @PathVariable("salesline") String salesLine,
      @PathVariable("languageCode") String languageCode,
      @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
      @PathVariable("articleNumber") Long articleNumber) {
    List<ArticleId> articleIds;
    try {
      articleIds =
          articleIdService.getArticleIdByArticleNumber(
              countryCode, salesLine, storeNo, articleNumber);
    } catch (Exception e) {
      throw new ArticleNotFoundException(e.getMessage(), e);
    }
    if (articleIds == null || articleIds.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(articleIds);
  }

  @AddLogs
  @GetMapping(
      value =
          "/countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/articlenumber/{articleNumber}/variant/{variant}/bundlenumber/{bundleNumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Integer>> getActiveStoresByByArtVarBundle(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
      @TagField(name = "languageCode") @PathVariable("languageCode") String languageCode,
      @PathVariable("articleNumber") Long articleNumber,
      @PathVariable("variant") Long variant,
      @PathVariable("bundleNumber") Long bundleNumber)
      throws Exception {

    ArticleId articleId =
        ArticleId.builder()
            .articleNumber(articleNumber)
            .variantNumber(variant)
            .bundleNumber(bundleNumber)
            .build();

    List<Integer> activeStoresList =
        articleIdService.getActiveStoresByByArtVarBundle(countryCode, salesLine, articleId);
    if (CollectionUtils.isEmpty(activeStoresList)) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(activeStoresList);
  }

  @AddLogs
  @PostMapping(
      value =
          "/countries/{countryCode}/salesline/{salesLine}/languages/{languageCode}/storeNumber/{storeNumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Set<ArticleId>> getActiveArticlesForStore(
      @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
      @TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
      @TagField(name = "languageCode") @PathVariable("languageCode") String languageCode,
      @TagField(name = "storeNumber") @PathVariable("storeNumber") Integer storeNumber,
      @RequestBody List<ArticleId> articleIdList)
      throws Exception {

    Set<ArticleId> activeArticles =
        articleMasterDataRepository
            .getArticleMasterDataForArticleIdList(
                articleIdList, countryCode, salesLine, languageCode, storeNumber)
            .get()
            .keySet();

    return ResponseEntity.ok(activeArticles);
  }
}
