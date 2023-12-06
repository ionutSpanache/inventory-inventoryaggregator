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
        @Value("${metrics.serviceName}") String serviceName,
        @Value("${metrics.host}") String hostName,
        @Value("${metrics.vertical}") String verticalName,
        @Value("${metrics.stage}") String stageName,
        @Value("${metrics.marcId}") String marcId) {
        return registry -> {
            registry
                .config()
                .commonTags(
                    "service", serviceName,
                    "host ", hostName,
                    "vertical", verticalName,
                    "stage", stageName,
                    "marc_id", marcId);

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

    @Bean
    public TimedAspect timedAspect(StatsdMeterRegistry registry) {
        return new TimedAspect(registry);
    }
}

