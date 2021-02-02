package net.metrosystems.inventoryaggregator.articlestoremasterdata.controller;

import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.service.AclArticleStoreMasterDataService;
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
@Timed("ArticleStoreMasterData")
@RequestMapping(value = "/inventoryaggregator")
public class ArticleStoreMasterDataController {

	private final AclArticleStoreMasterDataService aclArticleStoreMasterDataService;

	@Autowired
	public ArticleStoreMasterDataController(AclArticleStoreMasterDataService aclArticleStoreMasterDataService) {
		this.aclArticleStoreMasterDataService = aclArticleStoreMasterDataService;
	}

	@AddTimer
	@AddLogs
	@GetMapping(value="/countries/{countryCode}/saleslines/{salesLine}/stores/{storeNo}/barcode/{barcode}/articlemasterdatabyean",
	        produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ArticleStoreMasterData>> getAclArticleStoreMasterDataByEan(
			@TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
			@TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
			@TagField(name = "storeNo") @PathVariable("storeNo") 	Integer storeNo,
			@PathVariable("barcode") String barcode) {

		List<ArticleStoreMasterData> articleStoreMasterDataList = aclArticleStoreMasterDataService.getArticleStoreMasterDataByEan(countryCode, salesLine, storeNo, barcode);
		if(articleStoreMasterDataList == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(articleStoreMasterDataList);
	}

	@AddTimer
	@AddLogs
	@GetMapping(value="/countries/{countryCode}/saleslines/{salesLine}/stores/{storeNo}/articleNumber/{artNo}/variantNumber/{varNo}/bundleNumber/{bundleNo}/articlemasterdatabyartvarbundle",
	        produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ArticleStoreMasterData>> getAclArticleStoreMasterDataByArtVArBundle(
			@TagField(name = "countryCode") @PathVariable("countryCode") String countryCode,
			@TagField(name = "salesLine") @PathVariable("salesLine") String salesLine,
			@TagField(name = "storeNo") @PathVariable("storeNo") 	Integer storeNo,
			@PathVariable("artNo")Integer artNo,
            @PathVariable("varNo")Integer varNo,
            @PathVariable("bundleNo")Integer bundleNo ) {
        
		List<ArticleStoreMasterData> articleStoreMasterDataList = aclArticleStoreMasterDataService.getArticleStoreMasterDataByArtVarBundle(countryCode, salesLine, storeNo, artNo,varNo,bundleNo);
        if(articleStoreMasterDataList == null) {		
	     return ResponseEntity.notFound().build();
        }
       return ResponseEntity.ok(articleStoreMasterDataList);		
    }
	
	@ExceptionHandler(ArticleNotFoundException.class)
	public final ResponseEntity<String> handleUserNotFoundException(ArticleNotFoundException ex, WebRequest request) {		      
		return ResponseEntity.notFound().build();
	}
}