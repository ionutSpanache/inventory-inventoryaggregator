package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.config.ConfigArticleCacheAAA;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model.Filter;
import net.metrosystems.inventoryaggregator.config.ObjectMapperCustom;
import net.metrosystems.inventoryaggregator.config.RestTemplate.RestTemplateCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class FilterAAAClient {


    @Autowired
    ConfigArticleCacheAAA configArticleCacheAAA;
    @Autowired
    private RestTemplateCustom restTemplateCustom;

    public Documents getFilteredDocuments(Filter filter) throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .uri(configArticleCacheAAA.getHost())
                .path(configArticleCacheAAA.getGeneral())
                .query(configArticleCacheAAA.getGeneralQueryParam())
                .buildAndExpand()
                .encode();
        System.out.println("FilterAAAClient.getFilteredDocuments uriComponents:" + uriComponents.toUri().toString());
        //todo add suport for multi page response.
        String filterAsString;
        try {
            filterAsString = ObjectMapperCustom.getMapper().writeValueAsString(filter);
        } catch (JsonProcessingException e) {
            throw new Exception(e);
        }
        System.out.println("FilterAAAClient.getFilteredDocuments filterAsString");
        return (Documents) restTemplateCustom.postForObject(uriComponents.toUri().toString(), filterAsString, Documents.class, true);
    }

    public Documents getFilteredDocumentsNextPage(Filter filter, String nextPage) throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .uri(configArticleCacheAAA.getHost())
                .path(configArticleCacheAAA.getGeneral())
                .query(configArticleCacheAAA.getGeneralQueryParam() + configArticleCacheAAA.getFrom())
                .buildAndExpand(nextPage)
                .encode();
        System.out.println("FilterAAAClient.getFilteredDocuments uriComponents:" + uriComponents.toUri().toString());
        String filterAsString;
        try {
            filterAsString = ObjectMapperCustom.getMapper().writeValueAsString(filter);
        } catch (JsonProcessingException e) {
            throw new Exception(e);
        }
        return (Documents) restTemplateCustom.postForObject(uriComponents.toUri().toString(), filterAsString, Documents.class, true);
    }
}
