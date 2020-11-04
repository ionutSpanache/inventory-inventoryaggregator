package net.metrosystems.inventoryaggregator.articleattributes.controller;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestController
@Timed("ArticleData")
@RequestMapping(value="/inventoryaggregator/countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/stores/{storeno}/articles", produces=MediaType.APPLICATION_JSON_VALUE)
public class ArticleDataController {

    private final ArticleDetailsService articleDetailsService;

	@Autowired
	public ArticleDataController(ArticleDetailsService articleDetailsService) {
		this.articleDetailsService = articleDetailsService;
	}

	@AddTimer
	@AddLogs
	@RequestMapping(value="/ean/{ean}", method=RequestMethod.GET)
	public ResponseEntity<?> getArticlesByEan(
			@TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
			@TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
			@PathVariable("languageCode") String languageCode,
			@TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
			@PathVariable("ean") String ean) throws Exception {

			try {
				return ResponseEntity.ok(articleDetailsService.getArticleDataListByEan(
						ean,
						countryCode, salesLine, languageCode, storeNo));
			} catch (EntityNotFoundException e) {
				return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
			}
	}
	@AddTimer
	@AddLogs
	@RequestMapping(value="/articlenumber/{articlenumber}/variant/{variant}/bundlenumber/{bundlenumber}", method=RequestMethod.GET)
	public ResponseEntity<?> getArticleByArtVarBundle(
			@TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
			@TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
			@PathVariable("languageCode") String languageCode,
			@TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
			@PathVariable("articlenumber") Long articleNumber,
			@PathVariable("variant") Long variant,
			@PathVariable("bundlenumber") Long bundleNumber) throws Exception {

			try {
				return ResponseEntity.ok(articleDetailsService.getArticleDataListByArticleVariantBundle(
						articleNumber, variant, bundleNumber,
						countryCode, salesLine, languageCode, storeNo));
			} catch (EntityNotFoundException e) {
				return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
			}
	}
	@AddTimer
	@AddLogs
	@RequestMapping(value="/subsystemarticlenumber/{subsystemarticlenumber}", method=RequestMethod.GET)
	public ResponseEntity<?> getArticleBySubsys(
			@TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
			@TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
			@PathVariable("languageCode") String languageCode,
			@TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
			@PathVariable("subsystemarticlenumber") Long subsystemArticleNumber) throws Exception {

			try {
				return ResponseEntity.ok(articleDetailsService.getArticleDataListBySubsys(
						subsystemArticleNumber,
						countryCode, salesLine, languageCode, storeNo));
			} catch (EntityNotFoundException e) {
				return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
			}
	}

	@ExceptionHandler(ArticleNotFoundException.class)
	public final ResponseEntity<String> handleUserNotFoundException(ArticleNotFoundException ex, WebRequest request) {
		return ResponseEntity.noContent().build();
	}

}
