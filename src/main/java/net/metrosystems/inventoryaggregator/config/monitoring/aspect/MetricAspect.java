package net.metrosystems.inventoryaggregator.config.monitoring.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Aspect
public class MetricAspect extends AspectHelper {

    private final MeterRegistry registry;

    @Value("${metrics.serviceName}")
    private String serviceName;

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
                        .builder(String.format("mstore.%s.%sTimer", serviceName, point.getStaticPart().getSignature().getName()))
                        .tags(strings.toArray(new String[0]))
                        .register(registry))
                .orElseGet(() -> Timer
                        .builder(String.format("mstore.%s.%sTimer", serviceName, point.getStaticPart().getSignature().getName()))
                        .register(registry));

//        return timer.recordCallable(() -> {
//            try {
//                return point.proceed();
//            } catch (Throwable throwable) {
//                throw new RuntimeException(throwable);
//            }
//        });
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
