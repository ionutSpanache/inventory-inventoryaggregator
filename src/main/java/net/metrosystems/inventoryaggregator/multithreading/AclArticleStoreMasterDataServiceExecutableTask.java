package net.metrosystems.inventoryaggregator.multithreading;

import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleData;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.service.AclArticleStoreMasterDataService;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleBundle;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleVariant;

@Slf4j
public class AclArticleStoreMasterDataServiceExecutableTask implements Callable<ArticleVariant>{

	private final AclArticleStoreMasterDataService aclArticleStoreMasterDataService;
	private String countryCode;
	private String salesLine;
	private Integer storeNo;
	private ArticleData articleData;
	private ArticleVariant articleVariant;

	public AclArticleStoreMasterDataServiceExecutableTask( AclArticleStoreMasterDataService aclArticleStoreMasterDataService,String countryCode, String salesLine, Integer storeNo,	ArticleData articleData,ArticleVariant articleVariant) {
     this.aclArticleStoreMasterDataService = aclArticleStoreMasterDataService;
     this.countryCode = countryCode;
     this.storeNo = storeNo;
     this.salesLine = salesLine;
	 this.articleData = articleData;
	 this.articleVariant = articleVariant;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getSalesLine() {
		return salesLine;
	}

	public void setSalesLine(String salesLine) {
		this.salesLine = salesLine;
	}

	public Integer getStoreNo() {
		return storeNo;
	}

	public void setStoreNo(Integer storeNo) {
		this.storeNo = storeNo;
	}


	public ArticleData getArticleData() {
		return articleData;
	}

	public void setArticleData(ArticleData articleData) {
		this.articleData = articleData;
	}

	public ArticleVariant getArticleVariant() {
		return articleVariant;
	}

	public void setArticleVariant(ArticleVariant articleVariant) {
		this.articleVariant = articleVariant;
	}

	public String getIdAsString() {
		return this.countryCode + "-" + this.salesLine + "-" + this.storeNo + "-" +
				this.articleData.getArticleNumber() + "-" + this.articleVariant.getVariantNumber().toString();
	}

	private void processAclArticleStoreMasterData() {
		ArticleStoreMasterData articleStoreMasterData = new ArticleStoreMasterData();
		int bundleIndex = 0;
		for(ArticleBundle articleBundle : articleVariant.getBundles()) {
			try {
				articleStoreMasterData = aclArticleStoreMasterDataService.getArticleStoreMasterData(countryCode, salesLine, storeNo,
                                                                                                    Math.toIntExact(this.articleData.getArticleNumber()),
						                                                                            Math.toIntExact(articleVariant.getVariantNumber()),
						                                                                            Math.toIntExact(articleBundle.getBundleNumber())
						                                                                            );


				if (articleStoreMasterData != null) {
					try {
					 articleData.setDepartmentNumber(Long.valueOf(articleStoreMasterData.getDepartmentNumber()));
					}catch(Exception e) {
					 log.error(e.getMessage(), e);
					}
					articleBundle.setPriorityAssortmentCode(articleStoreMasterData.getPriorityAssortmentCode());
					articleVariant.getBundles().set(bundleIndex, articleBundle);
				}
			}catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			bundleIndex++;
		}
	}

	@Override
	public ArticleVariant call() throws Exception{
		try {
			//logger.info("Thread " + this.getIdAsString() + " has started");
			processAclArticleStoreMasterData();
			return this.articleVariant;
		} catch(Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
}
