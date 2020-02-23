package inventory.aggregator.articlestoremasterdata.controller;

import inventory.aggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import inventory.aggregator.articlestoremasterdata.service.AclArticleStoreMasterDataService;
import inventory.aggregator.common.model.ArticleNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;


@RestController
public class ArticleStoreMasterDataController {

	@Autowired
	private AclArticleStoreMasterDataService aclArticleStoreMasterDataService;
	
	@RequestMapping(value="/countries/{countryCode}/saleslines/{salesLine}/stores/{storeNo}/barcode/{barcode}/articlemasterdatabyean",
	        method=RequestMethod.GET,
	        produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ArticleStoreMasterData>> getAclArticleStoreMasterDataByEan(
	             @PathVariable("countryCode") String countryCode,
	             @PathVariable("salesLine") String salesLine,
	             @PathVariable("storeNo") 	Integer storeNo,
	             @PathVariable("barcode") String barcode) {
	
	List<ArticleStoreMasterData> articleStoreMasterDataList = aclArticleStoreMasterDataService.getArticleStoreMasterDataByEan(countryCode, salesLine, storeNo, barcode);
	 if(articleStoreMasterDataList == null) {		
		return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(articleStoreMasterDataList);		
}
	@RequestMapping(value="/countries/{countryCode}/saleslines/{salesLine}/stores/{storeNo}/articleNumber/{artNo}/variantNumber/{varNo}/bundleNumber/{bundleNo}/articlemasterdatabyartvarbundle",
	        method=RequestMethod.GET,
	        produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ArticleStoreMasterData>> getAclArticleStoreMasterDataByArtVArBundle(
             @PathVariable("countryCode") String countryCode,
             @PathVariable("salesLine") String salesLine,
             @PathVariable("storeNo") 	Integer storeNo,
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