package net.metrosystems.inventoryaggregator.config.monitoring.aspect;

import net.metrosystems.inventoryaggregator.config.monitoring.annotation.TagField;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AspectHelper {


    /**
     * In an array of annotations, find the annotation of the specified type, if any.
     *
     * @return the annotation if available, or null
     */
    @SuppressWarnings("unchecked")
    private static <T extends Annotation> T getAnnotationByType(final Annotation[] annotations) {

        T result = null;
        for (final Annotation annotation : annotations) {
            if (TagField.class.isAssignableFrom(annotation.getClass())) {
                result = (T) annotation;
                break;
            }
        }
        return result;
    }

    Optional<List<String>> retrieveTags(JoinPoint point) {

        List<String> keysValues = new ArrayList<>();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String[] parameterNames = signature.getParameterNames();
        Object[] parameterValues = point.getArgs();
        Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            TagField paramAnnotation = getAnnotationByType(annotations);
            if (paramAnnotation != null) {

                keysValues.add(parameterNames[i]);
                keysValues.add(parameterValues[i].toString());

            }

        }

        return Optional.ofNullable(keysValues);

    }


}
