package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.*;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlelocation.ArticleLocation;
import net.metrosystems.inventoryaggregator.articleattributes.repository.AclBundleStockLocationRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleDataFromMovieRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleVariantBundleIdentifier;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class ArticleDetailsService {

  private final ArticleCacheService articleCacheService;
  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;
  private final ArticleMasterDataRepository articleMasterDataRepository;
  private final ArticleDataFromMovieRepository articleDataFromMovieRepository;
  private final AclBundleStockLocationRepository aclBundleStockLocationRepository;

  private static final String MERCHANDISE_GROUP_INFORMATION = "merchandiseGroupInformation";

  @Autowired
  public ArticleDetailsService(ArticleCacheService articleCacheService,
      RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository,
      ArticleMasterDataRepository articleMasterDataRepository,
      ArticleDataFromMovieRepository articleDataFromMovieRepository,
      AclBundleStockLocationRepository aclBundleStockLocationRepository) {
    this.articleCacheService = articleCacheService;
    this.rawArticleStoreMasterDataRepository = rawArticleStoreMasterDataRepository;
    this.articleMasterDataRepository = articleMasterDataRepository;
    this.articleDataFromMovieRepository = articleDataFromMovieRepository;
    this.aclBundleStockLocationRepository = aclBundleStockLocationRepository;
  }

  public List<RawArticleData> getArticleDataListByEan(String ean, String countryCode,
      String salesLine, String languageCode, Integer storeNo) throws Exception {
    List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList = getArticleVariantBundleIdentifierListForEan(
        ean, countryCode, languageCode, salesLine);

    return getRawArticleDataListForArticleVariantBundleIdentifierList(
        articleVariantBundleIdentifierList, countryCode, salesLine, languageCode, storeNo);
  }

  public List<RawArticleData> getArticleDataListBySubsys(Long subsysArtNo, String countryCode,
      String salesLine, String languageCode, Integer storeNo) throws Exception {
    List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList = getArticleVariantBundleIdentifierListForSubsys(
        subsysArtNo, countryCode, languageCode, salesLine);
    return getRawArticleDataListForArticleVariantBundleIdentifierList(
        articleVariantBundleIdentifierList, countryCode, salesLine, languageCode, storeNo);
  }


  public List<RawArticleData> getArticleDataListByArticleVariantBundle(Long articleNumber,
      Long variantNumber, Long bundleNumber, String countryCode, String salesLine,
      String languageCode, Integer storeNo) throws Exception {
    return getRawArticleDataListForArticleVariantBundleIdentifierList(
        ArticleId.builder().articleNumber(articleNumber).variantNumber(variantNumber)
                 .bundleNumber(bundleNumber).build(), countryCode, salesLine, languageCode,
        storeNo);
  }

  public List<ArticleMasterData> getArticleMasterDataListByArticleVariantBundle(Long articleNumber,
      Long variantNumber, Long bundleNumber, String countryCode, String salesLine,
      String languageCode, Integer storeNo) throws Exception {
    List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList = getArticleVariantBundleIdentifierListForArticleVariantBundle(
        articleNumber, variantNumber, bundleNumber, countryCode, salesLine, languageCode);
    return getRawArticleMasterDataListForArticleVariantBundleIdentifierList(
        articleVariantBundleIdentifierList, countryCode, salesLine, languageCode, storeNo);
  }


  public CompleteArticleMasterData getCompleteArticleMasterDataByArticleVariantBundle(
      Long articleNumber, Long variantNumber, Long bundleNumber, String countryCode,
      String salesLine, String languageCode, Integer storeNo) throws Exception {

    ArticleId articleId = ArticleId.builder().articleNumber(articleNumber)
                                   .variantNumber(variantNumber).bundleNumber(bundleNumber).build();

    CompletableFuture<ArticleMasterData> articleMasterData = new CompletableFuture<>();

    try {
      articleMasterData = articleMasterDataRepository
          .getArticleMasterData(articleId, countryCode, salesLine, languageCode, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      e.printStackTrace();
    }

    CompletableFuture<RawArticleStoreMasterData> discountArticleStoreMasterData = new CompletableFuture<>();
    try {
      discountArticleStoreMasterData = rawArticleStoreMasterDataRepository
          .getDiscountArticleStoreMasterData(articleId, countryCode, salesLine, languageCode,
              storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      e.printStackTrace();
    }

    CompletableFuture.allOf(articleMasterData, discountArticleStoreMasterData);

    return CompleteArticleMasterData.builder().articleMasterData(articleMasterData.get())
                                    .articleStoreMasterData(discountArticleStoreMasterData.get())
                                    .build();
  }


  private List<ArticleVariantBundleIdentifier> getArticleVariantBundleIdentifierListForEan(
      String ean, String countryCode, String languageCode, String salesLine) throws Exception {
    return articleCacheService
        .getArticleVariantBundleListForEan(ean, countryCode, salesLine, languageCode);
  }


  private List<ArticleVariantBundleIdentifier> getArticleVariantBundleIdentifierListForSubsys(
      Long subsysArtNo, String countryCode, String languageCode, String salesLine)
      throws Exception {
    return articleCacheService
        .getArticleVariantBundleListForSubsys(subsysArtNo, countryCode, salesLine, languageCode);
  }


  private List<ArticleVariantBundleIdentifier> getArticleVariantBundleIdentifierListForArticleVariantBundle(
      Long articleNumber, Long variantNumber, Long bundleNumber, String countryCode,
      String salesLine, String languageCode) throws Exception {
    return articleCacheService
        .getArticleVariantBundleListForArticleVariantBundle(articleNumber, variantNumber,
            bundleNumber, countryCode, salesLine, languageCode);
  }


  private List<ArticleVariantBundleIdentifier> getArticleVariantBundleIdentifierListForArticleNumber(
      Long articleNumber, String countryCode, String salesLine, String languageCode)
      throws Exception {
    return articleCacheService
        .getArticleVariantBundleListForArticleNumber(articleNumber, countryCode, salesLine,
            languageCode);
  }


  private ArticleIdTreeStructureList transformArticleVariantBundleListToArticleIdTreeStructureList(
      List<ArticleVariantBundleIdentifier> articleVariantBundleList) {
    ArticleIdTreeStructureList articleIdTreeStructureList = new ArticleIdTreeStructureList();
    for (ArticleVariantBundleIdentifier articleVariantBundleIdentifier : articleVariantBundleList) {
      if (articleIdTreeStructureList == null) {
        articleIdTreeStructureList = new ArticleIdTreeStructureList(new ArrayList<>(Arrays.asList(
            new ArticleIdTreeStructure(articleVariantBundleIdentifier.getArticleNumber(),
                transformArticleVariantBundleListToArticleComponentByArticle(
                    articleVariantBundleList,
                    articleVariantBundleIdentifier.getArticleNumber())))));
      } else {
        Integer articleIndex = articleIdTreeStructureList
            .findArticleIndex(articleVariantBundleIdentifier.getArticleNumber());
        if (articleIndex == null) {
          articleIdTreeStructureList.setArticleIdTreeStructureList(new ArrayList<>(Arrays.asList(
              new ArticleIdTreeStructure(articleVariantBundleIdentifier.getArticleNumber(),
                  transformArticleVariantBundleListToArticleComponentByArticle(
                      articleVariantBundleList,
                      articleVariantBundleIdentifier.getArticleNumber())))));
        } else if (articleIndex == -1) {
          articleIdTreeStructureList.getArticleIdTreeStructureList().add(
              new ArticleIdTreeStructure(articleVariantBundleIdentifier.getArticleNumber(),
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
        articleComponent.addVariantBundle(articleVariantBundleIdentifier.getVariantNumber(),
            articleVariantBundleIdentifier.getBundleNumber());
      }
    }
    return articleComponent;
  }

  private List<RawArticleData> getRawArticleDataListForArticleVariantBundleIdentifierList(
      List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList, String countryCode,
      String salesLine, String languageCode, Integer storeNo) throws Exception {
    List<RawArticleData> rawArticleDataList = new ArrayList<>();
    ArticleIdTreeStructureList articleIdTreeStructureList = transformArticleVariantBundleListToArticleIdTreeStructureList(
        articleVariantBundleIdentifierList);

    for (ArticleIdTreeStructure articleIdTreeStructure : articleIdTreeStructureList
        .getArticleIdTreeStructureList()) {
      ArticleComponent articleComponent = transformArticleVariantBundleListToArticleComponentByArticle(
          getArticleVariantBundleIdentifierListForArticleNumber(
              articleIdTreeStructure.getArticleNumber(), countryCode, salesLine, languageCode),
          articleIdTreeStructure.getArticleNumber());

      for (ArticleComponent.ArticleVariant articleVariant : articleIdTreeStructure
          .getArticleComponent().getVariantList()) {
        for (ArticleComponent.ArticleBundle articleBundle : articleVariant.getBundleList()) {
          ArticleId articleId = new ArticleId(articleIdTreeStructure.getArticleNumber(),
              articleVariant.getVariantNumber(), articleBundle.getBundleNumber());
          buildRawArticleDataList(articleId, countryCode, salesLine, languageCode, storeNo,
              rawArticleDataList, articleComponent);
        }
      }
    }
    return rawArticleDataList;
  }

  private List<RawArticleData> getRawArticleDataListForArticleVariantBundleIdentifierList(
      ArticleId articleId, String countryCode, String salesLine, String languageCode,
      Integer storeNo) throws Exception {
    List<RawArticleData> rawArticleDataList = new ArrayList<>();


    ArticleComponent articleComponent = transformArticleVariantBundleListToArticleComponentByArticle(
        getArticleVariantBundleIdentifierListForArticleNumber(articleId.getArticleNumber(),
            countryCode, salesLine, languageCode), articleId.getArticleNumber());

    buildRawArticleDataList(articleId, countryCode, salesLine, languageCode, storeNo,
        rawArticleDataList, articleComponent);
    return rawArticleDataList;
  }

  private void buildRawArticleDataList(ArticleId articleId, String countryCode, String salesLine,
      String languageCode, Integer storeNo, List<RawArticleData> rawArticleDataList,
      ArticleComponent articleComponent)
      throws ExecutionException, InterruptedException {

    CompletableFuture<ArticleMasterData> articleMasterData = new CompletableFuture<>();
    try {
      articleMasterData = articleMasterDataRepository
          .getArticleMasterData(articleId, countryCode, salesLine, languageCode, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error("ArticleDetailsService.buildRawArticleDataList - articleMasterData - error: {}",
          e.getMessage(), e);
    }

    CompletableFuture<RawArticleStoreMasterData> rawArticleStoreMasterData = new CompletableFuture<>();
    try {
      rawArticleStoreMasterData = rawArticleStoreMasterDataRepository
          .getRawArticleStoreMasterData(articleId, countryCode, salesLine, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleDetailsService.buildRawArticleDataList - rawArticleStoreMasterData - error: {}",
          e.getMessage(), e);
    }

    CompletableFuture<ArticleDataFromMovie> articleDataFromMovie = new CompletableFuture<>();
    try {
      articleDataFromMovie = articleDataFromMovieRepository
          .getArticleDataFromMovie(articleId, countryCode, salesLine, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error("ArticleDetailsService.buildRawArticleDataList - articleDataFromMovie - error: {}",
          e.getMessage(), e);
    }

    CompletableFuture<List<ArticleStockLocation>> articleStockLocationList = new CompletableFuture<>();
    try {
      articleStockLocationList = aclBundleStockLocationRepository
          .getArticleStockLocation(articleId, countryCode, salesLine, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleDetailsService.buildRawArticleDataList - articleStockLocationList - error: {}",
          e.getMessage(), e);
    }

    CompletableFuture<ArticleLocation> articleLocationList = new CompletableFuture<>();
    try {
      articleLocationList = aclBundleStockLocationRepository
          .getArticleLocation(countryCode, salesLine, storeNo, articleId);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error("ArticleDetailsService.buildRawArticleDataList - articleLocationList - error: {}",
          e.getMessage(), e);
    }

    CompletableFuture<ProductLifeCycleData> productLifeCycleData =
            articleMasterDataRepository.getProductLifeCycleData(countryCode, articleId);

    CompletableFuture<MaximumRetailPrice> maximumRetailPrice =
            rawArticleStoreMasterDataRepository.getMaximumRetailPrice(countryCode, salesLine, storeNo, articleId);

    CompletableFuture<String> channelDeliveryFlag = articleMasterDataRepository.getChannelDeliveryFlag(
            countryCode, articleId, storeNo);

    CompletableFuture<Optional<IduItemDerivedStandard>> iduItemDerivedStandard = articleMasterDataRepository.getMultiLanguageData(
            countryCode, articleId);

    CompletableFuture<PromotionArticleData> promotionArticleData = articleMasterDataRepository.getPromotionArticleData(countryCode,
            storeNo, articleId);

    CompletableFuture.allOf(articleMasterData, rawArticleStoreMasterData, articleDataFromMovie,
        articleStockLocationList, articleLocationList, productLifeCycleData, maximumRetailPrice, iduItemDerivedStandard,
            promotionArticleData);

    StockInformation stockDetails = null;
    List<ArticlePlannedDeliveryDate> plannedDeliveryDates = null;
    if (articleDataFromMovie.get() != null) {
      stockDetails = articleDataFromMovie.get().getArticleStockData();
      plannedDeliveryDates = articleDataFromMovie.get().getArticlePlannedDeliveryDates();
    }

    if (promotionArticleData.get().getPromotionNetSalesPricePerBundle() != null) {
      rawArticleStoreMasterData.get().setStorePromotionBundleNetPrice(promotionArticleData
              .get().getPromotionNetSalesPricePerBundle());
    }

    RawArticleData rawArticleData = RawArticleData.builder().articleId(articleId)
                                                  .articleMasterData(articleMasterData.get())
                                                  .articleStoreMasterData(
                                                      rawArticleStoreMasterData.get())
                                                  .stockDetails(stockDetails)
                                                  .plannedDeliveryDates(plannedDeliveryDates)
                                                  .stockLocations(articleStockLocationList.get())
                                                  .articleLocation(articleLocationList.get())
                                                  .articleComponent(articleComponent)
                                                  .productLifeCycleData(productLifeCycleData.get())
                                                  .maximumRetailPrice(maximumRetailPrice.get())
                                                  .channelDeliveryFlag(channelDeliveryFlag.get())
                                                  .minimumRetailPrice(articleMasterData.get().getMinimumRetailPrice())
                                                  .multiLanguageData(mapToMultiLanguageData(articleMasterData.get(), iduItemDerivedStandard.get()))
                                                  .build();


    if (rawArticleDataList != null) {
      rawArticleDataList.add(rawArticleData);
    }
  }

  private MultiLanguageData mapToMultiLanguageData(ArticleMasterData articleMasterData, Optional<IduItemDerivedStandard> iduItemDerivedStandard) {
    if(!iduItemDerivedStandard.isPresent()) {
      return null;
    }

    List<VariantDescription> variantDescriptions = iduItemDerivedStandard.get()
            .getContentIduItemDerivedStandard().getVariantSortDescriptionV2()
            .stream().map(variantSortDescription -> VariantDescription.builder().description(
                    variantSortDescription.getDescription())
                    .languageCode(variantSortDescription.getLanguage())
                    .validFrom(variantSortDescription.getValidFrom())
                    .build())
            .collect(Collectors.toList());

    List<LanguageData> articleDescriptions = iduItemDerivedStandard.get()
            .getContentIduItemDerivedStandard().getMerchandiseArticleDescription()
            .stream().map(merchandiseArticleDescription -> LanguageData.builder()
                    .description(merchandiseArticleDescription.getDescription())
                    .languageCode(merchandiseArticleDescription.getLanguage())
                    .build())
            .collect(Collectors.toList());

    List<LanguageData> presentationMainMerchandiseGroupDescriptions = getPmmgData(iduItemDerivedStandard.get(),"" + articleMasterData.getPresentationMainMerchandiseGroup());


    List<LanguageData> presentationMerchandiseGroupDescriptions = getPmmgData(iduItemDerivedStandard.get(),
            "" + articleMasterData.getPresentationMainMerchandiseGroup() +
                 "-" + articleMasterData.getPresentationMerchandiseGroup());


    List<LanguageData> presentationSubMerchandiseGroupDescriptions = getPmmgData(iduItemDerivedStandard.get(),
            "" + articleMasterData.getPresentationMainMerchandiseGroup() +
                 "-" + articleMasterData.getPresentationMerchandiseGroup()    +
                 "-" + articleMasterData.getPresentationSubMerchandiseGroup());

    MultiLanguageData multiLanguageData = MultiLanguageData.builder()
              .variantDescriptions(variantDescriptions)
              .articleDescriptions(articleDescriptions)
              .presentationMainMerchandiseGroupDescriptions(presentationMainMerchandiseGroupDescriptions)
              .presentationMerchandiseGroupDescriptions(presentationMerchandiseGroupDescriptions)
              .presentationSubMerchandiseGroupDescriptions(presentationSubMerchandiseGroupDescriptions)
            .build();

    return multiLanguageData;
  }

  private List<LanguageData> getPmmgData(IduItemDerivedStandard iduItemDerivedStandard, String filter) {
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
        .collect(Collectors.toList());
  }

  private List<ArticleMasterData> getRawArticleMasterDataListForArticleVariantBundleIdentifierList(
      List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList, String countryCode,
      String salesLine, String languageCode, Integer storeNo) throws Exception {
    List<ArticleMasterData> rawArticleMasterData = new ArrayList<>();
    ArticleIdTreeStructureList articleIdTreeStructureList = transformArticleVariantBundleListToArticleIdTreeStructureList(
        articleVariantBundleIdentifierList);
    for (ArticleIdTreeStructure articleIdTreeStructure : articleIdTreeStructureList
        .getArticleIdTreeStructureList()) {
      for (ArticleComponent.ArticleVariant articleVariant : articleIdTreeStructure
          .getArticleComponent().getVariantList()) {
        for (ArticleComponent.ArticleBundle articleBundle : articleVariant.getBundleList()) {
          ArticleId articleId = ArticleId.builder()
                                         .articleNumber(articleIdTreeStructure.getArticleNumber())
                                         .variantNumber(articleVariant.getVariantNumber())
                                         .bundleNumber(articleBundle.getBundleNumber()).build();
          ArticleMasterData articleMasterData = articleMasterDataRepository
              .getArticleMasterData(articleId, countryCode, salesLine, languageCode, storeNo).get();
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

}
