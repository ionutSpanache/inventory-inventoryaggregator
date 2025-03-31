package net.metrosystems.inventoryaggregator.config.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpRequestRetryHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.SocketException;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class HttpClientRetryHandlerConfig {

  @Value("${rest.template.retry.count}")
  private int retryCount;

  @Value("${rest.template.time.to.wait.between.retry}")
  private int timeToWaitBetweenRetry;

  @Bean
  public HttpRequestRetryHandler retryHandler() {
    return (exception, executionCount, context) -> {
      if (executionCount > retryCount) {
        log.warn("Maximum retries {} reached", retryCount);
        return false;
      }

      if (exception instanceof SocketException) {
        return false;
      }

      if (exception instanceof Exception) {
        log.warn("Retry {}", executionCount);
        try {
          TimeUnit.SECONDS.sleep(timeToWaitBetweenRetry);
        } catch (InterruptedException e) {
          log.warn("Interrupted while waiting for retry {}", executionCount, e);
        }
        return true;
      }
      return false;
    };
  }
}
