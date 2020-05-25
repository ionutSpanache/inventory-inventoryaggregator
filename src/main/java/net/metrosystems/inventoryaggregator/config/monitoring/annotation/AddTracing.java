package net.metrosystems.inventoryaggregator.config.monitoring.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AddTracing {
}
