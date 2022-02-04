package net.metrosystems.inventoryaggregator.config.RestTemplate;

import org.apache.http.HttpHeaders;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  private final CloseableHttpClient httpClient;

  @Value("${ARTICLE_CACHE_USERNAME}")
  private String AC_USERNAME;

  @Value("${ARTICLE_CACHE_PASSWORD}")
  private String AC_PASSWORD;

  @Autowired
  public RestTemplateConfig(CloseableHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @Bean
  public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
    clientHttpRequestFactory.setHttpClient(httpClient);
//    clientHttpRequestFactory.setConnectTimeout(5000);
//    clientHttpRequestFactory.setReadTimeout(5000);
    return clientHttpRequestFactory;
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplateBuilder().requestFactory(this::clientHttpRequestFactory)
                                    .errorHandler(new CustomClientErrorHandler()).build();
  }

  @Bean
  @Qualifier("RestTemplate-ArticleCache")
  public RestTemplate restTemplateForAAA() {
    return new RestTemplateBuilder().requestFactory(this::clientHttpRequestFactory)
                                    .basicAuthentication(AC_USERNAME, AC_PASSWORD)
                                    .defaultHeader(HttpHeaders.ACCEPT_ENCODING, "gzip").build();
  }

}
