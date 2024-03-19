package net.metrosystems.inventoryaggregator.config.monitoring.annotation;

import java.lang.annotation.*;

/**
 * to be used as key/values for adding additional tags when generating metrics.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TagField {
    String name() default "";
}
