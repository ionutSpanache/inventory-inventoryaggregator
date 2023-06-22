package net.metrosystems.inventoryaggregator.articleid.client;

import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.service.FilterAAAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArticleIdClient {
    @Autowired
    FilterAAAService filterAAAService;

    public List<ArticleId> getArticleIdByBarcode(String countryCode, String salesLine, String languageCode, Integer storeNo, String barCode) throws Exception {
        return filterAAAService.getArticleIdByBarcode(countryCode, salesLine, languageCode, storeNo, barCode);

    }

    public List<ArticleId> getArticleIdByArticleNumber(String countryCode, String salesLine, Integer storeNo, Long articleNumber) throws Exception {
        return filterAAAService.getArticleIdByArticleNumber(countryCode, salesLine, storeNo, articleNumber);

    }

    
    public List<ArticleId> getArticleIdBySubsysArticleNumber(String countryCode, String salesLine, String languageCode, Integer storeNo, Long subsysArticleNumber) throws Exception {
        return filterAAAService.getArticleIdBySubsysArticleNumber(countryCode, salesLine, languageCode, storeNo, subsysArticleNumber);
    }
}
