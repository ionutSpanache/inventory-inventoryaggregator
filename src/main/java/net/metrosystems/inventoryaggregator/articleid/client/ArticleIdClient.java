package net.metrosystems.inventoryaggregator.articleid.client;

import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.service.FilterAAAService;
import net.metrosystems.inventoryaggregator.articleid.model.ArticleId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArticleIdClient {
    @Autowired
    FilterAAAService filterAAAService;

    public List<ArticleId> getArticleIdByBarcode(String countryCode, String salesLine, String languageCode, String barCode) throws Exception {
        return filterAAAService.getArticleIdByBarcode(countryCode, salesLine, languageCode, barCode);

    }

    public List<ArticleId> getArticleIdByArticleNumber(String countryCode, String salesLine, String languageCode, Long articleNumber) throws Exception {
        return filterAAAService.getArticleIdByArticleNumber(countryCode, salesLine, languageCode, articleNumber);

    }

    
    public List<ArticleId> getArticleIdBySubsysArticleNumber(String countryCode, String salesLine, String languageCode, Long subsysArticleNumber) throws Exception {
        return filterAAAService.getArticleIdBySubsysArticleNumber(countryCode, salesLine, languageCode, subsysArticleNumber);
    }
}
