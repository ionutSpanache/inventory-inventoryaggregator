package inventory.aggregator.art_attributes.controller;

import inventory.aggregator.common.model.ArticleNotFoundException;
import inventory.aggregator.exception.EntityNotFoundException;
import inventory.aggregator.rawarticledata.model.ArticleMasterData;
import inventory.aggregator.rawarticledata.model.CompleteDiscountArticleMasterData;
import inventory.aggregator.rawarticledata.service.ArticleDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value="/countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/stores/{storeno}", produces=MediaType.APPLICATION_JSON_VALUE)
public class ArticleMasterDataController {
	
	@Autowired
    private ArticleDetailsService articleDetailsService;

	@RequestMapping(value="/articlemasterdata/articlenumber/{articlenumber}/variant/{variant}/bundlenumber/{bundlenumber}", method=RequestMethod.GET)
	public ResponseEntity<?> getArticleByArtVarBundle(
			@PathVariable("countryCode") String countryCode,
			@PathVariable("salesLine") String salesLine,
			@PathVariable("languageCode") String languageCode,
			@PathVariable("storeno") Integer storeNo,
			@PathVariable("articlenumber") Long articleNumber,
			@PathVariable("variant") Long variant,
			@PathVariable("bundlenumber") Long bundleNumber) throws Exception {

		//todo @Dacian to be delete when tests are finish

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

	@RequestMapping(value="/completearticlemasterdata/articlenumber/{articlenumber}/variant/{variant}/bundlenumber/{bundlenumber}", method=RequestMethod.GET)
	public ResponseEntity<?> getArticleMasterDataByArtVarBundle(
			@PathVariable("countryCode") String countryCode,
			@PathVariable("salesLine") String salesLine,
			@PathVariable("languageCode") String languageCode,
			@PathVariable("storeno") Integer storeNo,
			@PathVariable("articlenumber") Long articleNumber,
			@PathVariable("variant") Long variant,
			@PathVariable("bundlenumber") Long bundleNumber) throws Exception {

        CompleteDiscountArticleMasterData articleMasterData;
		try {
			articleMasterData = articleDetailsService.getCompleteArticleMasterDataByArticleVariantBundle(
					articleNumber, variant, bundleNumber,
					countryCode, salesLine, languageCode, storeNo);
		} catch (EntityNotFoundException e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(articleMasterData);
	}

	@ExceptionHandler(ArticleNotFoundException.class)
	public final ResponseEntity<String> handleUserNotFoundException(ArticleNotFoundException ex, WebRequest request) {
		return ResponseEntity.notFound().build();
	}

}
