package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.service;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
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

  @Autowired FilterAAAClient filterAAAClient;
  @Autowired @Lazy // TODO: resolve circular dependency
  FilterAAAService filterAAAService;
  @Autowired @Lazy // TODO: resolve circular dependency
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
          filterAAAClient.getFilteredDocumentsNextPage(
              filter, escapeHtml4(documents.getNextPageToken()));
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


  public Documents getDocumentsByArticleNumber(
      String countryCode, String salesLine, Long articleNumber) throws Exception {

    ArticleFilterKey artFilterKey = new ArticleFilterKey();
    ArticleHardKey artHardKey =
        new ArticleHardKey(countryCode, salesLine, String.valueOf(articleNumber), null, null);
    artFilterKey.getSubFilterKeys().add(artHardKey);

    Documents documents = filterAAAClient.getFilteredDocuments(artFilterKey);
    return getDocumentsNextpage(documents, artFilterKey);
  }

  public Documents getDocumentsByArticleNumberVarianBundleId(
      String countryCode, String salesLine, Long articleNumber, Long variantNr, Long bundleId)
      throws Exception {

    ArticleFilterKey artFilterKey = new ArticleFilterKey();
    ArticleHardKey artHardKey =
        new ArticleHardKey(
            countryCode,
            salesLine,
            String.valueOf(articleNumber),
            String.valueOf(variantNr),
            String.valueOf(bundleId));
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
