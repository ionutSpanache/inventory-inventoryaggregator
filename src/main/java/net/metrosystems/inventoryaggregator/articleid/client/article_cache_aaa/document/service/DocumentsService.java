package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.service;

import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.client.FilterAAAClient;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model.Filter;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model.KeysWithTypes;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model.SubFilterKey;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.service.FilterAAAService;
import net.metrosystems.inventoryaggregator.articleid.model.ArticleId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@Service
public class DocumentsService {

    @Autowired
    FilterAAAClient filterAAAClient;
    @Autowired
    FilterAAAService filterAAAService;


    public ArticleId tranformDocumentInArticleId(String document) {
        ArticleId articleId = new ArticleId();
        int indexOfStartData = document.indexOf("?");
        document = document.substring(indexOfStartData + 1);
        Arrays.stream(document.split("&")).forEach(
                articleAtributes -> {
                    if (articleAtributes.contains("mmsartno")) {
                        articleId.setArticleNumber(Long.valueOf(articleAtributes.replace("mmsartno=", "")));

                    } else if (articleAtributes.contains("variantno")) {
                        articleId.setVariantNumber(Long.valueOf(articleAtributes.replace("variantno=", "")));

                    } else if (articleAtributes.contains("bundleno")) {
                        articleId.setBundleNumber(Long.valueOf(articleAtributes.replace("bundleno=", "")));
                    }
                }
        );
        return articleId;
    }


    public List<ArticleId> transformDocumentListInArticleIdList(Documents documents) {

        List<ArticleId> articleIdList = new ArrayList<>();
        documents.getArticleKeys().stream().forEach(document -> {
            ArticleId articleId = this.tranformDocumentInArticleId(document);
            articleIdList.add(articleId);
        });
        return articleIdList;
    }

    public Documents getDocumentsNextpage(Documents documents, Filter filter) throws Exception {

        while (documents != null && documents.getNextPageToken() != null) {
            Documents documentsNextPage = filterAAAClient.getFilteredDocumentsNextPage(filter, documents.getNextPageToken());
            if(documentsNextPage != null) {
                documents.getArticleKeys().addAll((Collection<? extends String>) documentsNextPage.getArticleKeys());
                documents.setNextPageToken(documentsNextPage.getNextPageToken());
            }
        }
        return documents;
    }

    public Documents getDocumentsByBarcode(String countryCode, String salesLine, String languageCode, String barCode) throws Exception {
        Filter filter = filterAAAService.initializeFilter(countryCode, salesLine);
        SubFilterKey subFilterKey = filterAAAService.addSubFilterKey(KeysWithTypes.salesBarcode, barCode, languageCode);
        filter.getFilterKeys().get(0).getSubFilterKeys().add(subFilterKey);
        return filterAAAClient.getFilteredDocuments(filter);
    }

    public Documents getDocumentsByArticleNumber(String countryCode, String salesLine, String languageCode, Long articleNumber) throws Exception {
        Filter filter = filterAAAService.initializeFilter(countryCode, salesLine);
        SubFilterKey subFilterKey = filterAAAService.addSubFilterKey(KeysWithTypes.mmsArtNo, String.valueOf(articleNumber), languageCode);
        filter.getFilterKeys().get(0).getSubFilterKeys().add(subFilterKey);
        Documents documents = filterAAAClient.getFilteredDocuments(filter);
        return this.getDocumentsNextpage(documents, filter);
    }

    public Documents getDocumentsByArticleNumberVarianBundleId(String countryCode, String salesLine, String languageCode, Long articleNumber, Long variantNr, Long bundleId) throws Exception {
        Filter filter = filterAAAService.initializeFilter(countryCode, salesLine);
        SubFilterKey subFilterKeyArtNo = filterAAAService.addSubFilterKey(KeysWithTypes.mmsArtNo, String.valueOf(articleNumber), languageCode);
        filter.getFilterKeys().get(0).getSubFilterKeys().add(subFilterKeyArtNo);
        SubFilterKey subFilterKeyVariantNr = filterAAAService.addSubFilterKey(KeysWithTypes.variantNo, String.valueOf(variantNr), languageCode);
        filter.getFilterKeys().get(0).getSubFilterKeys().add(subFilterKeyVariantNr);
        SubFilterKey subFilterKeyBundleId = filterAAAService.addSubFilterKey(KeysWithTypes.bundleNo, String.valueOf(bundleId), languageCode);
        filter.getFilterKeys().get(0).getSubFilterKeys().add(subFilterKeyBundleId);
        Documents documents = filterAAAClient.getFilteredDocuments(filter);
        return this.getDocumentsNextpage(documents, filter);
    }

    public Documents getDocumentsBySubsysArticleNumber(String countryCode, String salesLine, String languageCode, Long subsysArticleNumber) throws Exception {
        Filter filter = filterAAAService.initializeFilter(countryCode, salesLine);
        SubFilterKey subFilterKey = filterAAAService.addSubFilterKey(KeysWithTypes.subsysNo, String.valueOf(subsysArticleNumber), languageCode);
        filter.getFilterKeys().get(0).getSubFilterKeys().add(subFilterKey);
        Documents documents = filterAAAClient.getFilteredDocuments(filter);
        return this.getDocumentsNextpage(documents, filter);
    }






}
