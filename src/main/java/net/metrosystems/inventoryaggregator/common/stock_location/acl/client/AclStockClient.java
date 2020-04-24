package net.metrosystems.inventoryaggregator.common.stock_location.acl.client;

import net.metrosystems.inventoryaggregator.common.stock_location.acl.model.AclBundleStockLocation;
import net.metrosystems.inventoryaggregator.common.stock_location.acl.model.PaginatedAclBundleStockLocation;
import net.metrosystems.inventoryaggregator.oauth.service.OAuth2ClientTokenSevice;
import net.metrosystems.inventoryaggregator.rawarticledata.model.ArticleId;
import net.metrosystems.inventoryaggregator.rawarticledata.model.articlelocation.ArticleLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
public class AclStockClient {
	RestTemplate restTemplate;

	@Value("${ACL_STOCK_SERVICE_HOST}")
	private String aclStockLocationHost;
	@Value("${ACL_STOCK_LOCATION_PATH_FOR_VARIANT}")
	private String aclStockLocationPathForVariant;
	@Value("${ACL_STOCK_ARTICLE_LOCATION}")
	private String aclArticleLocationPath;

	@Autowired
	OAuth2ClientTokenSevice oauth2ClientTokenSevice;

	public AclStockClient() {
		restTemplate = new RestTemplate();
	}

	public List<AclBundleStockLocation> getStockLocationsForArticleVariant(String countryCode, String salesLine, Integer storeNo, Long articleNo, Long variantNo) {
		String urlInUse = String.format(aclStockLocationHost + aclStockLocationPathForVariant,
				countryCode.toUpperCase(), salesLine.toUpperCase(), storeNo, articleNo, variantNo);
		log.debug("uriStockLocation=" + urlInUse);
		PaginatedAclBundleStockLocation paginatedAclBundleStockLocation = restTemplate.getForObject(urlInUse, PaginatedAclBundleStockLocation.class);
		try {
			return paginatedAclBundleStockLocation.getStockLocations();
		} catch (Exception e) {
			return null;
		}

	}

	public ArticleLocation getArticleLocationsForArticleVariant(String countryCode, String salesLine, Integer storeNo, ArticleId articleId) {
		String urlInUse = String.format(aclStockLocationHost + aclArticleLocationPath,
				countryCode.toUpperCase(), salesLine.toUpperCase(), storeNo, articleId.getArticleNumber(), articleId.getVariantNumber(), articleId.getBundleNumber());
		log.debug("uriArticleLocation=" + urlInUse);

		try {
			ArticleLocation articleLocation = restTemplate.getForObject(urlInUse, ArticleLocation.class);
			return articleLocation;
		} catch (Exception e) {
			return null;
		}

	}
}