package net.metrosystems.inventoryaggregator.config.RestTemplate;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

  private final Logger logger = LoggerFactory.getLogger(CustomClientHttpRequestInterceptor.class);

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {
    // log the http request
    logger.info("URI: {}", request.getURI());
    logger.info("HTTP Method: {}", request.getMethodValue());
    logger.info("HTTP Headers: {}", request.getHeaders());

    return execution.execute(request, bytes);
  }

}
