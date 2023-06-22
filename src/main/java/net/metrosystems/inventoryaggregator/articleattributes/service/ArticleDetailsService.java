package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleDataFromMovie;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleIdTreeStructure;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleIdTreeStructureList;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticlePlannedDeliveryDate;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.CompleteArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.LanguageData;
import net.metrosystems.inventoryaggregator.articleattributes.model.MaximumRetailPrice;
import net.metrosystems.inventoryaggregator.articleattributes.model.MultiLanguageData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.StockInformation;
import net.metrosystems.inventoryaggregator.articleattributes.model.VariantDescription;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlelocation.ArticleLocation;
import net.metrosystems.inventoryaggregator.articleattributes.repository.AclBundleStockLocationRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleDataFromMovieRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.GeneralDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise.StoreInformationGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.ContentIduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.Domain;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.MerchandiseArticleDescription;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleVariantBundleIdentifier;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class ArticleDetailsService {

  private static final String MERCHANDISE_GROUP_INFORMATION = "merchandiseGroupInformation";
  private final ArticleCacheService articleCacheService;
  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;
  private final ArticleMasterDataRepository articleMasterDataRepository;
  private final ArticleDataFromMovieRepository articleDataFromMovieRepository;
  private final AclBundleStockLocationRepository aclBundleStockLocationRepository;
  private final GeneralDataRepository generalDataRepository;
  private final MovieService movieService;
  private final RawArticleDataService rawArticleDataService;

  @Autowired
  public ArticleDetailsService(
      ArticleCacheService articleCacheService,
      RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository,
      ArticleMasterDataRepository articleMasterDataRepository,
      ArticleDataFromMovieRepository articleDataFromMovieRepository,
      AclBundleStockLocationRepository aclBundleStockLocationRepository,
      GeneralDataRepository generalDataRepository,
      MovieService movieService,
      RawArticleDataService rawArticleDataService) {
    this.articleCacheService = articleCacheService;
    this.rawArticleStoreMasterDataRepository = rawArticleStoreMasterDataRepository;
    this.articleMasterDataRepository = articleMasterDataRepository;
    this.articleDataFromMovieRepository = articleDataFromMovieRepository;
    this.aclBundleStockLocationRepository = aclBundleStockLocationRepository;
    this.generalDataRepository = generalDataRepository;
    this.movieService = movieService;
    this.rawArticleDataService = rawArticleDataService;
  }

  public List<RawArticleData> getArticleDataListByEan(
      String ean, String countryCode, String salesLine, String languageCode, Integer storeNo)
      throws Exception {
    List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList =
        getArticleVariantBundleIdentifierListForEan(ean, countryCode, languageCode, salesLine);

    return getRawArticleDataListForArticleVariantBundleIdentifierList(
        articleVariantBundleIdentifierList, countryCode, salesLine, languageCode, storeNo);
  }

  public List<RawArticleData> getArticleDataListBySubsys(
      Long subsysArtNo, String countryCode, String salesLine, String languageCode, Integer storeNo)
      throws Exception {
    List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList =
        getArticleVariantBundleIdentifierListForSubsys(
            subsysArtNo, countryCode, languageCode, salesLine);
    return getRawArticleDataListForArticleVariantBundleIdentifierList(
        articleVariantBundleIdentifierList, countryCode, salesLine, languageCode, storeNo);
  }

  public List<RawArticleData> getArticleDataListByArticleVariantBundle(
      Long articleNumber,
      Long variantNumber,
      Long bundleNumber,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo,
      boolean dataSource)
      throws Exception {
    return getRawArticleDataListForArticleVariantBundleIdentifierList(
        ArticleId.builder()
            .articleNumber(articleNumber)
            .variantNumber(variantNumber)
            .bundleNumber(bundleNumber)
            .build(),
        countryCode,
        salesLine,
        languageCode,
        storeNo,
        dataSource);
  }

  public List<ArticleMasterData> getArticleMasterDataListByArticleVariantBundle(
      Long articleNumber,
      Long variantNumber,
      Long bundleNumber,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo)
      throws Exception {
    List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList =
        getArticleVariantBundleIdentifierListForArticleVariantBundle(
            articleNumber, variantNumber, bundleNumber, countryCode, salesLine, languageCode);
    return getRawArticleMasterDataListForArticleVariantBundleIdentifierList(
        articleVariantBundleIdentifierList, countryCode, salesLine, languageCode, storeNo);
  }

  public CompleteArticleMasterData getCompleteArticleMasterDataByArticleVariantBundle(
      Long articleNumber,
      Long variantNumber,
      Long bundleNumber,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo)
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
              articleId, countryCode, salesLine, languageCode, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      e.printStackTrace();
    }

    CompletableFuture<RawArticleStoreMasterData> discountArticleStoreMasterData =
        new CompletableFuture<>();
    try {
      discountArticleStoreMasterData =
          rawArticleStoreMasterDataRepository.getDiscountArticleStoreMasterData(
              articleId, countryCode, salesLine, languageCode, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      e.printStackTrace();
    }

    CompletableFuture.allOf(articleMasterData, discountArticleStoreMasterData);

    return CompleteArticleMasterData.builder()
        .articleMasterData(articleMasterData.get())
        .articleStoreMasterData(discountArticleStoreMasterData.get())
        .build();
  }

  private List<ArticleVariantBundleIdentifier> getArticleVariantBundleIdentifierListForEan(
      String ean, String countryCode, String languageCode, String salesLine) throws Exception {
    return articleCacheService.getArticleVariantBundleListForEan(
        ean, countryCode, salesLine, languageCode);
  }

  private List<ArticleVariantBundleIdentifier> getArticleVariantBundleIdentifierListForSubsys(
      Long subsysArtNo, String countryCode, String languageCode, String salesLine)
      throws Exception {
    return articleCacheService.getArticleVariantBundleListForSubsys(
        subsysArtNo, countryCode, salesLine, languageCode);
  }

  private List<ArticleVariantBundleIdentifier>
      getArticleVariantBundleIdentifierListForArticleVariantBundle(
          Long articleNumber,
          Long variantNumber,
          Long bundleNumber,
          String countryCode,
          String salesLine,
          String languageCode)
          throws Exception {
    return articleCacheService.getArticleVariantBundleListForArticleVariantBundle(
        articleNumber, variantNumber, bundleNumber, countryCode, salesLine, languageCode);
  }

  private List<ArticleVariantBundleIdentifier>
      getArticleVariantBundleIdentifierListForArticleNumber(
          Long articleNumber, String countryCode, String salesLine)
          throws Exception {
    return articleCacheService.getArticleVariantBundleListForArticleNumber(
        articleNumber, countryCode, salesLine);
  }

  private ArticleIdTreeStructureList transformArticleVariantBundleListToArticleIdTreeStructureList(
      List<ArticleVariantBundleIdentifier> articleVariantBundleList) {
    ArticleIdTreeStructureList articleIdTreeStructureList = new ArticleIdTreeStructureList();
    for (ArticleVariantBundleIdentifier articleVariantBundleIdentifier : articleVariantBundleList) {
      if (articleIdTreeStructureList == null) {
        articleIdTreeStructureList =
            new ArticleIdTreeStructureList(
                new ArrayList<>(
                    Arrays.asList(
                        new ArticleIdTreeStructure(
                            articleVariantBundleIdentifier.getArticleNumber(),
                            transformArticleVariantBundleListToArticleComponentByArticle(
                                articleVariantBundleList,
                                articleVariantBundleIdentifier.getArticleNumber())))));
      } else {
        Integer articleIndex =
            articleIdTreeStructureList.findArticleIndex(
                articleVariantBundleIdentifier.getArticleNumber());
        if (articleIndex == null) {
          articleIdTreeStructureList.setArticleIdTreeStructureList(
              new ArrayList<>(
                  Arrays.asList(
                      new ArticleIdTreeStructure(
                          articleVariantBundleIdentifier.getArticleNumber(),
                          transformArticleVariantBundleListToArticleComponentByArticle(
                              articleVariantBundleList,
                              articleVariantBundleIdentifier.getArticleNumber())))));
        } else if (articleIndex == -1) {
          articleIdTreeStructureList
              .getArticleIdTreeStructureList()
              .add(
                  new ArticleIdTreeStructure(
                      articleVariantBundleIdentifier.getArticleNumber(),
                      transformArticleVariantBundleListToArticleComponentByArticle(
                          articleVariantBundleList,
                          articleVariantBundleIdentifier.getArticleNumber())));
        }
      }
    }
    return articleIdTreeStructureList;
  }

  private ArticleComponent transformArticleVariantBundleListToArticleComponentByArticle(
      List<ArticleVariantBundleIdentifier> articleVariantBundleList, Long articleNumber) {
    ArticleComponent articleComponent = new ArticleComponent();
    for (ArticleVariantBundleIdentifier articleVariantBundleIdentifier : articleVariantBundleList) {
      if (articleVariantBundleIdentifier.getArticleNumber().equals(articleNumber)) {
        articleComponent.addVariantBundle(
            articleVariantBundleIdentifier.getVariantNumber(),
            articleVariantBundleIdentifier.getBundleNumber());
      }
    }
    return articleComponent;
  }

  private List<RawArticleData> getRawArticleDataListForArticleVariantBundleIdentifierList(
      List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo)
      throws Exception {
    List<RawArticleData> rawArticleDataList = new ArrayList<>();
    ArticleIdTreeStructureList articleIdTreeStructureList =
        transformArticleVariantBundleListToArticleIdTreeStructureList(
            articleVariantBundleIdentifierList);

    for (ArticleIdTreeStructure articleIdTreeStructure :
        articleIdTreeStructureList.getArticleIdTreeStructureList()) {
      ArticleComponent articleComponent =
          transformArticleVariantBundleListToArticleComponentByArticle(
              getArticleVariantBundleIdentifierListForArticleNumber(
                  articleIdTreeStructure.getArticleNumber(), countryCode, salesLine),
              articleIdTreeStructure.getArticleNumber());

      for (ArticleComponent.ArticleVariant articleVariant :
          articleIdTreeStructure.getArticleComponent().getVariantList()) {
        for (ArticleComponent.ArticleBundle articleBundle : articleVariant.getBundleList()) {
          ArticleId articleId =
              new ArticleId(
                  articleIdTreeStructure.getArticleNumber(),
                  articleVariant.getVariantNumber(),
                  articleBundle.getBundleNumber());
          buildRawArticleDataList(
              articleId,
              countryCode,
              salesLine,
              languageCode,
              storeNo,
              rawArticleDataList,
              articleComponent);
        }
      }
    }
    return rawArticleDataList;
  }

  private List<RawArticleData> getRawArticleDataListForArticleVariantBundleIdentifierList(
      ArticleId articleId,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo,
      boolean dataSource)
      throws Exception {
    List<RawArticleData> rawArticleDataList = new ArrayList<>();

    ArticleComponent articleComponent =
        transformArticleVariantBundleListToArticleComponentByArticle(
            getArticleVariantBundleIdentifierListForArticleNumber(
                articleId.getArticleNumber(), countryCode, salesLine),
            articleId.getArticleNumber());

    if (dataSource) {
      rawArticleDataService.buildRawArticleDataList(
          articleId,
          countryCode,
          salesLine,
          languageCode,
          storeNo,
          rawArticleDataList,
          articleComponent);
    } else {

      buildRawArticleDataList(
          articleId,
          countryCode,
          salesLine,
          languageCode,
          storeNo,
          rawArticleDataList,
          articleComponent);
    }
    return rawArticleDataList;
  }

  private void buildRawArticleDataList(
      ArticleId articleId,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo,
      List<RawArticleData> rawArticleDataList,
      ArticleComponent articleComponent)
      throws ExecutionException, InterruptedException {

    CompletableFuture<ArticleMasterData> articleMasterData = new CompletableFuture<>();
    try {
      articleMasterData =
          articleMasterDataRepository.getArticleMasterData(
              articleId, countryCode, salesLine, languageCode, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleDetailsService.buildRawArticleDataList - articleMasterData - error: {}",
          e.getMessage(),
          e);
    }

    CompletableFuture<RawArticleStoreMasterData> rawArticleStoreMasterData =
        new CompletableFuture<>();
    try {
      rawArticleStoreMasterData =
          rawArticleStoreMasterDataRepository.getRawArticleStoreMasterData(
              articleId, countryCode, salesLine, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleDetailsService.buildRawArticleDataList - rawArticleStoreMasterData - error: {}",
          e.getMessage(),
          e);
    }

    CompletableFuture<ArticleDataFromMovie> articleDataFromMovie = new CompletableFuture<>();
    try {
      articleDataFromMovie =
          articleDataFromMovieRepository.getArticleDataFromMovie(
              articleId, countryCode, salesLine, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleDetailsService.buildRawArticleDataList - articleDataFromMovie - error: {}",
          e.getMessage(),
          e);
    }

    CompletableFuture<List<ArticleStockLocation>> articleStockLocationList =
        new CompletableFuture<>();
    try {
      articleStockLocationList =
          aclBundleStockLocationRepository.getArticleStockLocation(
              articleId, countryCode, salesLine, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleDetailsService.buildRawArticleDataList - articleStockLocationList - error: {}",
          e.getMessage(),
          e);
    }

    CompletableFuture<ArticleLocation> articleLocationList = new CompletableFuture<>();
    try {
      articleLocationList =
          aclBundleStockLocationRepository.getArticleLocation(
              countryCode, salesLine, storeNo, articleId);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleDetailsService.buildRawArticleDataList - articleLocationList - error: {}",
          e.getMessage(),
          e);
    }

    CompletableFuture<List<IduItemDerivedStandard>> iduDerivedStandardsList =
        articleMasterDataRepository.getIduItemDerivedStandardsAsync(countryCode, articleId);

    CompletableFuture<MaximumRetailPrice> maximumRetailPrice =
        rawArticleStoreMasterDataRepository.getMaximumRetailPrice(
            countryCode, salesLine, storeNo, articleId);

    CompletableFuture<StoreInformationGroup> storeInformationGroup =
        articleMasterDataRepository.getDeliveryFlagAndStoreInformation(
            countryCode, articleId, storeNo);

    CompletableFuture<Optional<IduItemDerivedStandard>> iduItemDerivedStandard =
        articleMasterDataRepository.getMultiLanguageData(countryCode, articleId);

    CompletableFuture<String> dailyMeanSales =
        movieService.getTotalAverageSales(countryCode, salesLine, storeNo, articleId);

    CompletableFuture.allOf(
        articleMasterData,
        rawArticleStoreMasterData,
        articleDataFromMovie,
        articleStockLocationList,
        articleLocationList,
        iduDerivedStandardsList,
        maximumRetailPrice,
        iduItemDerivedStandard,
        dailyMeanSales);

    articleCacheService.enhanceArticleMasterData(
        articleMasterData.get(), iduDerivedStandardsList.get());

    generalDataRepository.enhanceRawArticleStoreMasterDataWithDepartmentName(
        countryCode, salesLine, storeNo, rawArticleStoreMasterData.get());

    StockInformation stockDetails = null;
    List<ArticlePlannedDeliveryDate> plannedDeliveryDates = null;

    if (articleDataFromMovie.get() != null) {
      stockDetails = articleDataFromMovie.get().getArticleStockData();
      plannedDeliveryDates = articleDataFromMovie.get().getArticlePlannedDeliveryDates();
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
            .articleLocation(articleLocationList.get())
            .articleComponent(articleComponent)
            .productLifeCycleData(
                articleCacheService.buildProductLifeCycleData(iduDerivedStandardsList.get()))
            .maximumRetailPrice(maximumRetailPrice.get())
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

  private List<ArticleMasterData> getRawArticleMasterDataListForArticleVariantBundleIdentifierList(
      List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo)
      throws Exception {
    List<ArticleMasterData> rawArticleMasterData = new ArrayList<>();
    ArticleIdTreeStructureList articleIdTreeStructureList =
        transformArticleVariantBundleListToArticleIdTreeStructureList(
            articleVariantBundleIdentifierList);
    for (ArticleIdTreeStructure articleIdTreeStructure :
        articleIdTreeStructureList.getArticleIdTreeStructureList()) {
      for (ArticleComponent.ArticleVariant articleVariant :
          articleIdTreeStructure.getArticleComponent().getVariantList()) {
        for (ArticleComponent.ArticleBundle articleBundle : articleVariant.getBundleList()) {
          ArticleId articleId =
              ArticleId.builder()
                  .articleNumber(articleIdTreeStructure.getArticleNumber())
                  .variantNumber(articleVariant.getVariantNumber())
                  .bundleNumber(articleBundle.getBundleNumber())
                  .build();
          ArticleMasterData articleMasterData =
              articleMasterDataRepository
                  .getArticleMasterData(articleId, countryCode, salesLine, languageCode, storeNo)
                  .get();
          if (rawArticleMasterData == null) {
            rawArticleMasterData = new ArrayList<>(Collections.singletonList(articleMasterData));
          } else {
            rawArticleMasterData.add(articleMasterData);
          }
        }
      }
    }
    return rawArticleMasterData;
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
