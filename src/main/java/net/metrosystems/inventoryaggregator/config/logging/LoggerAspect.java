package net.metrosystems.inventoryaggregator.config.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
        if(doClear) MDC.clear();
        MDC.put("countryCode", joinPoint.getArgs()[0].toString());
        MDC.put("salesLine", joinPoint.getArgs()[1].toString());
        MDC.put("storeNumber", joinPoint.getArgs()[2].toString());
    }

}