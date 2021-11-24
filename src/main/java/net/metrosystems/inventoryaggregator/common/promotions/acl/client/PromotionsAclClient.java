package net.metrosystems.inventoryaggregator.common.promotions.acl.client;

import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleid.model.ArticleId;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class PromotionsAclClient {

    private WebClient webClient;

    @Value("${ACL_PROMOTIONS_SERVICE_HOST}")
    private String aclPromotionsHost;

    @Value("${ACL_ACTIVE_PROMOTION_PATH}")
    private String aclActivePromotionPath;

    @Autowired
    public  PromotionsAclClient(@Qualifier("webClientForAcl") WebClient webClient) {
        this.webClient = webClient;
    }


    public Boolean getActivePromotion(
            StoreId storeId, ArticleId articleId, String promotionId) {
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(
                aclPromotionsHost + aclActivePromotionPath)
                .buildAndExpand(
                        storeId.getCountryCode().toUpperCase(),
                        storeId.getSalesLine().toUpperCase(),
                        storeId.getStoreNumber(),
                        promotionId,
                        articleId.getArticleNumber(),
                        articleId.getVariantNumber(),
                        articleId.getBundleNumber());
        log.debug("uriActivePromotion=" + uriComponents.toString());

        try {
            return webClient.get()
                    .uri(uriComponents.toUriString())
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

        } catch (Exception e) {
            return false;
        }
    }

}
