package net.metrosystems.inventoryaggregator.config.resttemplate;

import static net.metrosystems.inventoryaggregator.config.resttemplate.HttpClientConfigConstants.CONNECTION_TIMEOUT;
import static net.metrosystems.inventoryaggregator.config.resttemplate.HttpClientConfigConstants.DEFAULT_KEEP_ALIVE_TIME;
import static net.metrosystems.inventoryaggregator.config.resttemplate.HttpClientConfigConstants.MAX_LOCALHOST_CONNECTIONS;
import static net.metrosystems.inventoryaggregator.config.resttemplate.HttpClientConfigConstants.MAX_ROUTE_CONNECTIONS;
import static net.metrosystems.inventoryaggregator.config.resttemplate.HttpClientConfigConstants.MAX_TOTAL_CONNECTIONS;
import static net.metrosystems.inventoryaggregator.config.resttemplate.HttpClientConfigConstants.REQUEST_TIMEOUT;
import static net.metrosystems.inventoryaggregator.config.resttemplate.HttpClientConfigConstants.SOCKET_TIMEOUT;

import java.util.Iterator;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HeaderElements;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.message.MessageSupport;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class ApacheHttpClientConfig {


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
      final Iterator<HeaderElement> it = MessageSupport.iterate(httpResponse, HeaderElements.KEEP_ALIVE);
      while (it.hasNext()){
        final HeaderElement he = it.next();
        final String param = he.getName();
        final String value = he.getValue();
        if (value != null && param.equalsIgnoreCase("timeout")) {
          try {
            return TimeValue.ofSeconds(Long.parseLong(value));
          } catch (final NumberFormatException ignore) {
          }
        }
      }

      return TimeValue.ofMilliseconds(DEFAULT_KEEP_ALIVE_TIME);
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
            .setConnectTimeout(Timeout.ofMilliseconds(CONNECTION_TIMEOUT))
            .setConnectionRequestTimeout(Timeout.ofMilliseconds(REQUEST_TIMEOUT))
            .setResponseTimeout(Timeout.ofMilliseconds(SOCKET_TIMEOUT))
            .build();

    return HttpClients.custom()
        .setDefaultRequestConfig(requestConfig)
        .setConnectionManager(poolingConnectionManager())
        .setKeepAliveStrategy(connectionKeepAliveStrategy())
        .setRetryStrategy(new DefaultHttpRequestRetryStrategy(3, TimeValue.ofSeconds(3)))
        .build();
  }
}
