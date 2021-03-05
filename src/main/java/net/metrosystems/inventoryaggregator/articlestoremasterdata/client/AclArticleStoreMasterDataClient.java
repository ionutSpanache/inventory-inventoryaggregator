package net.metrosystems.inventoryaggregator.articlestoremasterdata.client;

import java.util.List;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.client.model.ArticleStoreMasterDataACLResponse;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AclArticleStoreMasterDataClient {

	@Autowired
	RestTemplate restTemplate;

	private final String exceptionMessage = "exceptie ";

	@Value("${ACL_STORE_MASTER_DATA_HOST}")
	private String aclArticleMasterDataHost;
	@Value("${ACL_STORE_MASTER_DATA_PATH_BY_EAN}")
	private String aclArticleMasterDataPathByEan;
	@Value("${ACL_STORE_MASTER_DATA_PATH_BY_SUBSYS}")
	private String aclArticleMasterDataPathBySubsys;
	@Value("${ACL_STORE_MASTER_DATA_PATH_BY_ART_VAR_BUNDLE}")
	private String aclArticleMasterDataPathByArtVarBundle;

	private String aclArticleStoreMasterDataUrl;

	public List<ArticleStoreMasterData> getArticleStoreMasterDataByEan(String countryCode, String salesLine,
                                                                       Integer storeNo, String barcode) {

		aclArticleStoreMasterDataUrl = aclArticleMasterDataHost + aclArticleMasterDataPathByEan;
		String urlInUse = String.format(aclArticleStoreMasterDataUrl, countryCode.toUpperCase(),
				salesLine.toUpperCase(), storeNo, barcode);

		try {
			return restTemplate.getForObject(urlInUse, ArticleStoreMasterDataACLResponse.class)
					.getArticleStoreMasterData();
		} catch (Exception e) {
			System.out.println(exceptionMessage + e);
			return null;
		}
	}

	public List<ArticleStoreMasterData> getArticleStoreMasterDataBySubsys(String countryCode, String salesLine,
			Integer storeNo, Integer subsys) {

    aclArticleStoreMasterDataUrl = aclArticleMasterDataHost + aclArticleMasterDataPathBySubsys;
		String urlInUse = String.format(aclArticleStoreMasterDataUrl, countryCode.toUpperCase(),
				salesLine.toUpperCase(), storeNo, subsys);
		System.out.println(urlInUse);

		try {
			return restTemplate.getForObject(urlInUse, ArticleStoreMasterDataACLResponse.class)
					.getArticleStoreMasterData();
		} catch (Exception e) {
			System.out.println(exceptionMessage + e);
			return null;
		}

	}

	public List<ArticleStoreMasterData> getArticleStoreMasterDataByArtVarBundle(String countryCode, String salesLine,
			Integer storeNo, Integer articleNumber, Integer variantNumber, Integer bundleNumber) {

    aclArticleStoreMasterDataUrl = aclArticleMasterDataHost + aclArticleMasterDataPathByArtVarBundle;
		String urlInUse = String.format(aclArticleStoreMasterDataUrl, countryCode.toUpperCase(),
				salesLine.toUpperCase(), storeNo, articleNumber, variantNumber, bundleNumber);
		System.out.println("uri=" + urlInUse);

		try {
			return restTemplate.getForObject(urlInUse, ArticleStoreMasterDataACLResponse.class)
					.getArticleStoreMasterData();
		} catch (Exception e) {
			System.out.println(exceptionMessage + e);
			return null;
		}

	}
}
