package net.metrosystems.inventoryaggregator.common.stock_information.movie.service;

import net.metrosystems.inventoryaggregator.common.stock_information.movie.client.MovieClient;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class MovieServiceTest {

    @Mock
    MovieClient movieClient;

    @InjectMocks
    private MovieService movieService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    public void reset() {
        Mockito.reset(movieClient);
    }

    @Test
    void get_article_stock_for_variant_bundle_return_code_is_zero() {
        when(movieClient.getStockDetailsForArticleVariantBundle(anyString(),
                anyString(), anyInt(), anyLong(), anyLong(), anyLong()))
                .thenReturn(createStockDetailsReturnZero());
        when(movieClient.getConsignmentStockForArticleVariantBundle(anyString(),
                anyString(), anyInt(), anyLong(), anyLong(), anyLong()))
                .thenReturn(createConsignmentStockReturnZero());

        MovieAdapter movieAdapter = movieService.getArticleStockForVariantBundle("NL","MCC", 10,
                1234L, 1L, 1L);
        //assertEquals(true, movieAdapter.isConsignmentstock());
        assertEquals(false, movieAdapter.isConsignmentstock());

    }

    @Test
    void get_article_stock_for_variant_bundle_return_code_is_one() {
        when(movieClient.getStockDetailsForArticleVariantBundle(anyString(),
                anyString(), anyInt(), anyLong(), anyLong(), anyLong()))
                .thenReturn(createStockDetailsReturnZero());
        when(movieClient.getConsignmentStockForArticleVariantBundle(anyString(),
                anyString(), anyInt(), anyLong(), anyLong(), anyLong()))
                .thenReturn(createConsignmentStockReturnOne());

        MovieAdapter movieAdapter = movieService.getArticleStockForVariantBundle("NL","MCC", 10,
                1234L, 1L, 1L);
        assertEquals(false, movieAdapter.isConsignmentstock());

    }

    private List<ConsignmentStock> createConsignmentStockReturnZero() {
        return List.of(ConsignmentStock.builder()
                .country("NL")
                .salesLine("MCC")
                .locationId(10)
                .consignmentStock(10.0)
                .returnCode(0)
                .build());

    }

    private List<ConsignmentStock> createConsignmentStockReturnOne() {
        return List.of(ConsignmentStock.builder()
                .country("NL")
                .salesLine("MCC")
                .locationId(10)
                .consignmentStock(null)
                .returnCode(1)
                .build());

    }

    private List<StockDetails> createStockDetailsReturnZero() {
        return List.of(StockDetails.builder()
                .country("NL")
                .salesLine("MCC")
                .locationId(10)
                .articleNo(123L)
                .variant(1L)
                .bundleNo(1L)
                .bundleContent("123")
                .blockedStock(createStock())
                .movementDetails(createMovementDetails())
                .openMovement(createStock())
                .openOrders(createStock())
                .reservation(createStock())
                .systemStock(createStock())
                .markdownStock(createStock())
                .virtualStock(createStock())
                .stockSummary(createStockSummary())
                .stockRelatedData(createStockRelatedData())
                .message("OK")
                .returnCode(0)
                .build());

    }

    private List<Stock> createStock() {
        return List.of(Stock.builder()
                .quantity(12F)
                .lotNo("12")
                .blockedReason("RES")
                .reservationType("RES")
                .virtualStockType(3)
                .processedQuantity(2F)
                .reservedQuantity(1F)
                .deliveryDate("12/12/2020")
                .orderNumber("23")
                .orderDate("10/10/2020")
                .promotionId(678)
                .supplierNumber(987)
                .lastGRDate("12/10/2020")
                .lastSalesDate("10/09/2020")
                .lastSCDate("09/10/2020")
                .reason("EST")
                .lastSCQuantity(12D)
                .lastSalesQuantity(10D)
                .lastGRQuantity(11D)
                .build());
    }

    private MovementDetails createMovementDetails() {
        return MovementDetails.builder()
                .markDownStock(createMovementDetailsMarkDownStock())
                .systemStock(createMovementDetailsSystemStock())
                .build();
    }

    private StockSummary createStockSummary() {
        return StockSummary.builder()
                .availableStock(12F)
                .availableVirtualStock(23F)
                .blockedStock(2D)
                .consignmentStock(2F)
                .markDownStock(3D)
                .normalStock(5F)
                .openMovement(2F)
                .reservation(1D)
                .systemStock(2F)
                .openGoodReceiving(4D)
                .build();
    }

    private StockRelatedData createStockRelatedData() {
        return StockRelatedData.builder()
                .dailyMeanSales("21/11/2020")
                .stockCoverage("123")
                .build();
    }

    private MovementDetailsSystemStock systemStock;
    private MovementDetailsMarkDownStock markDownStock;

    private MovementDetailsSystemStock createMovementDetailsSystemStock() {
        return MovementDetailsSystemStock.builder()
                .GR(12F)
                .SALES(2F)
                .SC(4F)
                .build();
    }

    private MovementDetailsMarkDownStock createMovementDetailsMarkDownStock() {
        return MovementDetailsMarkDownStock.builder()
                .LABEL(12F)
                .SALES(10F)
                .build();
    }

}