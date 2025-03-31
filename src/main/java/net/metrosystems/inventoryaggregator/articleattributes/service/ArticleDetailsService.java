package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticlePlannedDeliveryDate;
import net.metrosystems.inventoryaggregator.articleattributes.model.CompleteArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.LanguageData;
import net.metrosystems.inventoryaggregator.articleattributes.model.MultiLanguageData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.StockInformation;
import net.metrosystems.inventoryaggregator.articleattributes.model.VariantDescription;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.GeneralDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.v2.service.ArticleProcessingServiceV2;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise.StoreInformationGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.ContentIduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.Domain;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.MerchandiseArticleDescription;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import net.metrosystems.inventoryaggregator.common.movieacl.MovieAclService;
import net.metrosystems.inventoryaggregator.stocklocation.model.StockLocation;
import net.metrosystems.inventoryaggregator.stocklocation.repository.StockLocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleDetailsService {
  private static final String MERCHANDISE_GROUP_INFORMATION = "merchandiseGroupInformation";
  private static final Map<String, String> LANGUAGE_CODE_EXCEPTIONS = new HashMap<>();

  static {
    LANGUAGE_CODE_EXCEPTIONS.put("UA", "UK");
    LANGUAGE_CODE_EXCEPTIONS.put("KZ", "RU");
    LANGUAGE_CODE_EXCEPTIONS.put("MD", "RO");
    LANGUAGE_CODE_EXCEPTIONS.put("PK", "UR");
    LANGUAGE_CODE_EXCEPTIONS.put("RS", "SR");
    LANGUAGE_CODE_EXCEPTIONS.put("CZ", "CS");
  }

  private final ArticleCacheService articleCacheService;
  private final ArticleMasterDataRepository articleMasterDataRepository;
  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;
  private final GeneralDataRepository generalDataRepository;
  private final ArticleProcessingService articleProcessingService;
  private final StockLocationRepository stockLocationRepository;
  private final ArticleProcessingServiceV2 articleProcessingServiceV2;
  private final MovieAclService movieAclService;

  public List<RawArticleData> getArticleDataListByArticleVariantBundle(
      ArticleId articleId,
      StoreId storeId,
      String languageCode,
      Optional<List<String>> dataTypeList)
      throws Exception {
    return getRawArticleDataListForArticleVariantBundleIdentifierList(
        articleId,
        storeId.getCountryCode(),
        storeId.getSalesLine(),
        languageCode,
        storeId.getStoreNumber(),
        dataTypeList);
  }

  public CompleteArticleMasterData getCompleteArticleMasterDataByArticleVariantBundle(
      Long articleNumber,
      Long variantNumber,
      Long bundleNumber,
      StoreId storeId,
      String languageCode)
      throws Exception {

    ArticleId articleId =
        ArticleId.builder()
            .articleNumber(articleNumber)
            .variantNumber(variantNumber)
            .bundleNumber(bundleNumber)
            .build();

    CompletableFuture<ArticleMasterData> articleMasterData = new CompletableFuture<>();

    try {
      articleMasterData =
          articleMasterDataRepository.getArticleMasterData(
              articleId,
              storeId.getCountryCode(),
              storeId.getSalesLine(),
              languageCode,
              storeId.getStoreNumber());
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    CompletableFuture<RawArticleStoreMasterData> discountArticleStoreMasterData =
        new CompletableFuture<>();
    try {
      discountArticleStoreMasterData =
          rawArticleStoreMasterDataRepository.getDiscountArticleStoreMasterData(
              articleId,
              storeId.getCountryCode(),
              storeId.getSalesLine(),
              languageCode,
              storeId.getStoreNumber());
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    CompletableFuture.allOf(articleMasterData, discountArticleStoreMasterData);

    return CompleteArticleMasterData.builder()
        .articleMasterData(articleMasterData.get())
        .articleStoreMasterData(discountArticleStoreMasterData.get())
        .build();
  }

  private List<RawArticleData> getRawArticleDataListForArticleVariantBundleIdentifierList(
      ArticleId articleId,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo,
      Optional<List<String>> dataTypeList)
      throws Exception {
    List<RawArticleData> rawArticleDataList = new ArrayList<>();

    buildRawArticleDataList(
        articleId, countryCode, salesLine, languageCode, storeNo, rawArticleDataList, dataTypeList);
    return rawArticleDataList;
  }

  private void buildRawArticleDataList(
      ArticleId articleId,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo,
      List<RawArticleData> rawArticleDataList,
      Optional<List<String>> dataTypeList)
      throws ExecutionException, InterruptedException {

    String correctLanguageCode = LANGUAGE_CODE_EXCEPTIONS.getOrDefault(countryCode, countryCode);

    CompletableFuture<ArticleComponent> articleComponentCompletableFuture =
        articleProcessingServiceV2.getArticleComponent(
            countryCode, storeNo, articleId.getArticleNumber());

    CompletableFuture<ArticleMasterData> articleMasterData =
        articleProcessingService.getArticleMasterData(
            articleId, countryCode, salesLine, correctLanguageCode, storeNo);

    CompletableFuture<RawArticleStoreMasterData> rawArticleStoreMasterData =
        articleProcessingService.getRawArticleStoreMasterData(
            articleId, countryCode, salesLine, storeNo);

    CompletableFuture<StockData> stockData =
        articleProcessingService.getArticleDataFromMovie(
            articleId, countryCode, salesLine, storeNo);

    CompletableFuture<List<StockLocation>> articleStockLocationList =
        stockLocationRepository.getArticleStockLocation(
            articleId, countryCode, storeNo, dataTypeList);

    CompletableFuture<List<IduItemDerivedStandard>> iduDerivedStandardsList =
        articleMasterDataRepository.getIduItemDerivedStandardsAsync(countryCode, articleId);

    CompletableFuture<StoreInformationGroup> storeInformationGroup =
        articleMasterDataRepository.getDeliveryFlagAndStoreInformation(
            countryCode, articleId, storeNo);

    CompletableFuture<Optional<IduItemDerivedStandard>> iduItemDerivedStandard =
        articleMasterDataRepository.getMultiLanguageData(countryCode, articleId);

    CompletableFuture<String> dailyMeanSales =
        movieAclService.getAverageSales(articleId, countryCode, salesLine, storeNo);

    CompletableFuture.allOf(
        articleMasterData,
        rawArticleStoreMasterData,
        stockData,
        articleStockLocationList,
        iduDerivedStandardsList,
        iduItemDerivedStandard,
        dailyMeanSales);

    articleCacheService.enhanceArticleMasterData(
        articleMasterData.get(), iduDerivedStandardsList.get());

    generalDataRepository.enhanceRawArticleStoreMasterDataWithDepartmentName(
        countryCode, salesLine, storeNo, rawArticleStoreMasterData.get());

    updateWithDefaultValues(rawArticleStoreMasterData.get());

    StockInformation stockDetails = null;
    List<ArticlePlannedDeliveryDate> plannedDeliveryDates = null;

    if (stockData.get() != null) {
      stockDetails = stockData.get().getArticleStockData();
      plannedDeliveryDates = stockData.get().getArticlePlannedDeliveryDates();
      if (dailyMeanSales.get() != null && stockDetails != null) {
        Float parsedDailyMeanSales = Float.parseFloat(dailyMeanSales.get());
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        String dailyMeanSales30 = df.format(parsedDailyMeanSales);
        stockDetails.setDailyMeanSales30(dailyMeanSales30);
      }
    }

    RawArticleData rawArticleData =
        RawArticleData.builder()
            .articleId(articleId)
            .articleMasterData(articleMasterData.get())
            .articleStoreMasterData(rawArticleStoreMasterData.get())
            .stockDetails(stockDetails)
            .plannedDeliveryDates(plannedDeliveryDates)
            .stockLocations(articleStockLocationList.get())
            .articleComponent(articleComponentCompletableFuture.get())
            .productLifeCycleData(
                articleCacheService.buildProductLifeCycleData(iduDerivedStandardsList.get()))
            .channelDeliveryFlag(storeInformationGroup.get().getChannelDeliveryFlag())
            .returnPossibilityStoreSupplierNumber(
                storeInformationGroup.get().getStoreItemPhaseOutInformation() != null
                    ? storeInformationGroup
                        .get()
                        .getStoreItemPhaseOutInformation()
                        .getReturnPossibilityStoreSupplierNumber()
                    : null)
            .minimumRetailPrice(articleMasterData.get().getMinimumRetailPrice())
            .multiLanguageData(
                mapToMultiLanguageData(articleMasterData.get(), iduItemDerivedStandard.get()))
            .build();

    if (rawArticleDataList != null) {
      rawArticleDataList.add(rawArticleData);
    }
  }

  private void updateWithDefaultValues(RawArticleStoreMasterData rawArticleStoreMasterData) {
    if (rawArticleStoreMasterData.getSalesBlockingCode() == null) {
      rawArticleStoreMasterData.setSalesBlockingCode(1);
    }
  }

  private MultiLanguageData mapToMultiLanguageData(
      ArticleMasterData articleMasterData,
      Optional<IduItemDerivedStandard> iduItemDerivedStandard) {
    if (!iduItemDerivedStandard.isPresent()) {
      return null;
    }

    List<VariantDescription> variantDescriptions =
        iduItemDerivedStandard
            .get()
            .getContentIduItemDerivedStandard()
            .getVariantSortDescriptionV2()
            .stream()
            .map(
                variantSortDescription ->
                    VariantDescription.builder()
                        .description(variantSortDescription.getDescription())
                        .languageCode(variantSortDescription.getLanguage())
                        .validFrom(variantSortDescription.getValidFrom())
                        .build())
            .toList();

    List<LanguageData> articleDescriptions =
        iduItemDerivedStandard
            .get()
            .getContentIduItemDerivedStandard()
            .getMerchandiseArticleDescription()
            .stream()
            .map(
                merchandiseArticleDescription ->
                    LanguageData.builder()
                        .description(merchandiseArticleDescription.getDescription())
                        .languageCode(merchandiseArticleDescription.getLanguage())
                        .build())
            .toList();

    List<LanguageData> presentationMainMerchandiseGroupDescriptions =
        getPmmgData(
            iduItemDerivedStandard.get(),
            "" + articleMasterData.getPresentationMainMerchandiseGroup());

    List<LanguageData> presentationMerchandiseGroupDescriptions =
        getPmmgData(
            iduItemDerivedStandard.get(),
            ""
                + articleMasterData.getPresentationMainMerchandiseGroup()
                + "-"
                + articleMasterData.getPresentationMerchandiseGroup());

    List<LanguageData> presentationSubMerchandiseGroupDescriptions =
        getPmmgData(
            iduItemDerivedStandard.get(),
            ""
                + articleMasterData.getPresentationMainMerchandiseGroup()
                + "-"
                + articleMasterData.getPresentationMerchandiseGroup()
                + "-"
                + articleMasterData.getPresentationSubMerchandiseGroup());

    return MultiLanguageData.builder()
        .variantDescriptions(variantDescriptions)
        .articleDescriptions(articleDescriptions)
        .presentationMainMerchandiseGroupDescriptions(presentationMainMerchandiseGroupDescriptions)
        .presentationMerchandiseGroupDescriptions(presentationMerchandiseGroupDescriptions)
        .presentationSubMerchandiseGroupDescriptions(presentationSubMerchandiseGroupDescriptions)
        .build();
  }

  private List<LanguageData> getPmmgData(
      IduItemDerivedStandard iduItemDerivedStandard, String filter) {
    return iduItemDerivedStandard.getReferences().getReferenceList().stream()
        .filter(
            reference ->
                reference.getRefName().equalsIgnoreCase(MERCHANDISE_GROUP_INFORMATION)
                    && reference.getRefKey().equalsIgnoreCase(filter)
                    && !CollectionUtils.isEmpty(reference.getLabels()))
        .flatMap(reference -> reference.getLabels().stream())
        .map(
            label ->
                LanguageData.builder()
                    .description(label.getDescription())
                    .languageCode(label.getLanguage())
                    .build())
        .toList();
  }

  public List<RawArticleData> getArticleMasterDataListByArticleIds(
      List<ArticleId> articleIds,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo)
      throws Exception {

    Map<ArticleId, List<IduItemDerivedStandard>> iduItemDerivedStandardList =
        articleMasterDataRepository.getIduItemDerivedStandards(articleIds, countryCode).get();

    return iduItemDerivedStandardList.keySet().parallelStream()
        .map(
            articleId -> {
              IduItemDerivedStandard iduItemDerivedStandard =
                  iduItemDerivedStandardList.get(articleId).get(0);
              String lang = languageCode.toLowerCase();
              ArticleMasterData articleMasterData =
                  getArticleMasterDataFromIduDerived(iduItemDerivedStandard, lang);
              return RawArticleData.builder()
                  .articleId(articleId)
                  .articleMasterData(articleMasterData)
                  .build();
            })
        .toList();
  }

  private ArticleMasterData getArticleMasterDataFromIduDerived(
      IduItemDerivedStandard iduItemDerivedStandard, String lang) {
    ContentIduItemDerivedStandard contentIduItemDerivedStandard =
        iduItemDerivedStandard.getContentIduItemDerivedStandard();
    Domain domain = iduItemDerivedStandard.getDomain();
    Optional<MerchandiseArticleDescription> firstLang =
        contentIduItemDerivedStandard.getMerchandiseArticleDescription().stream()
            .filter(
                merchandiseArticleDescription ->
                    lang.equals(merchandiseArticleDescription.getLanguage()))
            .findFirst();
    return ArticleMasterData.builder()
        .subsystemArticleNumber(domain.getStoreSubsystemArticleNumber())
        .articleDescription(firstLang.isPresent() ? firstLang.get().getDescription() : "")
        .weightArticle(contentIduItemDerivedStandard.getWeightArticle())
        .build();
  }
}
