package net.metrosystems.inventoryaggregator.articleid.service;


import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleid.client.ArticleIdClient;
import net.metrosystems.inventoryaggregator.articleid.model.ArticleId;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ArticleIdService {

    @Autowired
    private ArticleIdClient articleIdClient;

    @Autowired
    private ArticleCacheService articleCacheService;

    public List<ArticleId> getArticleIdByBarcode(String countryCode, String salesLine, String languageCode, Integer storeNo, String barCode) throws Exception {
        return articleIdClient.getArticleIdByBarcode(countryCode, salesLine, languageCode, storeNo, barCode);
    }

    public List<ArticleId> getArticleIdByArticleNumber(String countryCode, String salesLine, String languageCode, Integer storeNo, Long articleNumber) throws Exception {
        return articleIdClient.getArticleIdByArticleNumber(countryCode, salesLine, languageCode, storeNo, articleNumber);
    }


    public List<ArticleId> getArticleIdBySubsysArticleNumber(String countryCode, String salesLine, String languageCode, Integer storeNo, Long subsysArticleNumber) throws Exception {
        return articleIdClient.getArticleIdBySubsysArticleNumber(countryCode, salesLine, languageCode, storeNo, subsysArticleNumber);
    }

  public List<Integer> getActiveStoresByByArtVarBundle(
      String countryCode, String salesLine, String languageCode, ArticleId articleId)
      throws Exception {
    return articleCacheService.getActiveStoresByByArtVarBundle(
        countryCode, salesLine, languageCode, articleId);
  }
}
