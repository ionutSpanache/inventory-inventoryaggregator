package net.metrosystems.inventoryaggregator.config.monitoring.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.List;
import java.util.Optional;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MetricAspect extends AspectHelper {

    private final MeterRegistry registry;

    @Value("${metrics.serviceName}")
    private String serviceName;

    @Value("${app.vertical}")
    private String verticalName;

    @Autowired
    public MetricAspect(MeterRegistry registry) {
        this.registry = registry;
    }

    /**
     * Pointcut expression for all AddTimer annotated methods
     */
    @Pointcut(value = "@annotation(net.metrosystems.inventoryaggregator.config.monitoring.annotation.AddTimer)")
    void addTimerToAnnotatedMethods() {
    }


    @Around(value = "addTimerToAnnotatedMethods()")
    public Object timedAnnotatedMethods(ProceedingJoinPoint point) throws Throwable {

        Optional<List<String>> tagMap = retrieveTags(point);

        Timer timer = tagMap.map(
                strings -> Timer
                        .builder(String.format("%s.%s.%sTimer", verticalName, serviceName, point.getStaticPart().getSignature().getName()))
                        .tags(strings.toArray(new String[0]))
                        .register(registry))
                .orElseGet(() -> Timer
                        .builder(String.format("%s.%s.%sTimer", verticalName, serviceName, point.getStaticPart().getSignature().getName()))
                        .register(registry));

        ThrowableHolder holder = new ThrowableHolder();
        Object result = timer.recordCallable(() -> {
            try {
                return point.proceed();
            } catch (Throwable e) {
                holder.throwable = e;
            }
            return null;
        });
        if (null != holder.throwable) {
            throw holder.throwable;
        }
        return result;
    }

    private static class ThrowableHolder {
        Throwable throwable;
    }
}
