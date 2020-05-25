package net.metrosystems.inventoryaggregator.articleattributes.controller;

import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.CompleteArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.service.ArticleDetailsService;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import net.metrosystems.inventoryaggregator.config.logging.AddLogs;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.AddTimer;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.TagField;
import net.metrosystems.inventoryaggregator.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.metrics.annotation.Timed;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@Slf4j
@RestController
@Timed("ArticleMasterData")
@RequestMapping(value="/inventoryaggregator/countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/stores/{storeno}", produces=MediaType.APPLICATION_JSON_VALUE)
public class ArticleMasterDataController {

    private final ArticleDetailsService articleDetailsService;

	@Autowired
	public ArticleMasterDataController(ArticleDetailsService articleDetailsService) {
		this.articleDetailsService = articleDetailsService;
	}

	@AddTimer
	@AddLogs
	@RequestMapping(value="/articlemasterdata/articlenumber/{articlenumber}/variant/{variant}/bundlenumber/{bundlenumber}", method=RequestMethod.GET)
	public ResponseEntity<?> getArticleMasterDataListByArtVarBundle(
			@TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
			@TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
			@PathVariable("languageCode") String languageCode,
			@TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
			@PathVariable("articlenumber") Long articleNumber,
			@PathVariable("variant") Long variant,
			@PathVariable("bundlenumber") Long bundleNumber) throws Exception {

			List<ArticleMasterData> articleMasterData = null;
			try {
				articleMasterData = articleDetailsService.getArticleMasterDataListByArticleVariantBundle(
						articleNumber, variant, bundleNumber,
						countryCode, salesLine, languageCode, storeNo);
			} catch (EntityNotFoundException e) {
				return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
			}
			return ResponseEntity.ok(articleMasterData);
	}

	@AddTimer
	@AddLogs
	@RequestMapping(value="/completearticlemasterdata/articlenumber/{articlenumber}/variant/{variant}/bundlenumber/{bundlenumber}", method=RequestMethod.GET)
	public ResponseEntity<?> getArticleMasterDataByArtVarBundle(
			@TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
			@TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
			@PathVariable("languageCode") String languageCode,
			@TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
			@PathVariable("articlenumber") Long articleNumber,
			@PathVariable("variant") Long variant,
			@PathVariable("bundlenumber") Long bundleNumber) throws Exception {

		CompleteArticleMasterData articleMasterData;
		try {
			articleMasterData = articleDetailsService.getCompleteArticleMasterDataByArticleVariantBundle(
					articleNumber, variant, bundleNumber,
					countryCode, salesLine, languageCode, storeNo);
		} catch (EntityNotFoundException e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.NO_CONTENT);
		}
		return ResponseEntity.ok(articleMasterData);
	}

	@ExceptionHandler(ArticleNotFoundException.class)
	public final ResponseEntity<String> handleUserNotFoundException(ArticleNotFoundException ex, WebRequest request) {
		return ResponseEntity.notFound().build();
	}

}
