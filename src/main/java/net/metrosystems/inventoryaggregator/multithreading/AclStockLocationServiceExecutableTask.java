package net.metrosystems.inventoryaggregator.multithreading;

import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleStockLocation;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleBundle;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleVariant;
import net.metrosystems.inventoryaggregator.common.stock_location.acl.model.AclBundleStockLocation;
import net.metrosystems.inventoryaggregator.common.stock_location.acl.service.AclStockService;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Slf4j
public class AclStockLocationServiceExecutableTask implements Callable<ArticleVariant> {

	AclStockService aclStockService;   // autowired AclOrderingService object to call the ACL ordering Web Service
	String countryCode;                                // country code to be used in the request to the ACL ordering Web Service
	String salesLine;                                  // sales line to be used in the request to the ACL ordering Web Service
	Integer storeNo;                                   // store number to be used in the request to the ACL ordering Web Service
	Long articleNumber;                                // article number to be used in the request to the ACL ordering Web Service
	ArticleVariant articleVariant;                     // article variant number to be enhanced with ACL ordering Data

	// Public constructor that sets the attributes for the call to ACL ordering Web Service
	public AclStockLocationServiceExecutableTask(AclStockService aclStockService,
                                                 String countryCode,
                                                 String salesLine,
                                                 Integer storeNo,
                                                 Long articleNumber,
                                                 ArticleVariant articleVariant) {
		this.aclStockService = aclStockService;
		this.countryCode = countryCode;
		this.salesLine = salesLine;
		this.storeNo = storeNo;
		this.articleNumber = articleNumber;
		this.articleVariant = articleVariant;
	}


	// Getters section
	public String getCountryCode() {
		return countryCode;
	}

	public String getSalesLine() {
		return salesLine;
	}

	public Integer getStoreNo() {
		return storeNo;
	}

	public Long getArticleNumber() {
		return articleNumber;
	}

	public ArticleVariant getArticleVariant() {
		return articleVariant;
	}


	public String getIdAsString() {
		return this.countryCode + "-" + this.salesLine + "-" + this.storeNo.toString() + "-" +
				this.articleNumber.toString() + "-" + this.articleVariant.getVariantNumber().toString();
	}


	// Implementation of the mandatory method for the Callable interface
	// Calls the ACL stock location Web Service for the attributes of the current object
	// Returns the enhanced ArticleVariant object
	@Override
	public ArticleVariant call() throws Exception{
		List<AclBundleStockLocation> aclBundleStockLocations = new ArrayList<>();
		try {
			log.info("Thread " + this.getIdAsString() + " has started");
			aclBundleStockLocations = aclStockService.getAclBundleStockLocations(
					countryCode, salesLine, storeNo,
					this.articleNumber,	articleVariant.getVariantNumber());
			processAclStockLocationDataIntoArticleVariant(aclBundleStockLocations);
			return this.articleVariant;
		} catch(Exception e) {
			log.error("Thread {} error: {}", this.getIdAsString(), e.getMessage());
			return null;
		}
	}


	// Take the ACL ordering data received from the ACL Ordering Web Service
	// and map it to the appropriate attributes in the current article variant
	private void processAclStockLocationDataIntoArticleVariant(List<AclBundleStockLocation> aclBundleStockLocations) {
		int bundleIndex = 0;
		for(ArticleBundle articleBundle : articleVariant.getBundles()) {
			if (aclBundleStockLocations != null) {
				for(AclBundleStockLocation aclBundleStockLocation : aclBundleStockLocations) {
					if(articleBundle.getBundleNumber().equals(aclBundleStockLocation.getBundleNo())) {
						articleBundle.setStockLocations(transformAclBundleStockLocationsToArticleStockLocationListByBundle(
							aclBundleStockLocations,
							articleBundle.getBundleNumber()));
						articleVariant.getBundles().set(bundleIndex, articleBundle);
					}
				}
			}
			bundleIndex++;
			}
	}


	private List<ArticleStockLocation> transformAclBundleStockLocationsToArticleStockLocationListByBundle(
			List<AclBundleStockLocation> aclBundleStockLocations,
			Long bundleNo) {
		if ((aclBundleStockLocations != null)
              && (!aclBundleStockLocations.isEmpty())) {
			List<AclBundleStockLocation> aclStockLocationsForBundle = null;
			try {
				aclStockLocationsForBundle = aclBundleStockLocations.stream()
						.filter(aclBundleStockLocation -> (aclBundleStockLocation.getBundleNo().equals(bundleNo)))
						.collect(Collectors.toList());
			}catch (Exception e) {
				return null;
			}
			if ((aclStockLocationsForBundle != null)
                  &&(!aclStockLocationsForBundle.isEmpty())){
				List<ArticleStockLocation> articleStockLocationList = null;
				for (AclBundleStockLocation aclBundleStockLocation : aclStockLocationsForBundle) {
					ArticleStockLocation articleStockLocation = new ArticleStockLocation(
							aclBundleStockLocation.getBundleNo(),
							StringUtils.leftPad(String.valueOf(aclBundleStockLocation.getAisleNo()),3, "0"),
							StringUtils.leftPad(String.valueOf(aclBundleStockLocation.getBayNo()),3, "0"),
							aclBundleStockLocation.getQuantity(),
							aclBundleStockLocation.getBestBeforeDate(),
							aclBundleStockLocation.getDateOfCreation(),
							aclBundleStockLocation.getLastChangeDate(),
							aclBundleStockLocation.getSerialShippingContainerCode());
					if (articleStockLocationList == null) {
						articleStockLocationList = new ArrayList<ArticleStockLocation>(Arrays.asList(articleStockLocation));
					}else {
						articleStockLocationList.add(articleStockLocation);
					}
				}
				return articleStockLocationList;
			}
		}
		return null;
	}


	// Method to return a string output of all attributes
	@Override
	public String toString() {
		return "AclOrderingServiceExecutableTask [countryCode=" + countryCode + ", salesLine=" + salesLine
				+ ", storeNo=" + storeNo + ", articleNumber=" + articleNumber + ", variantNumber=" + articleVariant.getVariantNumber()
				+ "]";
	}

}
