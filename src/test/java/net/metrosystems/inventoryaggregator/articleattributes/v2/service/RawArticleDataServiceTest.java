package net.metrosystems.inventoryaggregator.articleattributes.v2.service;

import net.metrosystems.inventoryaggregator.articleattributes.model.*;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.repository.GeneralDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.AclStoreMasterData;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import net.metrosystems.inventoryaggregator.common.movieacl.MovieAclService;
import net.metrosystems.inventoryaggregator.stocklocation.model.StockLocation;
import net.metrosystems.inventoryaggregator.stocklocation.repository.StockLocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RawArticleDataServiceTest {

    @Mock
    private GeneralDataRepository generalDataRepository;

    @Mock
    private ArticleProcessingService articleProcessingService;

    @Mock
    private StockLocationRepository stockLocationRepository;

    @Mock
    private RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;

    @Mock
    private MovieAclService movieAclService;

    @InjectMocks
    private RawArticleDataService rawArticleDataService;

    private ArticleId articleId;
    private StoreId storeId;
    private List<RawArticleData> rawArticleDataList;

    @BeforeEach
    void setUp() {
        articleId = buildArticleId();
        storeId = buildStoreId();
        rawArticleDataList = new ArrayList<>();
    }

    @Test
    void buildRawArticleDataList_returnValue() throws Exception {
        ArticleComponent articleComponent = new ArticleComponent();
        ArticleMasterData articleMasterData = buildArticleMasterData();
        MultiLanguageData multiLanguageData = new MultiLanguageData();
        ArticleMasterId articleMasterId = ArticleMasterId.builder()
                .articleMasterData(articleMasterData)
                .multiLanguageData(multiLanguageData)
                .build();

        AclStoreMasterData aclStoreMasterData = new AclStoreMasterData();
        StockData stockData = buildStockData();
        List<StockLocation> stockLocations = new ArrayList<>();
        RawArticleStoreMasterData rawArticleStoreMasterData = new RawArticleStoreMasterData();

        when(articleProcessingService.getArticleComponent(anyString(), any(Integer.class), any(Long.class)))
                .thenReturn(CompletableFuture.completedFuture(articleComponent));
        when(articleProcessingService.getArticleMasterId(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture(articleMasterId));
        when(articleProcessingService.getAclStoreMasterData(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture(aclStoreMasterData));
        when(articleProcessingService.getArticleDataFromMovie(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture(stockData));
        when(stockLocationRepository.getArticleStockLocation(any(ArticleId.class), anyString(), any(Integer.class), any()))
                .thenReturn(CompletableFuture.completedFuture(stockLocations));
        when(movieAclService.getAverageSales(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture("5.5"));
        when(rawArticleStoreMasterDataRepository.getRawArticleStoreMasterData(any(), any()))
                .thenReturn(rawArticleStoreMasterData);

        rawArticleDataService.buildRawArticleDataList(articleId, storeId, rawArticleDataList, Optional.empty());

        assertNotNull(rawArticleDataList);
        assertEquals(1, rawArticleDataList.size());

        RawArticleData result = rawArticleDataList.getFirst();
        assertEquals(articleId, result.getArticleId());
        assertEquals(articleMasterData, result.getArticleMasterData());
        assertNotNull(result.getStockDetails());
        assertEquals(new DecimalFormat().format(Double.valueOf("5.5")), result.getStockDetails().getDailyMeanSales30());
        assertEquals(articleComponent, result.getArticleComponent());
        assertEquals(stockLocations, result.getStockLocations());
        assertEquals(rawArticleStoreMasterData, result.getArticleStoreMasterData());

        // Verify ProductLifeCycleData
        ProductLifeCycleData productLifeCycleData = result.getProductLifeCycleData();
        assertNotNull(productLifeCycleData);
        assertEquals("A1", productLifeCycleData.getAssortmentCluster());
        assertEquals(1, productLifeCycleData.getPresencePeriodStartWeek());
        assertEquals(2024, productLifeCycleData.getPresencePeriodStartYear());
        assertEquals(52, productLifeCycleData.getPresencePeriodEndWeek());
        assertEquals(2024, productLifeCycleData.getPresencePeriodEndYear());

        // Verify other fields
        assertEquals("Y", result.getChannelDeliveryFlag());
        assertEquals(true, result.getSalesAreaFlag());
        assertEquals(1L, result.getReturnPossibilityStoreSupplierNumber());

        verify(generalDataRepository).enhanceRawArticleStoreMasterDataWithDepartmentName(
                storeId.getCountryCode(),
                storeId.getSalesLine(),
                storeId.getStoreNumber(),
                aclStoreMasterData);
    }

    @Test
    void buildRawArticleDataList_withNullStockData_returnValue() throws Exception {
        ArticleMasterData articleMasterData = buildArticleMasterData();
        MultiLanguageData multiLanguageData = new MultiLanguageData();
        ArticleMasterId articleMasterId = ArticleMasterId.builder()
                .articleMasterData(articleMasterData)
                .multiLanguageData(multiLanguageData)
                .build();

        when(articleProcessingService.getArticleComponent(anyString(), any(Integer.class), any(Long.class)))
                .thenReturn(CompletableFuture.completedFuture(new ArticleComponent()));
        when(articleProcessingService.getArticleMasterId(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture(articleMasterId));
        when(articleProcessingService.getAclStoreMasterData(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture(new AclStoreMasterData()));
        when(articleProcessingService.getArticleDataFromMovie(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(stockLocationRepository.getArticleStockLocation(any(ArticleId.class), anyString(), any(Integer.class), any()))
                .thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        when(movieAclService.getAverageSales(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(rawArticleStoreMasterDataRepository.getRawArticleStoreMasterData(any(), any()))
                .thenReturn(new RawArticleStoreMasterData());

        rawArticleDataService.buildRawArticleDataList(articleId, storeId, rawArticleDataList, Optional.empty());

        assertNotNull(rawArticleDataList);
        assertEquals(1, rawArticleDataList.size());
        assertNull(rawArticleDataList.getFirst().getStockDetails());
        assertNull(rawArticleDataList.getFirst().getPlannedDeliveryDates());
    }

    @Test
    void buildRawArticleDataList_withStockDataButNullDailyMeanSales_returnValue() throws Exception {
        ArticleMasterData articleMasterData = buildArticleMasterData();
        MultiLanguageData multiLanguageData = new MultiLanguageData();
        ArticleMasterId articleMasterId = ArticleMasterId.builder()
                .articleMasterData(articleMasterData)
                .multiLanguageData(multiLanguageData)
                .build();

        StockData stockData = buildStockData();

        when(articleProcessingService.getArticleComponent(anyString(), any(Integer.class), any(Long.class)))
                .thenReturn(CompletableFuture.completedFuture(new ArticleComponent()));
        when(articleProcessingService.getArticleMasterId(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture(articleMasterId));
        when(articleProcessingService.getAclStoreMasterData(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture(new AclStoreMasterData()));
        when(articleProcessingService.getArticleDataFromMovie(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture(stockData));
        when(stockLocationRepository.getArticleStockLocation(any(ArticleId.class), anyString(), any(Integer.class), any()))
                .thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        when(movieAclService.getAverageSales(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(rawArticleStoreMasterDataRepository.getRawArticleStoreMasterData(any(), any()))
                .thenReturn(new RawArticleStoreMasterData());

        rawArticleDataService.buildRawArticleDataList(articleId, storeId, rawArticleDataList, Optional.empty());

        assertNotNull(rawArticleDataList);
        assertEquals(1, rawArticleDataList.size());
        assertNotNull(rawArticleDataList.getFirst().getStockDetails());
        assertNull(rawArticleDataList.getFirst().getStockDetails().getDailyMeanSales30());
    }

    @Test
    void buildRawArticleDataList_withDifferentDailyMeanSalesFormat_returnValue() throws Exception {
        ArticleMasterData articleMasterData = buildArticleMasterData();
        MultiLanguageData multiLanguageData = new MultiLanguageData();
        ArticleMasterId articleMasterId = ArticleMasterId.builder()
                .articleMasterData(articleMasterData)
                .multiLanguageData(multiLanguageData)
                .build();

        StockData stockData = buildStockData();

        when(articleProcessingService.getArticleComponent(anyString(), any(Integer.class), any(Long.class)))
                .thenReturn(CompletableFuture.completedFuture(new ArticleComponent()));
        when(articleProcessingService.getArticleMasterId(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture(articleMasterId));
        when(articleProcessingService.getAclStoreMasterData(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture(new AclStoreMasterData()));
        when(articleProcessingService.getArticleDataFromMovie(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture(stockData));
        when(stockLocationRepository.getArticleStockLocation(any(ArticleId.class), anyString(), any(Integer.class), any()))
                .thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        when(movieAclService.getAverageSales(any(ArticleId.class), anyString(), anyString(), any(Integer.class)))
                .thenReturn(CompletableFuture.completedFuture("15.123456"));
        when(rawArticleStoreMasterDataRepository.getRawArticleStoreMasterData(any(), any()))
                .thenReturn(new RawArticleStoreMasterData());

        rawArticleDataService.buildRawArticleDataList(articleId, storeId, rawArticleDataList, Optional.empty());

        assertNotNull(rawArticleDataList);
        assertEquals(1, rawArticleDataList.size());
        assertNotNull(rawArticleDataList.getFirst().getStockDetails());
        assertEquals(new DecimalFormat().format(Double.valueOf("15.12")), rawArticleDataList.getFirst().getStockDetails().getDailyMeanSales30());
    }

    private ArticleId buildArticleId() {
        return ArticleId.builder()
                .articleNumber(123456L)
                .variantNumber(2L)
                .bundleNumber(1L)
                .build();
    }

    private StoreId buildStoreId() {
        return StoreId.builder()
                .countryCode("DE")
                .salesLine("01")
                .storeNumber(1)
                .build();
    }

    private ArticleMasterData buildArticleMasterData() {
        return ArticleMasterData.builder()
                .assortmentCluster("A1")
                .presencePeriodStartWeek(1L)
                .presencePeriodStartYear(2024L)
                .presencePeriodEndWeek(52L)
                .presencePeriodEndYear(2024L)
                .channelDeliveryFlag("Y")
                .salesAreaFlag(true)
                .returnPossibilityStoreSupplierNumber(1L)
                .build();
    }

    private StockData buildStockData() {
        StockData stockData = new StockData();
        StockInformation stockInformation = new StockInformation();
        stockData.setArticleStockData(stockInformation);
        stockData.setArticlePlannedDeliveryDates(new ArrayList<>());
        return stockData;
    }
}














