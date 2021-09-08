package net.metrosystems.inventoryaggregator.articlestoremasterdata.client;

import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.MaximumRetailPrice;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.client.model.ArticleStoreMasterDataACLResponse;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Component
public class AclArticleStoreMasterDataClient {

	@Autowired
	RestTemplate restTemplate;

	@Qualifier("webClientForAcl")
	@Autowired
	private WebClient webClient;

	private final String exceptionMessage = "exceptie ";

	@Value("${ACL_STORE_MASTER_DATA_HOST}")
	private String aclArticleMasterDataHost;
	@Value("${ACL_STORE_MASTER_DATA_PATH_BY_EAN}")
	private String aclArticleMasterDataPathByEan;
	@Value("${ACL_STORE_MASTER_DATA_PATH_BY_SUBSYS}")
	private String aclArticleMasterDataPathBySubsys;
	@Value("${ACL_STORE_MASTER_DATA_PATH_BY_ART_VAR_BUNDLE}")
	private String aclArticleMasterDataPathByArtVarBundle;
	@Value("${ACL_MAXIMUM_RETAIL_PRICE_PATH}")
	private String maximumRetailPricePath;

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
			ArticleStoreMasterDataACLResponse articleStoreMasterDataACLResponse =
					restTemplate.getForObject(urlInUse, ArticleStoreMasterDataACLResponse.class);
			if(articleStoreMasterDataACLResponse != null
					&& !CollectionUtils.isEmpty(articleStoreMasterDataACLResponse.getArticleStoreMasterData()))
				return articleStoreMasterDataACLResponse.getArticleStoreMasterData();
			return Collections.emptyList();
		} catch (Exception e) {
			System.out.println(exceptionMessage + e);
			return Collections.emptyList();
		}

	}

	public MaximumRetailPrice getMaximumRetailPrice(
			String countryCode, String salesLine, Integer storeNumber, ArticleId articleId) {

		UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(
				aclArticleMasterDataHost + maximumRetailPricePath)
				.queryParam("articleNumber", articleId.getArticleNumber())
				.queryParam("variantNumber", articleId.getVariantNumber())
				.queryParam("bundleNumber", articleId.getBundleNumber())
				.buildAndExpand(countryCode, salesLine, storeNumber);

		return webClient.get()
				.uri(uriComponents.toString())
				.retrieve()
				.bodyToMono(MaximumRetailPrice.class)
				.block();

	}
}
