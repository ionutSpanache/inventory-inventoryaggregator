package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.service;

import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.config.ConfigArticleCacheAAA;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.service.DocumentsService;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.client.FilterAAAClient;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class FilterAAAService {

    @Autowired
    ConfigArticleCacheAAA configArticleCacheAAA;
    @Autowired
    FilterAAAClient filterAAAClient;
    @Autowired
    DocumentsService documentsService;

    public SubFilterKey addSubFilterKey(KeysWithTypes key, String value, String languageCode) {
        SubFilterKey subFilterKey = new SubFilterKey();
        if(StringUtils.isEmpty(languageCode))
            subFilterKey.setLanguage(languageCode.toLowerCase());
        subFilterKey.setKey(key.getCode());
        subFilterKey.setType(key.getType());
        subFilterKey.setQuery(value);
        if (!key.getType().equals("string")) {
            List<InListOfRange> listOfRanges = new ArrayList<>();
            InListOfRange inListOfRange = new InListOfRange(Long.valueOf(value), Long.valueOf(value));
            listOfRanges.add(inListOfRange);
            subFilterKey.setInListOfRange(listOfRanges);
        }
        return subFilterKey;
    }

    public Filter initializeFilter(String countryCode, String salesLine) {
        Filter filter = new Filter();
        List<FilterKey> filterKeyList = new ArrayList<>();
        filter.setFilterKeys(filterKeyList);
        FilterKey filterKey = new FilterKey();
        List<SubFilterKey> subFilterKeys = new ArrayList<>();
        filterKey.setSubFilterKeys(subFilterKeys);
        filterKey.setCountry(countryCode.toLowerCase());
        filterKey.setSalesLine(salesLine.toLowerCase());
        filterKeyList.add(filterKey);
        return filter;
    }

    public FilterSupplierInformation initializeSupplierInformationFilter(String countryCode, String salesLine, Long supplierNumber) {
        FilterSupplierInformation filter = new FilterSupplierInformation();
        List<FilterKeySupplierInformation> filterKeyList = new ArrayList<>();
        filter.setFilterKeys(filterKeyList);
        FilterKeySupplierInformation filterKey = new FilterKeySupplierInformation();
        filterKey.setHardKeyDocument("supplier-information");
        filterKey.setCountry(countryCode.toLowerCase());
        filterKey.setSalesLine(salesLine.toLowerCase());
        filterKey.setSupplierNumber(supplierNumber);
        filterKeyList.add(filterKey);
        return filter;
    }

    public List<ArticleId> getArticleIdByBarcode(String countryCode, String salesLine, String languageCode, Integer storeNo, String barCode) throws Exception {
        return documentsService.
                transformDocumentListInArticleIdList(
                        documentsService.
                                getDocumentsByBarcode(countryCode, salesLine, languageCode, barCode), storeNo);
    }

    public List<ArticleId> getArticleIdByArticleNumber(String countryCode, String salesLine, Integer storeNo, Long articleNumber) throws Exception {
        return documentsService.
                transformDocumentListInArticleIdList(
                        documentsService.
                                getDocumentsByArticleNumber(countryCode, salesLine, articleNumber), storeNo);
    }

    public List<ArticleId> getArticleIdBySubsysArticleNumber(String countryCode, String salesLine, String languageCode, Integer storeNo, Long subsysArticleNumber) throws Exception {
        return documentsService.
                transformDocumentListInArticleIdList(
                        documentsService.
                                getDocumentsBySubsysArticleNumber(countryCode, salesLine, languageCode, subsysArticleNumber), storeNo);
    }

    public FilterKey initializeFilterKey(String countryCode, String salesLine) {
        FilterKey filterKey = new FilterKey();
        filterKey.setSubFilterKeys(new ArrayList<>());
        filterKey.setCountry(countryCode.toLowerCase());
        filterKey.setSalesLine(salesLine.toLowerCase());
        return filterKey;
    }

    public FilterKey initializeFilterKey(String countryCode) {
        FilterKey filterKey = new FilterKey();
        filterKey.setSubFilterKeys(new ArrayList<>());
        filterKey.setCountry(countryCode.toLowerCase());
        return filterKey;
    }
}
