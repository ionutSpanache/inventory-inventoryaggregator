package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.CompleteArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.ContentIduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.Domain;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.MerchandiseArticleDescription;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleDetailsService {
  private static final Map<String, String> LANGUAGE_CODE_EXCEPTIONS = new HashMap<>();

  static {
    LANGUAGE_CODE_EXCEPTIONS.put("UA", "UK");
    LANGUAGE_CODE_EXCEPTIONS.put("KZ", "RU");
    LANGUAGE_CODE_EXCEPTIONS.put("MD", "RO");
    LANGUAGE_CODE_EXCEPTIONS.put("PK", "UR");
    LANGUAGE_CODE_EXCEPTIONS.put("RS", "SR");
    LANGUAGE_CODE_EXCEPTIONS.put("CZ", "CS");
  }

  private final ArticleMasterDataRepository articleMasterDataRepository;
  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;

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
