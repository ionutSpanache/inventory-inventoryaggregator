package net.metrosystems.inventoryaggregator.config.multithread;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfiguration {

  @Bean(name = "asyncExecutor")
  public Executor asyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(100);
    executor.setKeepAliveSeconds(3600);
    executor.setThreadNamePrefix("async-");
    executor.initialize();
    return executor;
  }

  private static class CustomForkJoinWorkerThread extends ForkJoinWorkerThread {

    CustomForkJoinWorkerThread(ForkJoinPool pool) {
      super(pool);
      setContextClassLoader(Thread.currentThread().getContextClassLoader());
    }
  }

  @Bean(name = "ForkJoinCustomPool")
  @Scope("singleton")
  public ForkJoinPool createForkJoinPool() {
    return new ForkJoinPool(100, CustomForkJoinWorkerThread::new, null, false);
  }

}
