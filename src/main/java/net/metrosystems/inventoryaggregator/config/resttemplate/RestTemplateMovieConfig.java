package net.metrosystems.inventoryaggregator.config.resttemplate;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class RestTemplateMovieConfig {


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
            .setRetryStrategy(new DefaultHttpRequestRetryStrategy(3, TimeValue.ofSeconds(3)))
                .build();
    }
}
