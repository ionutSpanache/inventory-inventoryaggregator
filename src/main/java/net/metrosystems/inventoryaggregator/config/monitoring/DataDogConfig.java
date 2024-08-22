package net.metrosystems.inventoryaggregator.config.monitoring;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataDogConfig {

  @Bean
  MeterRegistryCustomizer<StatsdMeterRegistry> registryCustomizer(
      @Value("${metrics.marc.id}") String marcId,
      @Value("${metrics.solution}") String solution,
      @Value("${metrics.solution.id}") String solutionId,
      @Value("${metrics.vertical}") String vertical,
      @Value("${metrics.project.id}") String projectId,
      @Value("${metrics.env}") String env,
      @Value("${metrics.service}") String service,
      @Value("${metrics.version}") String version,
      @Value("${metrics.host}") String host) {
    return registry -> {
      registry
          .config()
          .commonTags(
              "marc_id",
              marcId,
              "solution",
              solution,
              "solution_id",
              solutionId,
              "vertical",
              vertical,
              "project_id",
              projectId,
              "env",
              env,
              "service",
              service,
              "version",
              version,
              "host",
              host);

      // JVM Metrics
      new ClassLoaderMetrics().bindTo(registry);
      new JvmMemoryMetrics().bindTo(registry);
      new JvmThreadMetrics().bindTo(registry);
      new JvmGcMetrics().bindTo(registry);
      // System metrics
      new ProcessorMetrics().bindTo(registry);
      // Logging metrics
      new LogbackMetrics().bindTo(registry);
    };
  }
}
