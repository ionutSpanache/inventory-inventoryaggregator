package net.metrosystems.inventoryaggregator.common.article_information.articlecache.service;

import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.MinimumRetailPrice;
import net.metrosystems.inventoryaggregator.articleattributes.model.ProductLifeCycleData;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.service.DocumentsService;
import net.metrosystems.inventoryaggregator.articleid.model.ArticleId;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise.IsuItemStoreMerchandise;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise.StoreInformationGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.ArticleCacheStoreExceptionsClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_aaa.ArticleCacheAAAClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IsTradeItemExcludedFromDiscounts;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master.IsuItemMaster;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheDataGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheReaderDigest;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.*;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import net.metrosystems.inventoryaggregator.multithreading.ArticleDataThreads;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class ArticleCacheService {

	@Value("${ARTICLE_CACHE_STORE_EXCEPTIONS_PATH}")
	private String articleCacheStoreExceptionsPath;

	@Autowired
	DocumentsService documentsService;

	private final ArticleDataThreads articleDataThreads;

	@Autowired
	private ArticleCacheAAAClient articleCacheClient;
	private final ArticleCacheStoreExceptionsClient articleCacheStoreExceptionsClient;

	private static final String PAKISTAN_COUNTRY_CODE = "PK";
	private static final Double ZERO = 0.0;

	@Autowired
	public ArticleCacheService(ArticleDataThreads articleDataThreads, ArticleCacheStoreExceptionsClient articleCacheStoreExceptionsClient) {
		this.articleDataThreads = articleDataThreads;
		this.articleCacheStoreExceptionsClient = articleCacheStoreExceptionsClient;
	}

	public List<ArticleVariantBundleIdentifier> getArticleVariantBundleListForEan(
			String ean,
			String countryCode,
			String salesLine,
			String languageCode) throws Exception {
		return getArticleVariantBundleIdentifierListFromFilterUrl(articleCacheClient.getArticleUrlPathByEan(ean, countryCode, salesLine, languageCode));

	}

	public List<ArticleVariantBundleIdentifier> getArticleVariantBundleListForSubsys(
			Long subsysArtNo,
			String countryCode,
			String salesLine,
			String languageCode) throws Exception {
		return getArticleVariantBundleIdentifierListFromFilterUrl(
				articleCacheClient.getArticleUrlPathBySubsysArtNo(subsysArtNo, countryCode, salesLine, languageCode)
		);

	}

	public List<ArticleVariantBundleIdentifier> getArticleVariantBundleListForArticleVariantBundle(
			Long articleNumber,
			Long variantNumber,
			Long bundleNumber,
			String countryCode,
			String salesLine,
			String languageCode
	) throws Exception {
		return getArticleVariantBundleIdentifierListFromFilterUrl(
				articleCacheClient.getArticleUrlPathByArtVarBundle(articleNumber, variantNumber, bundleNumber, countryCode, salesLine, languageCode)
		);

	}

	public List<ArticleVariantBundleIdentifier> getArticleVariantBundleListForArticleNumber(
			Long articleNumber,
			String countryCode,
			String salesLine,
			String languageCode
	) throws Exception {

		return getArticleVariantBundleIdentifierListFromFilterUrl(
				articleCacheClient.getArticleUrlPathByMmsArtNo(articleNumber, countryCode, salesLine, languageCode)
		);

	}

	private List<ArticleVariantBundleIdentifier> getArticleVariantBundleIdentifierListFromFilterUrl(Documents filterUrlString) {
    if (filterUrlString == null
        || CollectionUtils.isEmpty(filterUrlString.getArticleKeys())) {
      return Collections.emptyList();
    }

		List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList =
				new ArrayList<>();
		for (String articleUrlPath : filterUrlString.getArticleKeys()) {
			ArticleId articleId = documentsService.tranformDocumentInArticleId(articleUrlPath);
			ArticleVariantBundleIdentifier articleVariantBundleIdentifier = new ArticleVariantBundleIdentifier();
			articleVariantBundleIdentifier.setArticleNumber(articleId.getArticleNumber());
			articleVariantBundleIdentifier.setVariantNumber(articleId.getVariantNumber());
			articleVariantBundleIdentifier.setBundleNumber(articleId.getBundleNumber());
			articleVariantBundleIdentifierList.add(articleVariantBundleIdentifier);
		}

		return articleVariantBundleIdentifierList;
	}

	public List<ArticleCacheFullData> getArticleCacheFullDataByEan(String ean,
                                                                   String countryCode, String salesLine, String languageCode, Integer storeNo) throws Exception {
		Documents articleUrlPathList = articleCacheClient.getArticleUrlPathByEan(ean, countryCode, salesLine, languageCode);
		if (articleUrlPathList.getArticleKeys().isEmpty()) {
			throw new ArticleNotFoundException();
		}
		List<ArticleCacheFullData> articleFullDataList = new ArrayList<>();
		for (String articleUrlPath : articleUrlPathList.getArticleKeys()) {
			articleFullDataList.add(
					transformArticleCacheDocumentToArticleFullData(
							articleCacheClient.getArticleCacheDocumentByURL(articleUrlPath),
							                                                storeNo, null));
		}
		return articleFullDataList;
	}

	public ArticleCacheFullData getArticleCacheFullDataByArtVarBundle(
			Long articleNumber,
			Long variantNumber,
			Long bundleNumber,
			String countryCode,
			String salesLine,
			String languageCode,
			Integer storeNo) throws Exception {
		Documents articleUrlPathList = articleCacheClient.getArticleUrlPathByArtVarBundle(
																articleNumber,
																variantNumber,
																bundleNumber,
																countryCode,
				salesLine,
																languageCode
																);
    if (CollectionUtils.isEmpty(articleUrlPathList.getArticleKeys())) {
      throw new ArticleNotFoundException();
    }
		String articleUrlPath = articleUrlPathList.getArticleKeys().get(0);
		return transformArticleCacheDocumentToArticleFullData(
												articleCacheClient.getArticleCacheDocumentByURL(articleUrlPath),
												storeNo, null);

	}

	public Optional<IduItemDerivedStandard> getMultiLanguageDataByArtVarBundle(
			String countryCode, net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId articleId) {
		Documents articleUrlPathList = articleCacheClient.getMultiLanguageDocumentUrl(
				countryCode, articleId);
		if (CollectionUtils.isEmpty(articleUrlPathList.getArticleKeys())) {
			return Optional.empty();
		}

		String articleUrlPath = articleUrlPathList.getArticleKeys().get(0);
		try {
			IduItemDerivedStandard iduItemDerivedStandard = articleCacheClient.getIduItemDerivedStandardDocument(articleUrlPath);
			return Optional.of(iduItemDerivedStandard);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public ProductLifeCycleData getArticleProductLifeCycleData(String countryCode, net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId articleId) {
		Documents articleUrlPathList = articleCacheClient.getProductLifeCycleDocumentUrl(countryCode, articleId);

		if (articleUrlPathList == null || CollectionUtils.isEmpty(articleUrlPathList.getArticleKeys())) {
			return new ProductLifeCycleData();
		}
		String articleUrlPath = articleUrlPathList.getArticleKeys().get(0);
		return mapToProductLifeCycleData(
				articleCacheClient.getProductLifeCycleData(articleUrlPath));

	}

	public Optional<String> getChannelDeliveryFlag(String countryCode, net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId articleId,
												   Integer storeNumber) {
		Documents articleUrlPathList = articleCacheClient.getChannelDeliveryFlagUrl(countryCode, articleId);

		if (articleUrlPathList == null || CollectionUtils.isEmpty(articleUrlPathList.getArticleKeys())) {
			return Optional.empty();
		}
		String articleUrlPath = articleUrlPathList.getArticleKeys().get(0);

		IsuItemStoreMerchandise isuItemStoreMerchandise = articleCacheClient.getChannelDeliveryFlagDocument(articleUrlPath);

		return mapToDeliveryFlagForStore(isuItemStoreMerchandise, storeNumber);

	}

	private Optional<String> mapToDeliveryFlagForStore(IsuItemStoreMerchandise isuItemStoreMerchandise, Integer storeNumber) {
		Optional<StoreInformationGroup> response = isuItemStoreMerchandise.getContentIsuItemStoreMerchandise().getStoreInformationGroups()
				== null ? Optional.empty() : isuItemStoreMerchandise.getContentIsuItemStoreMerchandise().getStoreInformationGroups()
				.stream().filter(storeInformationGroup -> storeInformationGroup.getGroupingId() == storeNumber)
				.findFirst();

		if(response.isPresent()) {
			return response.map(storeInformationGroup -> storeInformationGroup.getChannelDeliveryFlag());
		}

		return Optional.empty();
	}

  public ArticleCacheFullData getArticleCacheFullDataByArtVarBundleForArticleId(
      Long articleNumber,
      Long variantNumber,
      Long bundleNumber,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo)
      throws Exception {
    Documents articleUrlPathList =
        articleCacheClient.getArticleUrlPathByArtVarBundle(
            articleNumber, variantNumber, bundleNumber, countryCode, salesLine, languageCode);
    if (CollectionUtils.isEmpty(articleUrlPathList.getArticleKeys())) {
      return null;
    }

    String articleUrlPath = articleUrlPathList.getArticleKeys().get(0);
    ArticleCacheIhdsTree articleCacheDocument =
        articleCacheClient.getArticleCacheDocumentByURL(articleUrlPath);

    if (articleCacheDocument == null
        || articleCacheDocument.getTransaction() == null
        || articleCacheDocument.getTransaction().getArticleCacheIncident() == null
        || articleCacheDocument
                .getTransaction()
                .getArticleCacheIncident()
                .getHierarchySequenceStoreNo(storeNo)
            == null) {
      return null;
    }

    IsTradeItemExcludedFromDiscounts isTradeItemExcludedFromDiscounts =
        isTradeItemExcludedFromDiscounts(
            countryCode, articleNumber, bundleNumber, variantNumber, salesLine);
    boolean isTradeItemExcludedFromDiscountsValue = false;
    if (Boolean.TRUE.equals(isTradeItemExcludedFromDiscounts.getValue())) {
      if (isTradeItemExcludedFromDiscounts.getValidFrom() == null) {
        isTradeItemExcludedFromDiscountsValue = true;
      } else {
        LocalDate validFrom =
            isTradeItemExcludedFromDiscounts
                .getValidFrom()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        if (LocalDate.now().isAfter(validFrom)) {
          isTradeItemExcludedFromDiscountsValue = true;
        }
      }
    }
    return transformArticleCacheDocumentToArticleFullData(
			articleCacheDocument, storeNo, isTradeItemExcludedFromDiscountsValue);
  }

  public IsTradeItemExcludedFromDiscounts isTradeItemExcludedFromDiscounts(
      String countryCode,
      Long articleNumber,
      Long bundleNumber,
      Long variantNumber,
      String salesLine) {
    Documents iduItemDerivedUrlList =
        documentsService.getIduItemDerivedUrlByArtVarBundle(
            countryCode,
            net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId.builder()
                .articleNumber(articleNumber)
                .bundleNumber(bundleNumber)
                .variantNumber(variantNumber)
                .build(),
            salesLine);
    IduItemDerivedStandard iduItemDerivedStandard =
        articleCacheClient.getIduItemDerivedStandardDocument(
            iduItemDerivedUrlList.getArticleKeys().get(0));
    if (iduItemDerivedStandard == null
        || iduItemDerivedStandard.getContentIduItemDerivedStandard() == null
        || iduItemDerivedStandard
                .getContentIduItemDerivedStandard()
                .getIsTradeItemExcludedFromDiscounts()
            == null) {
      return new IsTradeItemExcludedFromDiscounts();
    }
    return iduItemDerivedStandard
        .getContentIduItemDerivedStandard()
        .getIsTradeItemExcludedFromDiscounts();
  }

	private ArticleCacheFullData transformArticleCacheDocumentToArticleFullData (
																			ArticleCacheIhdsTree articleCacheDocument,
			                                                                Integer storeNo,
																			Boolean isTradeItemExcludedFromDiscounts) {
		Integer hierarchySequenceStoreNo = articleCacheDocument.getTransaction().getArticleCacheIncident().getHierarchySequenceStoreNo(storeNo);
		if(hierarchySequenceStoreNo == null){
			throw new ArticleNotFoundException();
		}
		ArticleCacheReaderDigest articleCacheReaderDigest = articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest();
		ArticleCacheDataGroup articleCacheDataGroup100 = articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheDataGroupWithoutStore(100);
		ArticleCacheDataGroup articleCacheDataGroup300 = articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheDataGroupWithoutStore(300);
		ArticleCacheDataGroup articleCacheDataGroup310 = articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheDataGroupWithoutStore(310);
		ArticleCacheDataGroup articleCacheDataGroup320 = articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheDataGroupWithStore(320, hierarchySequenceStoreNo);
		ArticleCacheDataGroup articleCacheDataGroup325 = articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheDataGroupWithStoreRef(325, hierarchySequenceStoreNo);
		ArticleCacheDataGroup articleCacheDataGroup200 = articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheDataGroupWithoutStore(200);

		return new ArticleCacheFullData(
				articleCacheReaderDigest.getMmsArtNo(),
				articleCacheReaderDigest.getVariantNo(),
				articleCacheReaderDigest.getBundleNo(),
				articleCacheReaderDigest.getArticleDescription(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getBarcodeForBundle(),
				articleCacheReaderDigest.getProducingSupplier(),
				articleCacheDataGroup100.getPresentationMerchandiseGroup(),
				getPromotionIdAvailable(articleCacheDataGroup320),
				articleCacheDataGroup310.getSellingBundleContent(),
				articleCacheDataGroup100.getWeightArticles(),
				articleCacheReaderDigest.getSubsysNo(),
				articleCacheDataGroup100.getVatPercent(),
				articleCacheDataGroup320.getPieceNetSellPrice(),
				articleCacheDataGroup320.getBundleNetSellPrice(),
				articleCacheDataGroup320.getPromotionBundleNetPrice(),
				articleCacheReaderDigest.getVariantText(),
				articleCacheReaderDigest.getArticleType(),
				articleCacheDataGroup325.getArticleClass(),
				articleCacheDataGroup100.getFoodNonfoodIndicator(),
				articleCacheDataGroup100.getPresentationMainMerchandiseGroup(),
				articleCacheDataGroup100.getPresentationSubMerchandiseGroup(),
				articleCacheDataGroup320.getAssignedStoreSupplier(),
				articleCacheDataGroup325.getProcurementCd(),
				articleCacheDataGroup320.getAssortmentCode(),
				articleCacheDataGroup320.getBlockingCode(),
				articleCacheDataGroup320.getActivationStatus(),
				articleCacheDataGroup200.getExpiryDays(),
				articleCacheDataGroup325.getPieceNetBuyingPrice(),
				articleCacheDataGroup325.getBundleNetBuyingPrice(),
				articleCacheDataGroup325.getPieceNetNetBuyingPrice(),
				articleCacheDataGroup325.getBundleNetNetBuyingPrice(),
				articleCacheDataGroup300.getArticleNoEmptiesSales(),
				articleCacheReaderDigest.getBrandName(),
				calculateMinimumRetailPrice(articleCacheDocument, storeNo, articleCacheDataGroup310.getSellingBundleContent()),
				articleCacheDataGroup200.getCountryOfOrigin(),
				isTradeItemExcludedFromDiscounts);
	}

  private String getPromotionIdAvailable(ArticleCacheDataGroup articleCacheDataGroup320) {

    if (articleCacheDataGroup320.getPromotionEnd() != null) {
      LocalDate sysDate = LocalDate.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate promotionEnd =
          LocalDate.parse(articleCacheDataGroup320.getPromotionEnd(), formatter);

      if (promotionEnd.isBefore(sysDate)) {
        return null;
      }
    }

    return articleCacheDataGroup320.getPromotionId();
  }

	private MinimumRetailPrice calculateMinimumRetailPrice(ArticleCacheIhdsTree articleCacheIhdsTree, Integer storeNumber, Integer sellingBundleContent) {
		String countryCode = articleCacheIhdsTree.getTransaction().getArticleCacheIncident()
				.getArticleCacheDomain().getPlkz();

		if (PAKISTAN_COUNTRY_CODE.equalsIgnoreCase(countryCode)) {
			Boolean hasMinimumRetailPrice = articleCacheIhdsTree.getTransaction()
					.getArticleCacheIncident().getArticleCacheDataGroupWithoutStore(100)
					.getHasMinimumRetailPrice();

			if (hasMinimumRetailPrice == true) {
				Double minimumRetailPriceForVatCalculation = articleCacheIhdsTree.getTransaction().getArticleCacheIncident()
						.getArticleCacheDataGroup().stream()
						.filter(articleCacheDataGroup -> articleCacheDataGroup.getStoreNo() == storeNumber)
						.findFirst()
						.map(articleCacheDataGroup -> articleCacheDataGroup.getMinimumRetailPriceForVatCalculation())
						.orElse(ZERO);
				return MinimumRetailPrice.builder().indicator(Boolean.TRUE)
						.value(minimumRetailPriceForVatCalculation * sellingBundleContent)
						.build();
			}

			return MinimumRetailPrice.builder().indicator(Boolean.FALSE).value(ZERO).build();
		}

		return MinimumRetailPrice.builder().indicator(Boolean.FALSE).value(ZERO).build();
	}

	private ProductLifeCycleData mapToProductLifeCycleData(IsuItemMaster isuItemMaster) {
		if(isuItemMaster.getContent().getAssortmentClusterSpecification()  == null)
			return null;

		Long presencePeriodEndWeek = isuItemMaster.getContent().getAssortmentClusterSpecification().getPresencePeriodV2() == null ? null :
				isuItemMaster.getContent().getAssortmentClusterSpecification().getPresencePeriodV2()
						.getPresencePeriodEndWeek();

		Long presencePeriodEndYear = isuItemMaster.getContent().getAssortmentClusterSpecification().getPresencePeriodV2() == null ? null :
				isuItemMaster.getContent().getAssortmentClusterSpecification().getPresencePeriodV2()
						.getPresencePeriodEndYear();

		Long presencePeriodStartWeek = isuItemMaster.getContent().getAssortmentClusterSpecification().getPresencePeriodV2() == null ? null :
				isuItemMaster.getContent().getAssortmentClusterSpecification().getPresencePeriodV2()
						.getPresencePeriodStartWeek();

		Long presencePeriodStartYear = isuItemMaster.getContent().getAssortmentClusterSpecification().getPresencePeriodV2() == null ? null :
				isuItemMaster.getContent().getAssortmentClusterSpecification().getPresencePeriodV2()
						.getPresencePeriodStartYear();

		return  ProductLifeCycleData
				.builder()
				.assortmentCluster(isuItemMaster.getContent().getAssortmentClusterSpecification().getAssortmentCluster())
				.presencePeriodEndWeek(presencePeriodEndWeek)
				.presencePeriodEndYear(presencePeriodEndYear)
				.presencePeriodStartWeek(presencePeriodStartWeek)
				.presencePeriodStartYear(presencePeriodStartYear)
				.build();
	}


	public ArticleBasicData getArticleBasicDataByEan(String ean, String countryCode, String salesLine, String languageCode, int storeNo) throws Exception {
		return processDocumentsReceivedFromArticleCache(
				articleCacheClient.getArticleUrlPathByEan(ean, countryCode, salesLine, languageCode)
				, storeNo);
	}

	public ArticleBasicData getArticleBasicDataByMmsArtNo(Long mmsArtNo, String countryCode, String salesLine, String languageCode, int storeNo) throws Exception {
		return processDocumentsReceivedFromArticleCache(
				articleCacheClient.getArticleUrlPathByMmsArtNo(mmsArtNo, countryCode, salesLine, languageCode)
				, storeNo);
	}

	public ArticleBasicData getArticleBasicDataBySubsysArtNo(Long subsysArtNo, String countryCode, String salesLine, String languageCode, int storeNo) throws Exception {
		return processDocumentsReceivedFromArticleCache(
				articleCacheClient.
						getArticleUrlPathBySubsysArtNo(subsysArtNo, countryCode, salesLine, languageCode), storeNo);
	}

	private ArticleBasicData processDocumentsReceivedFromArticleCache(Documents articleUrlPathList, int storeNo) {
    if (CollectionUtils.isEmpty(articleUrlPathList.getArticleKeys())) {
      throw new ArticleNotFoundException();
    }
		String articleUrlPath = articleUrlPathList.getArticleKeys().get(0);
        ArticleId articleId = new ArticleId();
        String[] articleUrlPathObtaineArticleIdComponent = articleUrlPath.split("&");
		for (int i = 0; i < articleUrlPathObtaineArticleIdComponent.length; i++) {
			if (articleUrlPathObtaineArticleIdComponent[i].contains("bundleno")) {
				articleId.setBundleNumber(Long.valueOf(articleUrlPathObtaineArticleIdComponent[i].substring(articleUrlPathObtaineArticleIdComponent[i].indexOf("=") + 1)));
			}
			if (articleUrlPathObtaineArticleIdComponent[i].contains("variantno")) {
				articleId.setVariantNumber(Long.valueOf(articleUrlPathObtaineArticleIdComponent[i].substring(articleUrlPathObtaineArticleIdComponent[i].indexOf("=") + 1)));
			}
			if (articleUrlPathObtaineArticleIdComponent[i].contains("mmsartno")) {
				articleId.setArticleNumber(Long.valueOf(articleUrlPathObtaineArticleIdComponent[i].substring(articleUrlPathObtaineArticleIdComponent[i].indexOf("=") + 1)));
			}

		}

        ArticleCacheIhdsTree articleCacheDocument = articleCacheClient.getArticleCacheDocumentByURL(articleUrlPath);
        return transformArticleCacheDocumentToArticleBasicData(articleCacheDocument, storeNo, articleId);

    }



	private ArticleBasicData transformArticleCacheDocumentToArticleBasicData(ArticleCacheIhdsTree articleCacheDocument,
                                                                             int storeNo, ArticleId articleId) {
		return new ArticleBasicData(
				articleCacheDocument.getTransaction().getId(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getMmsArtNo(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getArticleDescription(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getAssignedStoreSupplierTypeForArticle(storeNo),
				//articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getMerchGroup(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getOwnBrand(),
                Math.toIntExact(articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getProducingSupplier()),
                articleId
				);
	}
	private List<ArticleVariant> addArticleCacheDocumentToArticleVariantList(List<ArticleVariant> articleVariantList,
                                                                             ArticleCacheIhdsTree articleCacheDocument,
                                                                             int storeNo) {
    	List<ArticleVariant> newArticleVariantList = articleVariantList;
    	boolean variantAlreadyExists = false;
    	if(!newArticleVariantList.isEmpty()) {
        	for(int i=0; i<newArticleVariantList.size(); i++) {
        		if(newArticleVariantList.get(i).getVariantNumber().equals(
								articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest()
																		.getVariantNo())){
        			newArticleVariantList.get(i).getBundles().add(transformArticleCacheDocumentToArticleBundle(articleCacheDocument, storeNo));
        			variantAlreadyExists = true;
        		}
        	}
    	}

		if(!variantAlreadyExists) {
    		try {
					newArticleVariantList.add(transformArticleCacheDocumentToArticleVariant(articleCacheDocument, storeNo));
				}catch (ArticleNotFoundException e) {

				}

		}
    	return newArticleVariantList;
    }
	private ArticleVariant transformArticleCacheDocumentToArticleVariant(ArticleCacheIhdsTree articleCacheDocument,
			                                                             int storeNo) throws ArticleNotFoundException{
		Integer hierarchySequenceStoreNo = articleCacheDocument.getTransaction().getArticleCacheIncident().getHierarchySequenceStoreNo(storeNo);
		if (hierarchySequenceStoreNo != null){
			return new ArticleVariant(
					articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getVariantNo(),
					articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getVariantText(),
					transformArticleCacheDocumentToArticleBundle(articleCacheDocument, storeNo));
		}else {
			throw new ArticleNotFoundException();
		}

	}

	private ArticleBundle transformArticleCacheDocumentToArticleBundle(ArticleCacheIhdsTree articleCacheDocument,
                                                                       int storeNo) {
		Integer hierarchySequenceStoreNo = articleCacheDocument.getTransaction().getArticleCacheIncident().getHierarchySequenceStoreNo(storeNo);
		Integer hierarchySequenceStoreNoForProcessing;
		if (hierarchySequenceStoreNo == null) {
			hierarchySequenceStoreNoForProcessing = -1;
		}else {
			hierarchySequenceStoreNoForProcessing = hierarchySequenceStoreNo;
		}
		return new ArticleBundle(
				articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getBundleNo(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getSubsysNo(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getBarcodeForBundle(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getMerchGroup(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getBundleProcurementCodeForStore(hierarchySequenceStoreNoForProcessing),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getBundleBlockingCodeForStore(hierarchySequenceStoreNoForProcessing),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getPromotionIdForStore(hierarchySequenceStoreNoForProcessing),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getSellingBundleIndicator(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getSellingBundleContent(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getPriceCheck(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getStatusForStore(hierarchySequenceStoreNoForProcessing),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getWeightArticle(hierarchySequenceStoreNoForProcessing),
				Math.toIntExact(articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getMerchGroupMain())
				);
	}

	public List<ArticleVariant> getArticleVariantsByArticleNumber(ArticleBasicData articleMasterData, String countryCode, String salesLine, String languageCode, int storeNo) throws Exception {
		List<ArticleVariant> articleVariantList = new ArrayList<>();
		Documents articleCacheDocumentURLs = articleCacheClient.findArticleUrlPathByMmsArtNo(articleMasterData.getArticleNumber(), countryCode, salesLine, languageCode);
        List<String> articleCacheDocumentURLsWithsearchedDBundleOnFirstPosition = new ArrayList<>();
        articleCacheDocumentURLsWithsearchedDBundleOnFirstPosition.add(null);
		for (String articleCacheDocumentURL : articleCacheDocumentURLs.getArticleKeys()
        ) {
			ArticleId articleId = documentsService.tranformDocumentInArticleId(articleCacheDocumentURL);
			if (articleId.getArticleNumber().equals(articleMasterData.getArticleId().getArticleNumber())
					&& articleId.getVariantNumber().equals(articleMasterData.getArticleId().getVariantNumber())
					&& articleId.getBundleNumber().equals(articleMasterData.getArticleId().getBundleNumber())) {
                articleCacheDocumentURLsWithsearchedDBundleOnFirstPosition.set(0, articleCacheDocumentURL);
            } else {
                articleCacheDocumentURLsWithsearchedDBundleOnFirstPosition.add(articleCacheDocumentURL);
            }

            //todo: check if this approach has better performance
		}
        //todo: check if this approach has better performance
        //todo:check here error for art 931988 001 001 in list are 5 but here 2 or 3 are readded

		for (ArticleCacheIhdsTree articleCacheDocument : articleDataThreads.getArticleCacheDocuments(articleCacheDocumentURLsWithsearchedDBundleOnFirstPosition)) {

            articleVariantList = addArticleCacheDocumentToArticleVariantList(articleVariantList, articleCacheDocument, storeNo);
        }

		return articleVariantList;
	}

	private List<ArticleCacheIhdsTree> getArticleCacheDocumentsByURLs(List<String> articleCacheDocumentURLs) {
		List<ArticleCacheIhdsTree> articleCacheDocuments = new ArrayList<>();
		for (String articleCacheDocumentURL : articleCacheDocumentURLs) {
			articleCacheDocuments.add(articleCacheClient.getArticleCacheDocumentByURL(articleCacheDocumentURL));
		}
		return articleCacheDocuments;
	}

	public ArticleCacheStoreExceptionsIhdsTree getArticleCacheStoreExceptions(
			Long articleNumber,
			Long variantNumber,
			Long bundleNumber,
			String countryCode,
			String salesLine,
			Integer storeNo) {

		String articleUrlPath = String.format(articleCacheStoreExceptionsPath,
				countryCode.toLowerCase(), bundleNumber, salesLine.toLowerCase(), "en", articleNumber, storeNo, variantNumber);
		//todo: change "en" to country code
		System.out.println("articleUrlPath: " + articleUrlPath);
		return articleCacheStoreExceptionsClient.getArticleCacheDocumentByURL(articleUrlPath);
	}

}
