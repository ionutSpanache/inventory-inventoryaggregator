package net.metrosystems.inventoryaggregator.config.RestTemplate;

import static net.metrosystems.inventoryaggregator.config.RestTemplate.HttpClientConfigConstants.CONNECTION_TIMEOUT;
import static net.metrosystems.inventoryaggregator.config.RestTemplate.HttpClientConfigConstants.DEFAULT_KEEP_ALIVE_TIME;
import static net.metrosystems.inventoryaggregator.config.RestTemplate.HttpClientConfigConstants.IDLE_CONNECTION_WAIT_TIME;
import static net.metrosystems.inventoryaggregator.config.RestTemplate.HttpClientConfigConstants.MAX_LOCALHOST_CONNECTIONS;
import static net.metrosystems.inventoryaggregator.config.RestTemplate.HttpClientConfigConstants.MAX_ROUTE_CONNECTIONS;
import static net.metrosystems.inventoryaggregator.config.RestTemplate.HttpClientConfigConstants.MAX_TOTAL_CONNECTIONS;
import static net.metrosystems.inventoryaggregator.config.RestTemplate.HttpClientConfigConstants.REQUEST_TIMEOUT;
import static net.metrosystems.inventoryaggregator.config.RestTemplate.HttpClientConfigConstants.SOCKET_TIMEOUT;

import java.util.concurrent.TimeUnit;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class ApacheHttpClientConfig {

  private HttpRequestRetryHandler retryHandler;

  @Autowired
  public ApacheHttpClientConfig(HttpRequestRetryHandler retryHandler) {
    this.retryHandler = retryHandler;
  }

  @Bean
  public PoolingHttpClientConnectionManager poolingConnectionManager() {
    PoolingHttpClientConnectionManager poolingConnectionManager =
        new PoolingHttpClientConnectionManager();

    // set total amount of connections across all HTTP routes
    poolingConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);

    // set maximum amount of connections for each http route in pool
    poolingConnectionManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);

    // increase the amounts of connections if host is localhost
    HttpHost localhost = new HttpHost("http://localhost", 8080);
    poolingConnectionManager.setMaxPerRoute(new HttpRoute(localhost), MAX_LOCALHOST_CONNECTIONS);

    return poolingConnectionManager;
  }

  @Bean
  public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
    return (httpResponse, httpContext) -> {
      HeaderIterator headerIterator = httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE);
      HeaderElementIterator elementIterator = new BasicHeaderElementIterator(headerIterator);

      while (elementIterator.hasNext()) {
        HeaderElement element = elementIterator.nextElement();
        String param = element.getName();
        String value = element.getValue();
        if (value != null && param.equalsIgnoreCase("timeout")) {
          return Long.parseLong(value) * 1000; // convert to ms
        }
      }

      return DEFAULT_KEEP_ALIVE_TIME;
    };
  }

  @Bean
  public Runnable idleConnectionMonitor(PoolingHttpClientConnectionManager pool) {
    return new Runnable() {
      @Override
      @Scheduled(fixedDelay = 20000)
      public void run() {
        // only if connection pool is initialised
        if (pool != null) {
          pool.closeExpiredConnections();
          pool.closeIdleConnections(IDLE_CONNECTION_WAIT_TIME, TimeUnit.MILLISECONDS);
        }
      }
    };
  }

  @Bean
  @Qualifier("taskScheduleIdle")
  public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setThreadNamePrefix("idleMonitor-");
    scheduler.setPoolSize(5);
    return scheduler;
  }

  @Bean
  public CloseableHttpClient httpClient() {
    RequestConfig requestConfig =
        RequestConfig.custom()
            .setConnectTimeout(CONNECTION_TIMEOUT)
            .setConnectionRequestTimeout(REQUEST_TIMEOUT)
            .setSocketTimeout(SOCKET_TIMEOUT)
            .build();

    return HttpClients.custom()
        .setDefaultRequestConfig(requestConfig)
        .setConnectionManager(poolingConnectionManager())
        .setKeepAliveStrategy(connectionKeepAliveStrategy())
        .setRetryHandler(retryHandler)
        .build();
  }
}
