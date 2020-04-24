package net.metrosystems.inventoryaggregator.articleId.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.metrosystems.inventoryaggregator.articleId.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleId.service.ArticleIdService;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@RestController
@RequestMapping(value = "/inventoryaggregator")
@Slf4j
public class ArticleIdController {

    @Autowired
    private ArticleIdService articleIdService;

    @GetMapping(value = "/countries/{countryCode}/saleslines/{salesline}/languages/{languageCode}/stores/{storeno}/article-id/barcode/{barcode}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ArticleId>> getArticleIdByBarcode(
            @PathVariable("countryCode") String countryCode,
            @PathVariable("salesline") String salesLine,
            @PathVariable("languageCode") String languageCode,
            @PathVariable("storeno") Integer storeNo,
            @PathVariable("barcode") String barCode
    ) throws JsonProcessingException {
        List<ArticleId> articleIds = null;
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

    @GetMapping(value = "/countries/{countryCode}/saleslines/{salesline}/languages/{languageCode}/stores/{storeno}/article-id/subsys-article-nunmber/{subsys-article-number}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ArticleId>> getArticleIdBySubsysArtNo(
            @PathVariable("countryCode") String countryCode,
            @PathVariable("salesline") String salesLine,
            @PathVariable("languageCode") String languageCode,
            @PathVariable("storeno") Integer storeNo,
            @PathVariable("subsys-article-number") Long subsysArticleNumber
    ) throws JsonProcessingException {
        List<ArticleId> articleIds = null;
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


    @GetMapping(value = "/countries/{countryCode}/saleslines/{salesline}/languages/{languageCode}/stores/{storeno}/article-id/article-number/{articleNumber}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ArticleId>> getArticleIdByArticleNumber(
            @PathVariable("countryCode") String countryCode,
            @PathVariable("salesline") String salesLine,
            @PathVariable("languageCode") String languageCode,
            @PathVariable("storeno") Integer storeNo,
            @PathVariable("articleNumber") Long articleNumber
    ) throws JsonProcessingException {
        List<ArticleId> articleIds = null;
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
