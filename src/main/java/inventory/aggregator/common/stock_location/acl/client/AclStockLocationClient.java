package inventory.aggregator.common.stock_location.acl.client;

import inventory.aggregator.common.stock_location.acl.model.AclBundleStockLocation;
import inventory.aggregator.common.stock_location.acl.model.PaginatedAclBundleStockLocation;
import inventory.aggregator.oauth.service.OAuth2ClientTokenSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class AclStockLocationClient {
	RestTemplate restTemplate;

	@Value("${ACL_STOCK_LOCATION_HOST}")
	private String aclStockLocationHost;
	@Value("${ACL_STOCK_LOCATION_PATH_FOR_VARIANT}")
	private String aclStockLocationPathForVariant;

	@Autowired
    OAuth2ClientTokenSevice oauth2ClientTokenSevice;

	public AclStockLocationClient() {
		restTemplate = new RestTemplate();
	}

	public List<AclBundleStockLocation> getStockLocationsForArticleVariant(String countryCode, String salesLine, Integer storeNo, Long articleNo, Long variantNo) {
		/*String token = oauth2ClientTokenSevice.getAccessToken();
		System.out.println("token bearer " + token);
		List<ClientHttpRequestInterceptor> interceptors = this.restTemplate.getInterceptors();
		if (CollectionUtils.isEmpty(interceptors)) {
			interceptors = new ArrayList<>();
		}
		interceptors.add(new OAuth2Interceptor(token));
		this.restTemplate.setInterceptors(interceptors);*/
		String urlInUse = String.format(aclStockLocationHost + aclStockLocationPathForVariant,
				countryCode.toUpperCase(), salesLine.toUpperCase(), storeNo, articleNo, variantNo);
		System.out.println("uriStockLocation=" + urlInUse);
		PaginatedAclBundleStockLocation paginatedAclBundleStockLocation = restTemplate.getForObject(urlInUse, PaginatedAclBundleStockLocation.class);
		try {
			//return Arrays.asList(restTemplate.getForObject(urlInUse, AclBundleStockLocation[].class));
			return paginatedAclBundleStockLocation.getStockLocations();
		} catch (Exception e) {
			return null;
		}

	}
}