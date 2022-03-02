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
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.GradeCodeReferenceFruitsVegetables;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IsTradeItemExcludedFromDiscounts;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master.IsuItemMaster;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheDataGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheReaderDigest;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleCacheFullData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleVariantBundleIdentifier;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import net.metrosystems.inventoryaggregator.common.promotions.acl.service.PromotionsAclService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArticleCacheService {

	@Value("${ARTICLE_CACHE_STORE_EXCEPTIONS_PATH}")
	private String articleCacheStoreExceptionsPath;

	@Value("${app.stage}")
	private String k8sEnvironment;

	private final DocumentsService documentsService;
	private final ArticleCacheAAAClient articleCacheClient;
	private final ArticleCacheStoreExceptionsClient articleCacheStoreExceptionsClient;
    private final PromotionsAclService promotionsAclService;

	private static final String PAKISTAN_COUNTRY_CODE = "PK";
	private static final Double ZERO = 0.0;

	private static final String IHDS_TREE = "erp_ihds_tree";
	private static final String BUNDLE_NO = "bundleno";
	private static final String MMS_ARTNO = "mmsartno";
	private static final String VARIANT_NO = "variantno";
	private static final String COMPANY = "company";
	private static final String ENVIRONMENT = "environment";
	private static final String ARTICLE_CACHE_PP2_ENVIRONMENT = "pp2";
	private static final String ARTICLE_CACHE_PP1_ENVIRONMENT = "pp1";
	private static final String ARTICLE_CACHE_PROD_ENVIRONMENT = "prod";
	private static final String K8S_DEV_ENVIRONMENT = "dev";
	private static final String K8S_PP_ENVIRONMENT = "pp";
	private static final String LOCAL_ENVIRONMENT = "local";
	private static final String LANGUAGE = "language";


	private static final Set<String> COUNTRY_CODES  = Set.of("IN", "CZ", "SK");

	@Autowired
	public ArticleCacheService(DocumentsService documentsService, ArticleCacheAAAClient articleCacheClient, ArticleCacheStoreExceptionsClient articleCacheStoreExceptionsClient, PromotionsAclService promotionsAclService) {
		this.documentsService = documentsService;
		this.articleCacheClient = articleCacheClient;
		this.articleCacheStoreExceptionsClient = articleCacheStoreExceptionsClient;
		this.promotionsAclService = promotionsAclService;
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

		GradeCodeReferenceFruitsVegetables gradeCodeReferenceFruitsVegetables =
				hasGradeCodeReferenceFruitsVegetables(articleUrlPath);

		return transformArticleCacheDocumentToArticleFullData(
												articleCacheClient.getArticleCacheDocumentByURL(articleUrlPath),
												storeNo, null, gradeCodeReferenceFruitsVegetables.getValue());

	}

	public Optional<IduItemDerivedStandard> getMultiLanguageDataByArtVarBundle(
			String countryCode, net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId articleId) {
		Documents articleUrlPathList = articleCacheClient.getMultiLanguageDocumentUrl(
				countryCode, articleId);
		if (CollectionUtils.isEmpty(articleUrlPathList.getArticleKeys())) {
			return Optional.empty();
		}
		try {
			return articleUrlPathList.getArticleKeys().stream().map(articleCacheClient::getIduItemDerivedStandardDocument)
							.filter(iduItemDerivedStandard -> iduItemDerivedStandard.getContentIduItemDerivedStandard().getVariantSortDescriptionV2() != null
							&& iduItemDerivedStandard.getContentIduItemDerivedStandard().getMerchandiseArticleDescription() != null)
							.findAny();
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
			return response.map(StoreInformationGroup::getChannelDeliveryFlag);
		}

		return Optional.empty();
	}

	private String buildIhdsTreeUrl(String countryCode, String salesLine, String languageCode, Long articleNumber, Long variantNumber,
									Long bundleNumber) {


		return  "/documents/" + countryCode.toLowerCase() +
				     "/"  + IHDS_TREE +
				     "?" + BUNDLE_NO + "=" + bundleNumber +
				     "&" + COMPANY   + "=" + salesLine.toLowerCase() +
				     "&" + ENVIRONMENT + "=" + getEnvironment(countryCode) +
				     "&" + LANGUAGE + "=" + languageCode.toLowerCase() +
				     "&" + MMS_ARTNO + "=" + articleNumber +
				     "&" + VARIANT_NO + "=" + variantNumber;
	}


	private String getEnvironment(String countryCode) {

		if (K8S_DEV_ENVIRONMENT.equalsIgnoreCase(k8sEnvironment) ||
				K8S_PP_ENVIRONMENT.equalsIgnoreCase(k8sEnvironment) ||
				LOCAL_ENVIRONMENT.equalsIgnoreCase(k8sEnvironment)) {

			if (COUNTRY_CODES.contains(countryCode)) {
				return ARTICLE_CACHE_PP2_ENVIRONMENT;
			} else {
				return ARTICLE_CACHE_PP1_ENVIRONMENT;
			}
		} else {
			return ARTICLE_CACHE_PROD_ENVIRONMENT;
		}

	}

  public ArticleCacheFullData getArticleCacheFullDataByArtVarBundleForArticleId(
      Long articleNumber,
      Long variantNumber,
      Long bundleNumber,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo) throws Exception {

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

	  List<String> articleKeysForDerivedStandard = getArticleKeysForDerivedStandardDocument(countryCode, articleNumber, bundleNumber,
			  variantNumber, salesLine);

	  IsTradeItemExcludedFromDiscounts isTradeItemExcludedFromDiscounts =
			  isTradeItemExcludedFromDiscounts(articleKeysForDerivedStandard);
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

		GradeCodeReferenceFruitsVegetables gradeCodeReferenceFruitsVegetables =
				hasGradeCodeReferenceFruitsVegetables(articleKeysForDerivedStandard.get(0));

    return transformArticleCacheDocumentToArticleFullData(
			articleCacheDocument, storeNo, isTradeItemExcludedFromDiscountsValue , gradeCodeReferenceFruitsVegetables.getValue());
  }

	public List<String> getArticleKeysForDerivedStandardDocument(String countryCode, Long articleNumber,
																 Long bundleNumber, Long variantNumber, String salesLine) {

		return documentsService.getIduItemDerivedUrlByArtVarBundle(
				countryCode,
				net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId.builder()
						.articleNumber(articleNumber)
						.bundleNumber(bundleNumber)
						.variantNumber(variantNumber)
						.build(),
				salesLine).getArticleKeys();
	}

  public IsTradeItemExcludedFromDiscounts isTradeItemExcludedFromDiscounts(List<String> articleKeys) {

    IsTradeItemExcludedFromDiscounts result = new IsTradeItemExcludedFromDiscounts();
    articleKeys
        .forEach(
            articleKey -> {
              IduItemDerivedStandard iduItemDerivedStandard =
                  articleCacheClient.getIduItemDerivedStandardDocument(articleKey);
              if (iduItemDerivedStandard != null
                  && iduItemDerivedStandard.getContentIduItemDerivedStandard() != null) {
                IsTradeItemExcludedFromDiscounts isTradeItemExcludedFromDiscounts =
                    iduItemDerivedStandard
                        .getContentIduItemDerivedStandard()
                        .getIsTradeItemExcludedFromDiscounts();
                if (isTradeItemExcludedFromDiscounts != null
                    && Boolean.TRUE.equals(isTradeItemExcludedFromDiscounts.getValue())) {
                  result.setValue(true);
                  if (result.getValidFrom() == null
                      || isTradeItemExcludedFromDiscounts
                          .getValidFrom()
                          .after(result.getValidFrom())) {
                    result.setValidFrom(isTradeItemExcludedFromDiscounts.getValidFrom());
                  }
                }
              }
            });
    return result;
  }

  public GradeCodeReferenceFruitsVegetables hasGradeCodeReferenceFruitsVegetables(String articleKey) {

		GradeCodeReferenceFruitsVegetables result = new GradeCodeReferenceFruitsVegetables();

		IduItemDerivedStandard iduItemDerivedStandard =
				articleCacheClient.getIduItemDerivedStandardDocument(articleKey);

		if(iduItemDerivedStandard != null
				&& iduItemDerivedStandard.getContentIduItemDerivedStandard() != null
				&& iduItemDerivedStandard.getContentIduItemDerivedStandard().getGradeCodeReferenceFruitsVegetables() != null ){
			result = iduItemDerivedStandard.getContentIduItemDerivedStandard().getGradeCodeReferenceFruitsVegetables();
		}

		return result;
	}

	private ArticleCacheFullData transformArticleCacheDocumentToArticleFullData (
																			ArticleCacheIhdsTree articleCacheDocument,
			                                                                Integer storeNo,
																			Boolean isTradeItemExcludedFromDiscounts , String gradeCodeReferenceFruitsVegetables) {
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

		List<String> promotionIdList  = articleCacheDocument.getTransaction().getArticleCacheIncident().getPromotionIdListWithStore(322, hierarchySequenceStoreNo);

    return new ArticleCacheFullData(
        articleCacheReaderDigest.getMmsArtNo(),
        articleCacheReaderDigest.getVariantNo(),
        articleCacheReaderDigest.getBundleNo(),
        articleCacheReaderDigest.getArticleDescription(),
        articleCacheDocument.getTransaction().getArticleCacheIncident().getBarcodeForBundle(),
        articleCacheReaderDigest.getProducingSupplier(),
        articleCacheDataGroup100.getPresentationMerchandiseGroup(),
        CollectionUtils.isEmpty(promotionIdList)
            ? null
            : getPromotionIdAvailable(
                articleCacheDocument, promotionIdList, storeNo),
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
        calculateMinimumRetailPrice(
            articleCacheDocument, storeNo, articleCacheDataGroup310.getSellingBundleContent()),
        articleCacheDataGroup200.getCountryOfOrigin(),
        isTradeItemExcludedFromDiscounts,
        gradeCodeReferenceFruitsVegetables,
        articleCacheDataGroup200.getPackageFactor(),
		articleCacheDataGroup100.getAlcoholClass(),
		articleCacheReaderDigest.getTraceabilityIndicator());
	}

  private String getPromotionIdAvailable(
      ArticleCacheIhdsTree articleCacheDocument, List<String> promotionIdList, Integer storeNo) {

    if (promotionIdList.size() > 1) {
      return promotionsAclService.getActivePromotion(
          articleCacheDocument, promotionIdList, storeNo);
    }
    return promotionIdList.get(0);
  }

	private MinimumRetailPrice calculateMinimumRetailPrice(ArticleCacheIhdsTree articleCacheIhdsTree, Integer storeNumber, Integer sellingBundleContent) {
		String countryCode = articleCacheIhdsTree.getTransaction().getArticleCacheIncident()
				.getArticleCacheDomain().getPlkz();

		if (PAKISTAN_COUNTRY_CODE.equalsIgnoreCase(countryCode)) {
			boolean hasMinimumRetailPrice = articleCacheIhdsTree.getTransaction()
					.getArticleCacheIncident().getArticleCacheDataGroupWithoutStore(100)
					.getHasMinimumRetailPrice();

			if (hasMinimumRetailPrice) {
				Double minimumRetailPriceForVatCalculation = articleCacheIhdsTree.getTransaction().getArticleCacheIncident()
						.getArticleCacheDataGroup().stream()
						.filter(articleCacheDataGroup -> articleCacheDataGroup.getStoreNo() == storeNumber)
						.findFirst()
						.map(ArticleCacheDataGroup::getMinimumRetailPriceForVatCalculation)
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
		return articleCacheStoreExceptionsClient.getArticleCacheDocumentByURL(articleUrlPath);
	}

  public List<Integer> getActiveStoresByByArtVarBundle(
      String countryCode, String salesLine, String languageCode, ArticleId articleId)
      throws Exception {

    List<Integer> activeStoresList;

    Documents articleUrlPathList =
        articleCacheClient.getArticleUrlPathByArtVarBundle(
            articleId.getArticleNumber(),
            articleId.getVariantNumber(),
            articleId.getBundleNumber(),
            countryCode,
            salesLine,
            languageCode);
    if (CollectionUtils.isEmpty(articleUrlPathList.getArticleKeys())) {
      throw new ArticleNotFoundException();
    }
    String articleUrlPath = articleUrlPathList.getArticleKeys().get(0);
    ArticleCacheIhdsTree articleCacheIhdsTree =
        articleCacheClient.getArticleCacheDocumentByURL(articleUrlPath);

    activeStoresList =
        articleCacheIhdsTree.getTransaction().getArticleCacheIncident().getArticleCacheDataGroup()
            .stream()
            .filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 320))
            .map(ArticleCacheDataGroup::getStoreNo)
            .collect(Collectors.toList());

    return activeStoresList;
  }
}
