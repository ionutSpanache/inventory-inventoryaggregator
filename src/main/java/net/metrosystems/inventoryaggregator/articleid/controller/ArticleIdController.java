package net.metrosystems.inventoryaggregator.articleid.controller;

import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleid.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleid.service.ArticleIdService;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import net.metrosystems.inventoryaggregator.config.logging.AddLogs;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.AddTimer;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.TagField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.metrics.annotation.Timed;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@RestController
@Timed("ArticleId")
@RequestMapping(value = "/inventoryaggregator")
@Slf4j
public class ArticleIdController {

    private final ArticleIdService articleIdService;

    @Autowired
    public ArticleIdController(ArticleIdService articleIdService) {
        this.articleIdService = articleIdService;
    }

    @AddTimer
    @AddLogs
    @GetMapping(value = "/countries/{countryCode}/saleslines/{salesline}/languages/{languageCode}/stores/{storeno}/article-id/barcode/{barcode}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ArticleId>> getArticleIdByBarcode(
            @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
            @TagField(name = "salesline") @PathVariable("salesline") String salesLine,
            @PathVariable("languageCode") String languageCode,
            @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
            @PathVariable("barcode") String barCode
    ) {
        List<ArticleId> articleIds;
        try {
            articleIds = articleIdService.getArticleIdByBarcode(countryCode, salesLine, languageCode, storeNo, barCode);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        if (articleIds == null || articleIds.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(articleIds);
    }

    @AddTimer
    @AddLogs
    @GetMapping(value = "/countries/{countryCode}/saleslines/{salesline}/languages/{languageCode}/stores/{storeno}/article-id/subsys-article-nunmber/{subsys-article-number}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ArticleId>> getArticleIdBySubsysArtNo(
            @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
            @TagField(name = "salesline") @PathVariable("salesline") String salesLine,
            @PathVariable("languageCode") String languageCode,
            @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
            @PathVariable("subsys-article-number") Long subsysArticleNumber
    ) {
        List<ArticleId> articleIds;
        try {
            articleIds = articleIdService.getArticleIdBySubsysArticleNumber(countryCode, salesLine, languageCode, storeNo, subsysArticleNumber);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        if (articleIds == null || articleIds.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(articleIds);
    }

    @AddTimer
    @AddLogs
    @GetMapping(value = "/countries/{countryCode}/saleslines/{salesline}/languages/{languageCode}/stores/{storeno}/article-id/article-number/{articleNumber}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ArticleId>> getArticleIdByArticleNumber(
            @TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
            @TagField(name = "salesline") @PathVariable("salesline") String salesLine,
            @PathVariable("languageCode") String languageCode,
            @TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
            @PathVariable("articleNumber") Long articleNumber
    ) {
        List<ArticleId> articleIds;
        try {
            articleIds = articleIdService.getArticleIdByArticleNumber(countryCode, salesLine, languageCode, storeNo, articleNumber);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        if (articleIds == null || articleIds.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(articleIds);
    }

    @ExceptionHandler(ArticleNotFoundException.class)
    public final ResponseEntity<String> handleUserNotFoundException(ArticleNotFoundException ex, WebRequest request) {
        return ResponseEntity.notFound().build();
    }

}
