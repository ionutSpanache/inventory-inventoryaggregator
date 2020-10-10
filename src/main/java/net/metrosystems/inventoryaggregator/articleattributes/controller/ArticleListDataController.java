package net.metrosystems.inventoryaggregator.articleattributes.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.service.ArticleListDetailsService;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import net.metrosystems.inventoryaggregator.config.logging.AddLogs;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.AddTimer;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.TagField;
import net.metrosystems.inventoryaggregator.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.metrics.annotation.Timed;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestController
@Timed("ArticleData")
@RequiredArgsConstructor
@RequestMapping(value="/inventoryaggregator/countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/stores/{storeno}/articles", produces=MediaType.APPLICATION_JSON_VALUE)
public class ArticleListDataController {

    private final ArticleListDetailsService articleListDetailsService;

	@AddTimer
	@AddLogs
	@RequestMapping(value="/articlenumber/{articlenumber}/variant/{variant}/bundlenumber/{bundlenumber}", method=RequestMethod.POST)
	public ResponseEntity<?> getArticleByArtVarBundle(
			@TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
			@TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
			@PathVariable("languageCode") String languageCode,
			@TagField(name = "storeno") @PathVariable("storeno") Integer storeNo,
			@RequestBody List<ArticleId> articleIdList) throws Exception {

			try {
				return ResponseEntity.ok(articleListDetailsService.getArticleDataListByArticleVariantBundle(
						articleIdList, countryCode, salesLine, languageCode, storeNo));
			} catch (EntityNotFoundException e) {
				return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
			}
	}
	
	@ExceptionHandler(ArticleNotFoundException.class)
	public final ResponseEntity<String> handleUserNotFoundException(ArticleNotFoundException ex, WebRequest request) {		      
		return ResponseEntity.noContent().build();
	}

}
