package net.metrosystems.inventoryaggregator.config.logging;

import java.lang.reflect.Parameter;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LoggerAspect {

    @Around("@annotation(AddLogs)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        addLogging(joinPoint, false);
        var result = joinPoint.proceed();

        try {
            addLogging(joinPoint, false);
            MDC.put("executionTimeInMillis", Long.toString(System.currentTimeMillis() - start));
            log.info("Called: {}.{}() with arguments = {}, received result = {}",
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                    joinPoint.getArgs(),
                    result);
        } catch (Exception e) {
            addLogging(joinPoint, false);
            MDC.put("executionTimeInMillis", Long.toString(System.currentTimeMillis() - start));
            log.error("Called: {}.{}() with arguments = {}, received result = {}",
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                    joinPoint.getArgs(),
                    e.getMessage(), e);
        }
        return result;
    }

    private void addLogging(ProceedingJoinPoint joinPoint, Boolean doClear) {
        if(Boolean.TRUE.equals(doClear)) MDC.clear();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parametersNames = methodSignature.getMethod().getParameters();
        Object[] parametersValues = joinPoint.getArgs();
        IntStream.range(0, parametersNames.length).forEach(index ->
            MDC.put(parametersNames[index].getName(), parametersValues[index].toString()));
    }

}