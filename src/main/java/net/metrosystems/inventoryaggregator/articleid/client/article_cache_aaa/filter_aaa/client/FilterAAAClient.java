package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.config.ConfigArticleCacheAAA;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model.*;
import net.metrosystems.inventoryaggregator.config.ObjectMapperCustom;
import net.metrosystems.inventoryaggregator.config.RestTemplate.RestTemplateCustom;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilterAAAClient {

  private final ConfigArticleCacheAAA configArticleCacheAAA;
  private final RestTemplateCustom restTemplateCustom;

  public <T> Documents getFilteredDocuments(T filter) throws Exception {
    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .uri(configArticleCacheAAA.getHost())
            .path(configArticleCacheAAA.getGeneral())
            .query(configArticleCacheAAA.getGeneralQueryParam())
            .buildAndExpand()
            .encode();
    // todo add suport for multi page response.
    String filterAsString;
    try {
      filterAsString = ObjectMapperCustom.getMapper().writeValueAsString(filter);
    } catch (JsonProcessingException e) {
      throw new Exception(e);
    }
    Documents documents =
        (Documents)
            restTemplateCustom.postForObject(
                uriComponents.toUri().toString(), filterAsString, Documents.class, true);
    if (documents != null && !CollectionUtils.isEmpty(documents.getArticleKeys()))
      documents
          .getArticleKeys()
          .removeIf(
              articleKey ->
                  articleKey.toLowerCase().contains("environment=dev")
                      || articleKey.toLowerCase().contains("environment=qa"));
    return documents;
  }

  public Documents getFilteredDocumentsForIsuMaster(FilterIsuMaster filterIsuMaster) {
    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .uri(configArticleCacheAAA.getHost())
            .path(configArticleCacheAAA.getGeneral())
            .query(configArticleCacheAAA.getGeneralQueryParam())
            .buildAndExpand()
            .encode();
    // todo add suport for multi page response.
    String filterAsString = null;
    try {
      filterAsString = ObjectMapperCustom.getMapper().writeValueAsString(filterIsuMaster);
    } catch (JsonProcessingException e) {
      log.error("ISU_ITEM_MASTER error when receiving data");
    }
    Documents documents =
        (Documents)
            restTemplateCustom.postForObject(
                uriComponents.toUri().toString(), filterAsString, Documents.class, true);
    if (documents != null && !CollectionUtils.isEmpty(documents.getArticleKeys()))
      documents
          .getArticleKeys()
          .removeIf(
              articleKey ->
                  articleKey.toLowerCase().contains("environment=dev")
                      || articleKey.toLowerCase().contains("environment=qa"));
    return documents;
  }

  public Documents getFilteredDocumentsForIsuStoreMerchandise(
      FilterIsuItemStoreMerchandise filterIsuItemStoreMerchandise) {
    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .uri(configArticleCacheAAA.getHost())
            .path(configArticleCacheAAA.getGeneral())
            .query(configArticleCacheAAA.getGeneralQueryParam())
            .buildAndExpand()
            .encode();
    // todo add suport for multi page response.
    String filterAsString = null;
    try {
      filterAsString =
          ObjectMapperCustom.getMapper().writeValueAsString(filterIsuItemStoreMerchandise);
    } catch (JsonProcessingException e) {
      log.error("ISU_ITEM_MASTER error when receiving data");
    }
    Documents documents =
        (Documents)
            restTemplateCustom.postForObject(
                uriComponents.toUri().toString(), filterAsString, Documents.class, true);
    if (documents != null && !CollectionUtils.isEmpty(documents.getArticleKeys()))
      documents
          .getArticleKeys()
          .removeIf(
              articleKey ->
                  articleKey.toLowerCase().contains("environment=dev")
                      || articleKey.toLowerCase().contains("environment=qa"));
    return documents;
  }

  public Documents getFilteredDocumentsForIduItemDerivedStandard(
      FilterIduItemDerivedStandard filterIduItemDerivedStandard) {
    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .uri(configArticleCacheAAA.getHost())
            .path(configArticleCacheAAA.getGeneral())
            .query(configArticleCacheAAA.getGeneralQueryParam())
            .buildAndExpand()
            .encode();
    // todo add suport for multi page response.
    String filterAsString = null;
    try {
      filterAsString =
          ObjectMapperCustom.getMapper().writeValueAsString(filterIduItemDerivedStandard);
    } catch (JsonProcessingException e) {
      log.error("ISU_ITEM_MASTER error when receiving data");
    }
    Documents documents =
        (Documents)
            restTemplateCustom.postForObject(
                uriComponents.toUri().toString(), filterAsString, Documents.class, true);
    if (documents != null && !CollectionUtils.isEmpty(documents.getArticleKeys()))
      documents
          .getArticleKeys()
          .removeIf(
              articleKey ->
                  articleKey.toLowerCase().contains("environment=dev")
                      || articleKey.toLowerCase().contains("environment=qa"));
    return documents;
  }

  public Documents getFilteredDocuments(FilterSupplierInformation filter) throws Exception {
    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .uri(configArticleCacheAAA.getHost())
            .path(configArticleCacheAAA.getGeneral())
            .query(configArticleCacheAAA.getGeneralQueryParam())
            .buildAndExpand()
            .encode();
    // todo add suport for multi page response.
    String filterAsString;
    try {
      filterAsString = ObjectMapperCustom.getMapper().writeValueAsString(filter);
    } catch (JsonProcessingException e) {
      throw new Exception(e);
    }
    return (Documents)
        restTemplateCustom.postForObject(
            uriComponents.toUri().toString(), filterAsString, Documents.class, true);
  }

  public <T> Documents getFilteredDocumentsNextPage(T filter, String nextPage) throws Exception {
    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .uri(configArticleCacheAAA.getHost())
            .path(configArticleCacheAAA.getGeneral())
            .query(configArticleCacheAAA.getGeneralQueryParam() + configArticleCacheAAA.getFrom())
            .buildAndExpand(nextPage)
            .encode();
    String filterAsString;
    try {
      filterAsString = ObjectMapperCustom.getMapper().writeValueAsString(filter);
    } catch (JsonProcessingException e) {
      throw new Exception(e);
    }
    return (Documents)
        restTemplateCustom.postForObject(
            uriComponents.toUri().toString(), filterAsString, Documents.class, true);
  }
}
