package net.metrosystems.inventoryaggregator.config.resttemplate;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  private final CloseableHttpClient httpClient;

  @Autowired
  public RestTemplateConfig(CloseableHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @Bean
  public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
        new HttpComponentsClientHttpRequestFactory();
    clientHttpRequestFactory.setHttpClient(httpClient);
    //    clientHttpRequestFactory.setConnectTimeout(5000);
    //    clientHttpRequestFactory.setReadTimeout(5000);
    return clientHttpRequestFactory;
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplateBuilder()
        .requestFactory(this::clientHttpRequestFactory)
        .errorHandler(new CustomClientErrorHandler())
        .build();
  }
}
