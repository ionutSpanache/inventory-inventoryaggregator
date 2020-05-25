package net.metrosystems.inventoryaggregator.articleattributes.controller;

import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.service.ArticleDetailsService;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import net.metrosystems.inventoryaggregator.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value="/inventoryaggregator/countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/stores/{storeno}/articles", produces=MediaType.APPLICATION_JSON_VALUE)
public class ArticleDataController {
	
	@Autowired
    private ArticleDetailsService articleDetailsService;
	
	@RequestMapping(value="/ean/{ean}", method=RequestMethod.GET)
	public ResponseEntity<?> getArticlesByEan(
			@PathVariable("countryCode") String countryCode,
			@PathVariable("salesLine") String salesLine,
			@PathVariable("languageCode") String languageCode,
			@PathVariable("storeno") Integer storeNo,
			@PathVariable("ean") String ean) throws Exception {

		//todo @Dacian to be delete when tests are finish
			List<RawArticleData> rawArticleDataList = null;
			try {
				rawArticleDataList = articleDetailsService.getArticleDataListByEan(
						ean,
						countryCode, salesLine, languageCode, storeNo);
			} catch (EntityNotFoundException e) {
				return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
			}
//			} catch (Exception e) {
//				return ResponseEntity.notFound().build();
//			}
			return ResponseEntity.ok(rawArticleDataList);
	}

	@RequestMapping(value="/articlenumber/{articlenumber}/variant/{variant}/bundlenumber/{bundlenumber}", method=RequestMethod.GET)
	public ResponseEntity<?> getArticleByArtVarBundle(
			@PathVariable("countryCode") String countryCode,
			@PathVariable("salesLine") String salesLine,
			@PathVariable("languageCode") String languageCode,
			@PathVariable("storeno") Integer storeNo,
			@PathVariable("articlenumber") Long articleNumber,
			@PathVariable("variant") Long variant,
			@PathVariable("bundlenumber") Long bundleNumber) throws Exception {

		//todo @Dacian to be delete when tests are finish

			List<RawArticleData> rawArticleDataList = null;
			try {
				rawArticleDataList = articleDetailsService.getArticleDataListByArticleVariantBundle(
						articleNumber, variant, bundleNumber,
						countryCode, salesLine, languageCode, storeNo);
			} catch (EntityNotFoundException e) {
				return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
			}
//			} catch (Exception e) {
//				return ResponseEntity.notFound().build();
//			}
			return ResponseEntity.ok(rawArticleDataList);
	}
	
	@RequestMapping(value="/subsystemarticlenumber/{subsystemarticlenumber}", method=RequestMethod.GET)
	public ResponseEntity<?> getArticleBySubsys(
			@PathVariable("countryCode") String countryCode,
			@PathVariable("salesLine") String salesLine,
			@PathVariable("languageCode") String languageCode,
			@PathVariable("storeno") Integer storeNo,
			@PathVariable("subsystemarticlenumber") Long subsystemArticleNumber) throws Exception {
		//todo @Dacian to be delete when tests are finish

			List<RawArticleData> rawArticleDataList = null;
			try {
				rawArticleDataList = articleDetailsService.getArticleDataListBySubsys(
						subsystemArticleNumber,
						countryCode, salesLine, languageCode, storeNo);
			} catch (EntityNotFoundException e) {
				return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
			}
//			} catch (Exception e) {
//				return ResponseEntity.notFound().build();
//			}
			return ResponseEntity.ok(rawArticleDataList);
	}
	
	@ExceptionHandler(ArticleNotFoundException.class)
	public final ResponseEntity<String> handleUserNotFoundException(ArticleNotFoundException ex, WebRequest request) {		      
		return ResponseEntity.notFound().build();
	}

}
