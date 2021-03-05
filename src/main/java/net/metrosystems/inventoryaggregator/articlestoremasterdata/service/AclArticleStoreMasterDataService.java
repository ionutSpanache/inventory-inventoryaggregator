package net.metrosystems.inventoryaggregator.articlestoremasterdata.service;

import java.util.List;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.client.AclArticleStoreMasterDataClient;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AclArticleStoreMasterDataService {

	private final AclArticleStoreMasterDataClient aclArticleStoreMasterDataClient;

	@Autowired
	public AclArticleStoreMasterDataService(AclArticleStoreMasterDataClient aclArticleStoreMasterDataClient) {
		this.aclArticleStoreMasterDataClient = aclArticleStoreMasterDataClient;
	}

   public List<ArticleStoreMasterData> getArticleStoreMasterDataByEan(String countryCode, String salesLine, Integer storeNo,String barcode){

	  return aclArticleStoreMasterDataClient.getArticleStoreMasterDataByEan(countryCode, salesLine, storeNo, barcode);

  }

   public List<ArticleStoreMasterData> getArticleStoreMasterDataBySubsys(String countryCode, String salesLine, Integer storeNo, Integer subsys){

	 return aclArticleStoreMasterDataClient.getArticleStoreMasterDataBySubsys(countryCode, salesLine, storeNo, subsys);

   }

   public List<ArticleStoreMasterData> getArticleStoreMasterDataByArtVarBundle(String countryCode, String salesLine, Integer storeNo,
		                                                                    Integer articleNumber, Integer variantNumber, Integer bundleNumber){

    return aclArticleStoreMasterDataClient.getArticleStoreMasterDataByArtVarBundle(countryCode, salesLine, storeNo, articleNumber, variantNumber, bundleNumber);

   }

   public ArticleStoreMasterData getArticleStoreMasterData(String countryCode, String salesLine, Integer storeNo,
           Integer articleNumber, Integer variantNumber, Integer bundleNumber) {
	   List<ArticleStoreMasterData> articleStoreMasterDataList = getArticleStoreMasterDataByArtVarBundle(countryCode,salesLine, storeNo,articleNumber,variantNumber, bundleNumber);

	   for (ArticleStoreMasterData articleStoreMasterData : articleStoreMasterDataList) {
		 if  (articleStoreMasterData.getArticleNumber().intValue() == articleNumber.intValue() &&
			  articleStoreMasterData.getVariantNumber().intValue() == variantNumber.intValue()  &&
			  articleStoreMasterData.getBundleNumber().intValue() == bundleNumber.intValue())
		   return articleStoreMasterData;
	   }
	   return null;
   }
}

