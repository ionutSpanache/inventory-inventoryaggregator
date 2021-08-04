package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.*;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlelocation.ArticleLocation;
import net.metrosystems.inventoryaggregator.articleattributes.repository.AclBundleStockLocationRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleDataFromMovieRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleVariantBundleIdentifier;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ArticleDetailsService {

  private final ArticleCacheService articleCacheService;
  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;
  private final ArticleMasterDataRepository articleMasterDataRepository;
  private final ArticleDataFromMovieRepository articleDataFromMovieRepository;
  private final AclBundleStockLocationRepository aclBundleStockLocationRepository;

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

    CompletableFuture.allOf(articleMasterData, rawArticleStoreMasterData, articleDataFromMovie,
        articleStockLocationList, articleLocationList, productLifeCycleData);

    StockInformation stockDetails = null;
    List<ArticlePlannedDeliveryDate> plannedDeliveryDates = null;
    if (articleDataFromMovie.get() != null) {
      stockDetails = articleDataFromMovie.get().getArticleStockData();
      plannedDeliveryDates = articleDataFromMovie.get().getArticlePlannedDeliveryDates();
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
                                                  .build();


    if (rawArticleDataList != null) {
      rawArticleDataList.add(rawArticleData);
    }
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
