package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.client.FilterAAAClient;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model.*;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.service.FilterAAAService;
import net.metrosystems.inventoryaggregator.articleid.model.ArticleId;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_aaa.ArticleCacheAAAClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class DocumentsService {

  @Autowired FilterAAAClient filterAAAClient;
  @Autowired FilterAAAService filterAAAService;
  @Autowired
  private ArticleCacheAAAClient articleCacheClient;

  public ArticleId tranformDocumentInArticleId(String document) {
    ArticleId articleId = new ArticleId();
    int indexOfStartData = document.indexOf("?");
    document = document.substring(indexOfStartData + 1);
    Arrays.stream(document.split("&"))
        .forEach(
            articleAtributes -> {
              if (articleAtributes.contains("mmsartno")) {
                articleId.setArticleNumber(Long.valueOf(articleAtributes.replace("mmsartno=", "")));

              } else if (articleAtributes.contains("variantno")) {
                articleId.setVariantNumber(
                    Long.valueOf(articleAtributes.replace("variantno=", "")));

              } else if (articleAtributes.contains("bundleno")) {
                articleId.setBundleNumber(Long.valueOf(articleAtributes.replace("bundleno=", "")));
              }
            });

    return articleId;
  }

  public List<ArticleId> transformDocumentListInArticleIdList(Documents documents, Integer storeNo) {

    List<ArticleId> articleIdList = new ArrayList<>();
    Stream.ofNullable(documents.getArticleKeys())
        .flatMap(Collection::stream)
        .forEach(
            document -> {
              if(isStorePresent(document, storeNo)) {
                ArticleId articleId = this.tranformDocumentInArticleId(document);
                articleIdList.add(articleId);
              }
            });
    return articleIdList;
  }

  public Documents getDocumentsNextpage(Documents documents, Filter filter) throws Exception {

    while (documents != null && documents.getNextPageToken() != null) {
      Documents documentsNextPage =
          filterAAAClient.getFilteredDocumentsNextPage(filter, documents.getNextPageToken());
      if (documentsNextPage != null) {
        documents
            .getArticleKeys()
            .addAll((Collection<? extends String>) documentsNextPage.getArticleKeys());
        documents.setNextPageToken(documentsNextPage.getNextPageToken());
      }
    }
    return documents;
  }

  public Documents getDocumentsByBarcode(
      String countryCode, String salesLine, String languageCode, String barCode) throws Exception {
    Filter filter = filterAAAService.initializeFilter(countryCode, salesLine);
    SubFilterKey subFilterKey =
        filterAAAService.addSubFilterKey(KeysWithTypes.salesBarcode, barCode, languageCode);
    filter.getFilterKeys().get(0).getSubFilterKeys().add(subFilterKey);
    return filterAAAClient.getFilteredDocuments(filter);
  }

  public Documents getIsuItemUrlByArtVarBundle(String countryCode, net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId articleId) {
    FilterKeyIsuMaster filterKeyIsuMaster = FilterKeyIsuMaster.builder()
            .bundleNumber(articleId.getBundleNumber())
            .variantNumber(articleId.getVariantNumber())
            .articleNumber(articleId.getArticleNumber())
            .countryCode(countryCode)
            .build();

    FilterIsuMaster filterIsuMaster = FilterIsuMaster
            .builder()
            .filterKeys(List.of(filterKeyIsuMaster))
            .build();

    return  filterAAAClient.getFilteredDocumentsForIsuMaster(filterIsuMaster);

  }

  public Documents getIduItemDerivedUrlByArtVarBundle(String countryCode, net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId articleId) {

    SubFilterKey articleNumberSubFilterKey = SubFilterKey.builder()
            .key(KeysWithTypes.articleNumber.getCode())
            .type(KeysWithTypes.articleNumber.getType())
            .query(String.valueOf(articleId.getArticleNumber()))
            .build();

    SubFilterKey variantNumberSubFilterKey = SubFilterKey.builder()
            .key(KeysWithTypes.variantNumber.getCode())
            .type(KeysWithTypes.variantNumber.getType())
            .query(String.valueOf(articleId.getVariantNumber()))
            .build();

    SubFilterKey bundleNumberSubFilterKey = SubFilterKey.builder()
            .key(KeysWithTypes.bundleNumber.getCode())
            .type(KeysWithTypes.bundleNumber.getType())
            .query(String.valueOf(articleId.getBundleNumber()))
            .build();

    FilterKeyIduItemDerivedStandard filterKeyIduItemDerivedStandard = FilterKeyIduItemDerivedStandard
        .builder()
        .subFilterKeys(List.of(articleNumberSubFilterKey, variantNumberSubFilterKey, bundleNumberSubFilterKey))
        .country(countryCode)
        .build();


    FilterIduItemDerivedStandard filterIduItemDerivedStandard = FilterIduItemDerivedStandard.builder()
            .filterKeys(List.of(filterKeyIduItemDerivedStandard))
            .build();

    return  filterAAAClient.getFilteredDocumentsForIduItemDerivedStandard(filterIduItemDerivedStandard);

  }



  public Documents getIsuItemStoreMerchandiseByArtVarBundle(String countryCode, net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId articleId) {
    FilterKeyIsuItemStoreMerchandise filterKeyStoreMerchandise = FilterKeyIsuItemStoreMerchandise.builder()
            .bundleNumber(articleId.getBundleNumber())
            .variantNumber(articleId.getVariantNumber())
            .articleNumber(articleId.getArticleNumber())
            .countryCode(countryCode)
            .build();

    FilterIsuItemStoreMerchandise filterStoreMerchandise = FilterIsuItemStoreMerchandise
            .builder()
            .filterKeys(List.of(filterKeyStoreMerchandise))
            .build();

    return  filterAAAClient.getFilteredDocumentsForIsuStoreMerchandise(filterStoreMerchandise);

  }

  public Documents getDocumentsByArticleNumber(
      String countryCode, String salesLine, String languageCode, Long articleNumber)
      throws Exception {
    Filter filter = filterAAAService.initializeFilter(countryCode, salesLine);
    SubFilterKey subFilterKey =
        filterAAAService.addSubFilterKey(
            KeysWithTypes.mmsArtNo, String.valueOf(articleNumber), languageCode);
    filter.getFilterKeys().get(0).getSubFilterKeys().add(subFilterKey);
    Documents documents = filterAAAClient.getFilteredDocuments(filter);
    return this.getDocumentsNextpage(documents, filter);
  }

  public Documents getDocumentsByArticleNumberVarianBundleId(
      String countryCode,
      String salesLine,
      String languageCode,
      Long articleNumber,
      Long variantNr,
      Long bundleId)
      throws Exception {
    Filter filter = filterAAAService.initializeFilter(countryCode, salesLine);
    SubFilterKey subFilterKeyArtNo =
        filterAAAService.addSubFilterKey(
            KeysWithTypes.mmsArtNo, String.valueOf(articleNumber), languageCode);
    filter.getFilterKeys().get(0).getSubFilterKeys().add(subFilterKeyArtNo);
    SubFilterKey subFilterKeyVariantNr =
        filterAAAService.addSubFilterKey(
            KeysWithTypes.variantNo, String.valueOf(variantNr), languageCode);
    filter.getFilterKeys().get(0).getSubFilterKeys().add(subFilterKeyVariantNr);
    SubFilterKey subFilterKeyBundleId =
        filterAAAService.addSubFilterKey(
            KeysWithTypes.bundleNo, String.valueOf(bundleId), languageCode);
    filter.getFilterKeys().get(0).getSubFilterKeys().add(subFilterKeyBundleId);
    Documents documents = filterAAAClient.getFilteredDocuments(filter);
    return this.getDocumentsNextpage(documents, filter);
  }

  public Documents getDocumentsBySubsysArticleNumber(
      String countryCode, String salesLine, String languageCode, Long subsysArticleNumber)
      throws Exception {
    Filter filter = filterAAAService.initializeFilter(countryCode, salesLine);
    SubFilterKey subFilterKey =
        filterAAAService.addSubFilterKey(
            KeysWithTypes.subsysNo, String.valueOf(subsysArticleNumber), languageCode);
    filter.getFilterKeys().get(0).getSubFilterKeys().add(subFilterKey);
    Documents documents = filterAAAClient.getFilteredDocuments(filter);
    return this.getDocumentsNextpage(documents, filter);
  }

  @SneakyThrows
  public Documents getDocumentsBySupplier(
      String countryCode, String salesLine, Long supplierNumber) {
    FilterSupplierInformation filter =
        filterAAAService.initializeSupplierInformationFilter(
            countryCode, salesLine, supplierNumber);
    Documents documents = filterAAAClient.getFilteredDocuments(filter);
    if (documents != null && !CollectionUtils.isEmpty(documents.getArticleKeys()))
      documents
          .getArticleKeys()
          .removeIf(
              articleKey ->
                  articleKey.toLowerCase().contains("environment=dev")
                      || articleKey.toLowerCase().contains("environment=qa"));
    return documents;
  }

  public boolean isStorePresent(String documents, Integer storeNo) {
    ArticleCacheIhdsTree articleCacheDocument = articleCacheClient.getArticleCacheDocumentByURL(documents);
    boolean storeNumberFound;
    try {
      storeNumberFound = !articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheDataGroup().stream()
              .filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 320))
              .filter(articleCacheDataGroup -> (articleCacheDataGroup.getStoreNo() == storeNo))
              .collect(Collectors.toList()).isEmpty();
    }catch(Exception e) {
      return false;
    }

    return storeNumberFound;
  }
}
