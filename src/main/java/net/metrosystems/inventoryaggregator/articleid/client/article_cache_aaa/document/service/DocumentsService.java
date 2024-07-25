package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.client.FilterAAAClient;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model.*;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.service.FilterAAAService;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_aaa.ArticleCacheAAAClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class DocumentsService {

  @Autowired
  FilterAAAClient filterAAAClient;
  @Autowired
  @Lazy //TODO: resolve circular dependency
  FilterAAAService filterAAAService;
  @Autowired
  @Lazy //TODO: resolve circular dependency
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

  public List<ArticleId> transformDocumentListInArticleIdList(
      Documents documents, Integer storeNo) {

    List<ArticleId> articleIdList = new ArrayList<>();
    Stream.ofNullable(documents.getArticleKeys())
        .flatMap(Collection::stream)
        .forEach(
            document -> {
              if (isStorePresent(document, storeNo)) {
                ArticleId articleId = this.tranformDocumentInArticleId(document);
                articleIdList.add(articleId);
              }
            });
    return articleIdList;
  }

  public <T> Documents getDocumentsNextpage(Documents documents, T filter) throws Exception {

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

  public Documents getIsuItemUrlByArtVarBundle(
      String countryCode,
      net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId articleId) {
    FilterKeyIsuMaster filterKeyIsuMaster =
        FilterKeyIsuMaster.builder()
            .bundleNumber(articleId.getBundleNumber())
            .variantNumber(articleId.getVariantNumber())
            .articleNumber(articleId.getArticleNumber())
            .countryCode(countryCode)
            .build();

    FilterIsuMaster filterIsuMaster =
        FilterIsuMaster.builder().filterKeys(List.of(filterKeyIsuMaster)).build();

    return filterAAAClient.getFilteredDocumentsForIsuMaster(filterIsuMaster);
  }

  public Documents getIduItemDerivedUrlByArtVarBundle(
      String countryCode,
      net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId articleId,
      String company) {

    SubFilterKey articleNumberSubFilterKey =
        SubFilterKey.builder()
            .key(KeysWithTypes.articleNumber.getCode())
            .type(KeysWithTypes.articleNumber.getType())
            .query(String.valueOf(articleId.getArticleNumber()))
            .build();

    SubFilterKey variantNumberSubFilterKey =
        SubFilterKey.builder()
            .key(KeysWithTypes.variantNumber.getCode())
            .type(KeysWithTypes.variantNumber.getType())
            .query(String.valueOf(articleId.getVariantNumber()))
            .build();

    SubFilterKey bundleNumberSubFilterKey =
        SubFilterKey.builder()
            .key(KeysWithTypes.bundleNumber.getCode())
            .type(KeysWithTypes.bundleNumber.getType())
            .query(String.valueOf(articleId.getBundleNumber()))
            .build();

    List<SubFilterKey> subFilterKeys;
    if (company != null) {
      SubFilterKey companySubFilterKey =
          SubFilterKey.builder()
              .key(KeysWithTypes.company.getCode())
              .type(KeysWithTypes.company.getType())
              .query(company)
              .build();
      subFilterKeys =
          List.of(
              articleNumberSubFilterKey,
              variantNumberSubFilterKey,
              bundleNumberSubFilterKey,
              companySubFilterKey);
    } else {
      subFilterKeys =
          List.of(articleNumberSubFilterKey, variantNumberSubFilterKey, bundleNumberSubFilterKey);
    }

    FilterKeyIduItemDerivedStandard filterKeyIduItemDerivedStandard =
        FilterKeyIduItemDerivedStandard.builder()
            .subFilterKeys(subFilterKeys)
            .country(countryCode)
            .build();

    FilterIduItemDerivedStandard filterIduItemDerivedStandard =
        FilterIduItemDerivedStandard.builder()
            .filterKeys(List.of(filterKeyIduItemDerivedStandard))
            .build();

    return filterAAAClient.getFilteredDocumentsForIduItemDerivedStandard(
        filterIduItemDerivedStandard);
  }


  public Documents getIsuItemStoreMerchandiseByArtVarBundle(
      String countryCode,
      ArticleId articleId) {
    FilterKeyIsuItemStoreMerchandise filterKeyStoreMerchandise =
        FilterKeyIsuItemStoreMerchandise.builder()
            .bundleNumber(articleId.getBundleNumber())
            .variantNumber(articleId.getVariantNumber())
            .articleNumber(articleId.getArticleNumber())
            .countryCode(countryCode)
            .build();

    FilterIsuItemStoreMerchandise filterStoreMerchandise =
        FilterIsuItemStoreMerchandise.builder()
            .filterKeys(List.of(filterKeyStoreMerchandise))
            .build();

    return filterAAAClient.getFilteredDocumentsForIsuStoreMerchandise(filterStoreMerchandise);
  }

  public Documents getDocumentsByArticleNumber(
      String countryCode, String salesLine, Long articleNumber)
      throws Exception {

    ArticleFilterKey artFilterKey  = new ArticleFilterKey();
    ArticleHardKey artHardKey = new ArticleHardKey(countryCode, salesLine, String.valueOf(articleNumber),
            null, null);
    artFilterKey.getSubFilterKeys().add(artHardKey);

    Documents documents = filterAAAClient.getFilteredDocuments(artFilterKey);
    return getDocumentsNextpage(documents, artFilterKey);
  }

  public Documents getDocumentsByArticleNumberVarianBundleId(
      String countryCode,
      String salesLine,
      Long articleNumber,
      Long variantNr,
      Long bundleId)
      throws Exception {

    ArticleFilterKey artFilterKey  = new ArticleFilterKey();
    ArticleHardKey artHardKey = new ArticleHardKey(countryCode, salesLine, String.valueOf(articleNumber),
            String.valueOf(variantNr), String.valueOf(bundleId));
    artFilterKey.getSubFilterKeys().add(artHardKey);

    Documents documents = filterAAAClient.getFilteredDocuments(artFilterKey);
    return this.getDocumentsNextpage(documents, artFilterKey);
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

  public boolean isStorePresent(String documents, Integer storeNo) {
    ArticleCacheIhdsTree articleCacheDocument =
        articleCacheClient.getArticleCacheDocumentByURL(documents);
    boolean storeNumberFound;
    try {
      storeNumberFound =
          !articleCacheDocument
              .getTransaction()
              .getArticleCacheIncident()
              .getArticleCacheDataGroup()
              .stream()
              .filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 320))
              .filter(articleCacheDataGroup -> (articleCacheDataGroup.getStoreNo() == storeNo))
              .collect(Collectors.toList())
              .isEmpty();
    } catch (Exception e) {
      return false;
    }

    return storeNumberFound;
  }

  public Map<net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId, String>
      getDocumentsForMultipleArticles(
          String countryCode,
          String salesLine,
          String languageCode,
          List<net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId>
              articleIdList)
          throws Exception {

    if (CollectionUtils.isEmpty(articleIdList)) return Collections.emptyMap();
    Filter filter = new Filter().setFilterKeys(new ArrayList<>());
    articleIdList.forEach(
        articleId -> {
          FilterKey filterKey = filterAAAService.initializeFilterKey(countryCode, salesLine);
          filterKey
              .getSubFilterKeys()
              .add(
                  filterAAAService.addSubFilterKey(
                      KeysWithTypes.mmsArtNo,
                      String.valueOf(articleId.getArticleNumber()),
                      languageCode));
          filterKey
              .getSubFilterKeys()
              .add(
                  filterAAAService.addSubFilterKey(
                      KeysWithTypes.variantNo,
                      String.valueOf(articleId.getVariantNumber()),
                      languageCode));
          filterKey
              .getSubFilterKeys()
              .add(
                  filterAAAService.addSubFilterKey(
                      KeysWithTypes.bundleNo,
                      String.valueOf(articleId.getBundleNumber()),
                      languageCode));
          filter.getFilterKeys().add(filterKey);
        });

    Documents documents =
        this.getDocumentsNextpage(filterAAAClient.getFilteredDocuments(filter), filter);
    if (documents == null || CollectionUtils.isEmpty(documents.getArticleKeys()))
      return Collections.emptyMap();
    return documents.getArticleKeys().stream()
        .collect(
            Collectors.toMap(
                this::transformDocumentInArticleAttributesId,
                articleKey -> articleKey,
                (oldValue, newValue) -> oldValue));
  }

  public net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId
      transformDocumentInArticleAttributesId(String document) {
    net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId articleId =
        new net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId();
    int indexOfStartData = document.indexOf("?");
    document = document.substring(indexOfStartData + 1);
    Arrays.stream(document.split("&"))
        .forEach(
            articleAttributes -> {
              if (articleAttributes.contains("mmsartno")) {
                articleId.setArticleNumber(
                    Long.valueOf(articleAttributes.replace("mmsartno=", "")));

              } else if (articleAttributes.contains("variantno")) {
                articleId.setVariantNumber(
                    Long.valueOf(articleAttributes.replace("variantno=", "")));

              } else if (articleAttributes.contains("bundleno")) {
                articleId.setBundleNumber(Long.valueOf(articleAttributes.replace("bundleno=", "")));
              }
            });

    return articleId;
  }

  public Documents getIduItemDerivedUrlForMultipleArticles(
      String countryCode,
      List<net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId> articleIdList,
      String company) {

    if (CollectionUtils.isEmpty(articleIdList)) return null;
    List<FilterKeyIduItemDerivedStandard> filterKeyIduItemDerivedStandardList = new ArrayList<>();
    articleIdList.stream()
        .filter(
            articleId ->
                articleId.getArticleNumber() != null
                    && articleId.getVariantNumber() != null
                    && articleId.getBundleNumber() != null)
        .forEach(
            articleId -> {
              List<SubFilterKey> subFilterKeys = new ArrayList<>();
              subFilterKeys.add(
                  SubFilterKey.builder()
                      .key(KeysWithTypes.articleNumber.getCode())
                      .type(KeysWithTypes.articleNumber.getType())
                      .query(String.valueOf(articleId.getArticleNumber()))
                      .build());

              subFilterKeys.add(
                  SubFilterKey.builder()
                      .key(KeysWithTypes.variantNumber.getCode())
                      .type(KeysWithTypes.variantNumber.getType())
                      .query(String.valueOf(articleId.getVariantNumber()))
                      .build());

              subFilterKeys.add(
                  SubFilterKey.builder()
                      .key(KeysWithTypes.bundleNumber.getCode())
                      .type(KeysWithTypes.bundleNumber.getType())
                      .query(String.valueOf(articleId.getBundleNumber()))
                      .build());

              if (company != null) {
                subFilterKeys.add(
                    SubFilterKey.builder()
                        .key(KeysWithTypes.company.getCode())
                        .type(KeysWithTypes.company.getType())
                        .query(company)
                        .build());
              }
              filterKeyIduItemDerivedStandardList.add(
                  FilterKeyIduItemDerivedStandard.builder()
                      .subFilterKeys(subFilterKeys)
                      .country(countryCode)
                      .build());
            });

    FilterIduItemDerivedStandard filterIduItemDerivedStandard =
        FilterIduItemDerivedStandard.builder()
            .filterKeys(filterKeyIduItemDerivedStandardList)
            .build();

    return filterAAAClient.getFilteredDocumentsForIduItemDerivedStandard(
        filterIduItemDerivedStandard);
  }
}
