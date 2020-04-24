package net.metrosystems.inventoryaggregator.config.RestTemplate;

import net.metrosystems.inventoryaggregator.articleId.client.article_cache_aaa.config.ConfigArticleCacheAAA;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Order(2)
public class BasicAuthInterceptor implements ClientHttpRequestInterceptor {

    private ConfigArticleCacheAAA configArticleCacheAAA;


    public BasicAuthInterceptor(ConfigArticleCacheAAA configArticleCacheAAA) {
        this.configArticleCacheAAA = configArticleCacheAAA;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {
        HTTPHeaderConfig httpHeaderConfig = new HTTPHeaderConfig();
        if (request.getURI().toString().contains("articlesearch.service-phalanx")) {
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, httpHeaderConfig.addBasicAuth(configArticleCacheAAA.getUsername(), configArticleCacheAAA.getPassword())
            );

        }

        System.out.println("TraceInformationRestTemplateAgentInterceptor.intercept:" + request.getURI().toURL().toString());
        return execution.execute(request, body);
    }
}
