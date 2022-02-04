package net.metrosystems.inventoryaggregator.config.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class RestTemplateMovieConfig {

    private HttpRequestRetryHandler retryHandler;

    @Autowired
    public RestTemplateMovieConfig(HttpRequestRetryHandler retryHandler) {
        this.retryHandler = retryHandler;
    }

    @Bean(name = "restTemplateMovie")
    public RestTemplate restTemplate(@Qualifier("httpClientRequestFactory") ClientHttpRequestFactory clientFactory) {
        return new RestTemplate(clientFactory);
    }


    @Bean(name = "httpClientRequestFactory")
    public ClientHttpRequestFactory clientFactory(@Qualifier("httpClientWithRetry") HttpClient httpClient) {

        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
//        httpComponentsClientHttpRequestFactory.setConnectTimeout(5000);
//        httpComponentsClientHttpRequestFactory.setReadTimeout(5000);
        return httpComponentsClientHttpRequestFactory;
    }

    @Bean("httpClientWithRetry")
    public HttpClient httpClient() {
        return HttpClients.custom()
                .setRetryHandler(retryHandler)
                .build();
    }
}
