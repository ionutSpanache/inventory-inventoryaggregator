package inventory.aggregator.articleId.service;


import inventory.aggregator.articleId.client.ArticleIdClient;
import inventory.aggregator.articleId.model.ArticleId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ArticleIdService {

    @Autowired
    private ArticleIdClient articleIdClient;

    public List<ArticleId> getArticleIdByBarcode(String countryCode, String salesLine, String languageCode, Integer storeNo, String barCode) throws Exception {
        return articleIdClient.getArticleIdByBarcode(countryCode, salesLine, languageCode, storeNo, barCode);
    }

    public List<ArticleId> getArticleIdByArticleNumber(String countryCode, String salesLine, String languageCode, Integer storeNo, Long articleNumber) throws Exception {
        return articleIdClient.getArticleIdByArticleNumber(countryCode, salesLine, languageCode, storeNo, articleNumber);
    }


    public List<ArticleId> getArticleIdBySubsysArticleNumber(String countryCode, String salesLine, String languageCode, Integer storeNo, Long subsysArticleNumber) throws Exception {
        return articleIdClient.getArticleIdBySubsysArticleNumber(countryCode, salesLine, languageCode, storeNo, subsysArticleNumber);
    }
}
