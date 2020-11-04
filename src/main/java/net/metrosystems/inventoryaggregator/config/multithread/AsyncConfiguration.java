package net.metrosystems.inventoryaggregator.config.multithread;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfiguration {

  @Bean(name = "asyncExecutor")
  public Executor asyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(500);
    executor.setKeepAliveSeconds(3600);
    executor.setThreadNamePrefix("async-");
    executor.initialize();
    return executor;
  }

}
