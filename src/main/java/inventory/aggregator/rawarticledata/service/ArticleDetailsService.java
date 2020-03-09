package inventory.aggregator.rawarticledata.service;

import inventory.aggregator.common.article_information.articlecache.model.ArticleVariantBundleIdentifier;
import inventory.aggregator.common.article_information.articlecache.service.ArticleCacheService;
import inventory.aggregator.common.model.ArticleNotFoundException;
import inventory.aggregator.rawarticledata.model.*;
import inventory.aggregator.rawarticledata.repository.AclBundleStockLocationRepository;
import inventory.aggregator.rawarticledata.repository.ArticleDataFromMovieRepository;
import inventory.aggregator.rawarticledata.repository.ArticleMasterDataRepository;
import inventory.aggregator.rawarticledata.repository.RawArticleStoreMasterDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ArticleDetailsService {

    private final Executor executor = Executors.newFixedThreadPool(6);

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


    public List<RawArticleData> getArticleDataListByEan(
            String ean,
            String countryCode,
            String salesLine,
            String languageCode,
            Integer storeNo) throws Exception {
        List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList =
                getArticleVariantBundleIdentifierListForEan(ean, countryCode, languageCode, salesLine);

        return getRawArticleDataListForArticleVariantBundleIdentifierList(
                articleVariantBundleIdentifierList,
                countryCode, salesLine, languageCode, storeNo);
    }

    public List<RawArticleData> getArticleDataListBySubsys(
            Long subsysArtNo,
            String countryCode,
            String salesLine,
            String languageCode,
            Integer storeNo) throws Exception {
        List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList =
                getArticleVariantBundleIdentifierListForSubsys(subsysArtNo, countryCode, languageCode, salesLine);
        return getRawArticleDataListForArticleVariantBundleIdentifierList(
                articleVariantBundleIdentifierList,
                countryCode, salesLine, languageCode, storeNo);
    }


    public List<RawArticleData> getArticleDataListByArticleVariantBundle(
            Long articleNumber,
            Long variantNumber,
            Long bundleNumber,
            String countryCode,
            String salesLine,
            String languageCode,
            Integer storeNo) throws Exception {
        List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList =
                getArticleVariantBundleIdentifierListForArticleVariantBundle(articleNumber, variantNumber, bundleNumber, countryCode, salesLine, languageCode);
        return getRawArticleDataListForArticleVariantBundleIdentifierList(
                articleVariantBundleIdentifierList,
                countryCode, salesLine, languageCode, storeNo);
    }

    public List<ArticleMasterData> getArticleMasterDataListByArticleVariantBundle(
            Long articleNumber,
            Long variantNumber,
            Long bundleNumber,
            String countryCode,
            String salesLine,
            String languageCode,
            Integer storeNo) throws Exception {
        List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList =
                getArticleVariantBundleIdentifierListForArticleVariantBundle(articleNumber, variantNumber, bundleNumber, countryCode, salesLine, languageCode);
        return getRawArticleMasterDataListForArticleVariantBundleIdentifierList(
                articleVariantBundleIdentifierList,
                countryCode, salesLine, languageCode, storeNo);
    }


    public CompleteArticleMasterData getCompleteArticleMasterDataByArticleVariantBundle(
            Long articleNumber,
            Long variantNumber,
            Long bundleNumber,
            String countryCode,
            String salesLine,
            String languageCode,
            Integer storeNo) throws Exception {

        ArticleId articleId = ArticleId.builder()
                .articleNumber(articleNumber)
                .variantNumber(variantNumber)
                .bundleNumber(bundleNumber)
                .build();

        CompletableFuture<ArticleMasterData> articleMasterData = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return articleMasterDataRepository.getArticleMasterData(
                                articleId,countryCode, salesLine, languageCode, storeNo).get(20, TimeUnit.SECONDS);
                    } catch (ArticleNotFoundException ex) {
                        throw new ArticleNotFoundException();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }, executor
        );

        CompletableFuture<RawArticleStoreMasterData> discountArticleStoreMasterData = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return rawArticleStoreMasterDataRepository.getDiscountArticleStoreMasterData(
                                articleId, countryCode, salesLine, languageCode, storeNo).get(20, TimeUnit.SECONDS);
                    } catch (ArticleNotFoundException ex) {
                        throw new ArticleNotFoundException();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }, executor
        );

        CompletableFuture.allOf(articleMasterData, discountArticleStoreMasterData);

        return CompleteArticleMasterData.builder()
                .articleMasterData(articleMasterData.get())
                .articleStoreMasterData(discountArticleStoreMasterData.get())
                .build();
    }


    private List<ArticleVariantBundleIdentifier> getArticleVariantBundleIdentifierListForEan(
            String ean,
            String countryCode,
            String languageCode,
            String salesLine) throws Exception {
        return articleCacheService.getArticleVariantBundleListForEan(ean, countryCode, salesLine, languageCode);
    }


    private List<ArticleVariantBundleIdentifier> getArticleVariantBundleIdentifierListForSubsys(
            Long subsysArtNo,
            String countryCode,
            String languageCode,
            String salesLine) throws Exception {
        return articleCacheService.getArticleVariantBundleListForSubsys(subsysArtNo, countryCode, salesLine, languageCode);
    }


    private List<ArticleVariantBundleIdentifier> getArticleVariantBundleIdentifierListForArticleVariantBundle(
            Long articleNumber,
            Long variantNumber,
            Long bundleNumber,
            String countryCode,
            String salesLine,
            String languageCode) throws Exception {
        return articleCacheService.getArticleVariantBundleListForArticleVariantBundle(articleNumber, variantNumber, bundleNumber, countryCode, salesLine, languageCode);
    }


    private List<ArticleVariantBundleIdentifier> getArticleVariantBundleIdentifierListForArticleNumber(
            Long articleNumber,
            String countryCode,
            String salesLine,
            String languageCode) throws Exception {
        return articleCacheService.getArticleVariantBundleListForArticleNumber(articleNumber, countryCode, salesLine, languageCode);
    }


    private ArticleIdTreeStructureList transformArticleVariantBundleListToArticleIdTreeStructureList(
            List<ArticleVariantBundleIdentifier> articleVariantBundleList) {
        ArticleIdTreeStructureList articleIdTreeStructureList = new ArticleIdTreeStructureList();
        for (ArticleVariantBundleIdentifier articleVariantBundleIdentifier : articleVariantBundleList) {
            if (articleIdTreeStructureList == null) {
                articleIdTreeStructureList = new ArticleIdTreeStructureList(new ArrayList<>(Arrays.asList(new ArticleIdTreeStructure(
                        articleVariantBundleIdentifier.getArticleNumber(),
                        transformArticleVariantBundleListToArticleComponentByArticle(
                                articleVariantBundleList,
                                articleVariantBundleIdentifier.getArticleNumber())))));
            } else {
                Integer articleIndex = articleIdTreeStructureList.findArticleIndex(articleVariantBundleIdentifier.getArticleNumber());
                if (articleIndex == null) {
                    articleIdTreeStructureList.setArticleIdTreeStructureList(new ArrayList<>(Arrays.asList(new ArticleIdTreeStructure(
                            articleVariantBundleIdentifier.getArticleNumber(),
                            transformArticleVariantBundleListToArticleComponentByArticle(
                                    articleVariantBundleList,
                                    articleVariantBundleIdentifier.getArticleNumber())))));
                } else if (articleIndex == -1) {
                    articleIdTreeStructureList.getArticleIdTreeStructureList().add(new ArticleIdTreeStructure(
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
            List<ArticleVariantBundleIdentifier> articleVariantBundleList,
            Long articleNumber) {
        ArticleComponent articleComponent = new ArticleComponent();
        for (ArticleVariantBundleIdentifier articleVariantBundleIdentifier : articleVariantBundleList) {
            if (articleVariantBundleIdentifier.getArticleNumber().equals(articleNumber)) {
                articleComponent.addVariantBundle(
                        articleVariantBundleIdentifier.getVariantNumber(),
                        articleVariantBundleIdentifier.getBundleNumber()
                );
            }
        }
        return articleComponent;
    }


    private List<RawArticleData> getRawArticleDataListForArticleVariantBundleIdentifierList(
            List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList,
            String countryCode, String salesLine, String languageCode, Integer storeNo) throws Exception {
        List<RawArticleData> rawArticleDataList = new ArrayList<>();
        ArticleIdTreeStructureList articleIdTreeStructureList = transformArticleVariantBundleListToArticleIdTreeStructureList(
                articleVariantBundleIdentifierList);

        for (ArticleIdTreeStructure articleIdTreeStructure : articleIdTreeStructureList.getArticleIdTreeStructureList()) {
            ArticleComponent articleComponent = transformArticleVariantBundleListToArticleComponentByArticle(
                    getArticleVariantBundleIdentifierListForArticleNumber(
                            articleIdTreeStructure.getArticleNumber(),
                            countryCode, salesLine, languageCode),
                    articleIdTreeStructure.getArticleNumber());

            for (ArticleComponent.ArticleVariant articleVariant : articleIdTreeStructure.getArticleComponent().getVariantList()) {
                for (ArticleComponent.ArticleBundle articleBundle : articleVariant.getBundleList()) {
                    ArticleId articleId = new ArticleId(
                            articleIdTreeStructure.getArticleNumber(),
                            articleVariant.getVariantNumber(),
                            articleBundle.getBundleNumber());

                    CompletableFuture<ArticleMasterData> articleMasterData = CompletableFuture.supplyAsync(
                            () -> {
                                try {
                                    return articleMasterDataRepository.getArticleMasterData(
                                            articleId,countryCode, salesLine, languageCode, storeNo).get(20, TimeUnit.SECONDS);
                                } catch (ArticleNotFoundException ex) {
                                    throw new ArticleNotFoundException();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }, executor
                    );

                    CompletableFuture<RawArticleStoreMasterData> rawArticleStoreMasterData = CompletableFuture.supplyAsync(
                            () -> {
                                try {
                                    return rawArticleStoreMasterDataRepository.getRawArticleStoreMasterData(
                                            articleId, countryCode, salesLine, languageCode, storeNo).get(20, TimeUnit.SECONDS);
                                } catch (ArticleNotFoundException ex) {
                                    throw new ArticleNotFoundException();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }, executor
                    );

                    CompletableFuture<ArticleDataFromMovie> articleDataFromMovie = CompletableFuture.supplyAsync(
                            () -> {
                                try {
                                    return articleDataFromMovieRepository.getArticleDataFromMovie(
                                            articleId, countryCode, salesLine, storeNo).get(20, TimeUnit.SECONDS);
                                } catch (ArticleNotFoundException ex) {
                                    throw new ArticleNotFoundException();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }, executor
                    );

                    CompletableFuture<List<ArticleStockLocation>> articleStockLocationList = CompletableFuture.supplyAsync(
                            () -> {
                                try {
                                    return aclBundleStockLocationRepository.getArticleStockLocation(
                                            articleId, countryCode, salesLine, storeNo).get(20, TimeUnit.SECONDS);
                                } catch (ArticleNotFoundException ex) {
                                    throw new ArticleNotFoundException();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }, executor
                    );

                    CompletableFuture.allOf(articleMasterData, rawArticleStoreMasterData, articleDataFromMovie, articleStockLocationList);
                    StockInformation stockDetails = null;
                    List<ArticlePlannedDeliveryDate> plannedDeliveryDates = null;
                    if (articleDataFromMovie.get() != null) {
                        stockDetails = articleDataFromMovie.get().getArticleStockData();
                        plannedDeliveryDates = articleDataFromMovie.get().getArticlePlannedDeliveryDates();
                    }

                    RawArticleData rawArticleData = RawArticleData.builder()
                            .articleId(articleId)
                            .articleMasterData(articleMasterData.get())
                            .articleStoreMasterData(rawArticleStoreMasterData.get())
                            .stockDetails(stockDetails)
                            .plannedDeliveryDates(plannedDeliveryDates)
                            .stockLocations(articleStockLocationList.get())
                            .articleComponent(articleComponent)
                            .build();

                    if (rawArticleDataList == null) {
                        rawArticleDataList = new ArrayList<>(Arrays.asList(rawArticleData));
                    } else {
                        rawArticleDataList.add(rawArticleData);
                    }

                }
            }
        }
        return rawArticleDataList;
    }

    private List<ArticleMasterData> getRawArticleMasterDataListForArticleVariantBundleIdentifierList(
            List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList,
            String countryCode, String salesLine, String languageCode, Integer storeNo) throws Exception {
        List<ArticleMasterData> rawArticleMasterData = new ArrayList<>();
        ArticleIdTreeStructureList articleIdTreeStructureList = transformArticleVariantBundleListToArticleIdTreeStructureList(
                articleVariantBundleIdentifierList);
        for (ArticleIdTreeStructure articleIdTreeStructure : articleIdTreeStructureList.getArticleIdTreeStructureList()) {
            for (ArticleComponent.ArticleVariant articleVariant : articleIdTreeStructure.getArticleComponent().getVariantList()) {
                for (ArticleComponent.ArticleBundle articleBundle : articleVariant.getBundleList()) {
                    ArticleId articleId = ArticleId.builder()
                            .articleNumber(articleIdTreeStructure.getArticleNumber())
                            .variantNumber(articleVariant.getVariantNumber())
                            .bundleNumber(articleBundle.getBundleNumber())
                            .build();
                    ArticleMasterData articleMasterData = articleMasterDataRepository.getArticleMasterData(articleId, countryCode, salesLine, languageCode, storeNo).get();
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
