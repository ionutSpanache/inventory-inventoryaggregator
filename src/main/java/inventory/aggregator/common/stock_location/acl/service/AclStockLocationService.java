package inventory.aggregator.common.stock_location.acl.service;

import inventory.aggregator.common.stock_location.acl.client.AclStockLocationClient;
import inventory.aggregator.common.stock_location.acl.model.AclBundleStockLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AclStockLocationService {
	private AclStockLocationClient aclStockLocationClient;
	
	@Autowired
	public AclStockLocationService(AclStockLocationClient aclStockLocationClient) {
		this.aclStockLocationClient = aclStockLocationClient;
	}
	
	public List<AclBundleStockLocation> getAclBundleStockLocations(
												String countryCode, String salesLine, Integer storeNo,
												Long articleNo, Long variantNo) {
		try {
			return aclStockLocationClient.getStockLocationsForArticleVariant(countryCode, salesLine, storeNo, articleNo, variantNo);
		}catch (Exception e) {
			return null;
		}
		
	}
}
