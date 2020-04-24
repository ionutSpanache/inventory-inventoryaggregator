package net.metrosystems.inventoryaggregator.common.stock_location.acl.service;

import net.metrosystems.inventoryaggregator.common.stock_location.acl.client.AclStockClient;
import net.metrosystems.inventoryaggregator.common.stock_location.acl.model.AclBundleStockLocation;
import net.metrosystems.inventoryaggregator.rawarticledata.model.ArticleId;
import net.metrosystems.inventoryaggregator.rawarticledata.model.articlelocation.ArticleLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AclStockService {
	private AclStockClient aclStockClient;
	
	@Autowired
	public AclStockService(AclStockClient aclStockClient) {
		this.aclStockClient = aclStockClient;
	}
	
	public List<AclBundleStockLocation> getAclBundleStockLocations(
												String countryCode, String salesLine, Integer storeNo,
												Long articleNo, Long variantNo) {
		try {
			return aclStockClient.getStockLocationsForArticleVariant(countryCode, salesLine, storeNo, articleNo, variantNo);
		}catch (Exception e) {
			return null;
		}
		
	}

	public ArticleLocation getAclArticleLocations(
			String countryCode, String salesLine, Integer storeNo, ArticleId articleId) {
		try {
			return aclStockClient.getArticleLocationsForArticleVariant(countryCode, salesLine, storeNo, articleId);
		}catch (Exception e) {
			return null;
		}
	}
}
