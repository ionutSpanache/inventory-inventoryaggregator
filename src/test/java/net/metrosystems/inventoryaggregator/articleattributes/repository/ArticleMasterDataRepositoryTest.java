package net.metrosystems.inventoryaggregator.articleattributes.repository;

import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.PromotionArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.SimplePromotionArticleData;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.service.DocumentsService;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_promotion_article.PromotionDescription;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class ArticleMasterDataRepositoryTest {

  @Mock
  private ArticleCacheService articleCacheService;

  @Mock
  private DocumentsService documentsService;

  @Mock
  private ExecutorService forkJoinPoolCustom;

  private final String COUNTRY_CODE = "NL";
  private final Integer STORE_NUMBER = 1;

  private final String DEFAULT_LANGUAGE_CODE = "EN";
  private final String NETHERLANDS_LANGUAGE_CODE = "NL";
  private final String ROMANIA_LANGUAGE_CODE = "RO";

  private final LocalDate CURRENT_DATE = now();

  private ArticleMasterDataRepository articleMasterDataRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    articleMasterDataRepository = new ArticleMasterDataRepository(articleCacheService, documentsService, "MCC",
        forkJoinPoolCustom);
  }

  @Test
  void getPromotion_COUNTRY_LANGUAGE_CODE() {

    PromotionArticleData promotionArticleData = generatePromotion(
        true,
        NETHERLANDS_LANGUAGE_CODE,
        CURRENT_DATE.minusMonths(2),
        CURRENT_DATE.plusMonths(2),
        "MCC");
    List<PromotionArticleData> promotionArticleDataList = List.of(
        promotionArticleData,
        generatePromotion(
            true,
            DEFAULT_LANGUAGE_CODE,
            CURRENT_DATE.minusMonths(1),
            CURRENT_DATE.plusMonths(3),
            "MCC"),
        generatePromotion(
            true,
            ROMANIA_LANGUAGE_CODE,
            CURRENT_DATE.minusMonths(5),
            CURRENT_DATE.plusMonths(2),
            "MCC"));
    when(articleCacheService.getPromotionArticleData(
        COUNTRY_CODE, generateArticleId(123L, 1L, 1L), STORE_NUMBER))
        .thenReturn(
            promotionArticleDataList);

    ArticleId articleId = generateArticleId(123L, 1L, 1L);

    SimplePromotionArticleData simplePromotionArticleData =
        articleMasterDataRepository.getActivePromotionForToday(
            COUNTRY_CODE, 1, NETHERLANDS_LANGUAGE_CODE, articleId);
    assertEquals(
        NETHERLANDS_LANGUAGE_CODE + "_TEST", simplePromotionArticleData.getPromotionDescription());
    assertEquals(CURRENT_DATE.minusMonths(2), simplePromotionArticleData.getPromotionStart());
    assertEquals(CURRENT_DATE.plusMonths(2), simplePromotionArticleData.getPromotionEnd());
  }

  @Test
  void getPromotion_WITH_DEFAULT_LANGUAGE_CODE() {

    PromotionArticleData promotionArticleData = generatePromotion(
        true, null, CURRENT_DATE.minusMonths(1), CURRENT_DATE.plusMonths(3), "MCC");
    PromotionArticleData promotionArticleData1 = generatePromotion(
        true, null, CURRENT_DATE.minusMonths(5), CURRENT_DATE.plusMonths(2), "MCC");
    List<PromotionArticleData> promotionArticleDataList = List.of(
        promotionArticleData,
        promotionArticleData1);
    when(articleCacheService.getPromotionArticleData(
        COUNTRY_CODE, generateArticleId(123L, 1L, 1L), STORE_NUMBER))
        .thenReturn(
            promotionArticleDataList);

    ArticleId articleId = generateArticleId(123L, 1L, 1L);
    SimplePromotionArticleData simplePromotionArticleData =
        articleMasterDataRepository.getActivePromotionForToday(
            COUNTRY_CODE, 1, NETHERLANDS_LANGUAGE_CODE, articleId);

    assertEquals(
        DEFAULT_LANGUAGE_CODE + "_TEST", simplePromotionArticleData.getPromotionDescription());
    assertEquals(CURRENT_DATE.minusMonths(5), simplePromotionArticleData.getPromotionStart());
    assertEquals(CURRENT_DATE.plusMonths(2), simplePromotionArticleData.getPromotionEnd());
  }

  @Test
  void getPromotion_NO_DESCRIPTION() {

    PromotionArticleData promotionArticleData = generatePromotion(
        false, null, CURRENT_DATE.minusMonths(4), CURRENT_DATE.plusMonths(1), "MCC");
    List<PromotionArticleData> promotionArticleDataList = List.of(
        promotionArticleData,
        generatePromotion(
            false, null, CURRENT_DATE.minusMonths(2), CURRENT_DATE.plusMonths(3), "MCC"));
    when(articleCacheService.getPromotionArticleData(
        COUNTRY_CODE, generateArticleId(123L, 1L, 1L), STORE_NUMBER))
        .thenReturn(
            promotionArticleDataList);

    ArticleId articleId = generateArticleId(123L, 1L, 1L);

    SimplePromotionArticleData simplePromotionArticleData =
        articleMasterDataRepository.getActivePromotionForToday(
            COUNTRY_CODE, 1, ROMANIA_LANGUAGE_CODE, articleId);

    assertNull(simplePromotionArticleData.getPromotionDescription());
    assertEquals(CURRENT_DATE.minusMonths(4), simplePromotionArticleData.getPromotionStart());
    assertEquals(CURRENT_DATE.plusMonths(1), simplePromotionArticleData.getPromotionEnd());
  }

  @Test
  void getPromotion_Invalid_PromotionChannel() {

    PromotionArticleData promotionArticleData = generatePromotion(
        false, null, CURRENT_DATE.minusMonths(4), CURRENT_DATE.plusMonths(1), "APT");
    List<PromotionArticleData> promotionArticleDataList = List.of(
        promotionArticleData,
        generatePromotion(
            false, null, CURRENT_DATE.minusMonths(2), CURRENT_DATE.plusMonths(3), "ABC"));
    when(articleCacheService.getPromotionArticleData(
        COUNTRY_CODE, generateArticleId(123L, 1L, 1L), STORE_NUMBER))
        .thenReturn(
            promotionArticleDataList);

    ArticleId articleId = generateArticleId(123L, 1L, 1L);

    SimplePromotionArticleData simplePromotionArticleData =
        articleMasterDataRepository.getActivePromotionForToday(
            COUNTRY_CODE, 1, ROMANIA_LANGUAGE_CODE, articleId);

    assertNull(simplePromotionArticleData.getPromotionDescription());
    assertEquals(null, simplePromotionArticleData.getPromotionStart());
    assertEquals(null, simplePromotionArticleData.getPromotionEnd());
    assertEquals(null, simplePromotionArticleData.getPromotionChannel());
  }

  @Test
  void getPromotion_NO_PROMOTION() {

    PromotionArticleData promotionArticleData = generatePromotion(
        true, null, CURRENT_DATE.plusMonths(4), CURRENT_DATE.plusMonths(8), "MCC");
    List<PromotionArticleData> promotionArticleDataList = List.of(
        promotionArticleData,
        generatePromotion(
            true, null, CURRENT_DATE.plusMonths(2), CURRENT_DATE.plusMonths(3), "MCC"));
    ArticleId articleId = generateArticleId(123L, 1L, 1L);

    when(articleCacheService.getPromotionArticleData(
        COUNTRY_CODE, articleId, STORE_NUMBER))
        .thenReturn(
            promotionArticleDataList);

    SimplePromotionArticleData simplePromotionArticleData =
        articleMasterDataRepository.getActivePromotionForToday(
            COUNTRY_CODE, 1, ROMANIA_LANGUAGE_CODE, articleId);

    assertNull(simplePromotionArticleData.getPromotionDescription());
    assertNull(simplePromotionArticleData.getPromotionStart());
    assertNull(simplePromotionArticleData.getPromotionEnd());
  }

  private PromotionArticleData generatePromotion(
      Boolean hasDescription, String otherLanguageCode, LocalDate start, LocalDate end, String promotionChannel) {

    List<PromotionDescription> promotionDescriptionList = null;

    if (hasDescription) {

      promotionDescriptionList = new ArrayList<>();
      promotionDescriptionList.add(generatePromotionDescription(DEFAULT_LANGUAGE_CODE));

      if (otherLanguageCode != null) {
        promotionDescriptionList.add(generatePromotionDescription(otherLanguageCode));
      }
    }
    return PromotionArticleData.builder()
        .promotionId(UUID.randomUUID().toString())
        .promotionStart(start)
        .promotionEnd(end)
        .valid(true)
        .descriptions(promotionDescriptionList)
        .promotionChannel(promotionChannel)
        .build();
  }

  private PromotionDescription generatePromotionDescription(String language) {
    PromotionDescription promotionDescription = new PromotionDescription();
    promotionDescription.setDescription(language + "_TEST");
    promotionDescription.setLanguage(language);
    return promotionDescription;
  }

  private ArticleId generateArticleId(Long articleNumber, Long variantNumber, Long bundleNumber) {
    return ArticleId.builder()
        .articleNumber(articleNumber)
        .variantNumber(variantNumber)
        .bundleNumber(bundleNumber)
        .build();
  }
}
