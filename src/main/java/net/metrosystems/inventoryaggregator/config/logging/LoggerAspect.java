package net.metrosystems.inventoryaggregator.config.logging;

import java.lang.reflect.Parameter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import java.util.Arrays;
import org.slf4j.MDC;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LoggerAspect {

    // M|Runtime allows a maximum of 16KB
    // Java chars are 2 bytes

    private static final String EMAIL_REGEX = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b";
    private static final int LOG_MAX_LENGTH = (16*1024) /2;
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
                    redactEmailAddresses(Arrays.toString(joinPoint.getArgs())),
                    result.toString().substring(0,Math.min(result.toString().length(), LOG_MAX_LENGTH)));
        } catch (Exception e) {
            addLogging(joinPoint, false);
            MDC.put("executionTimeInMillis", Long.toString(System.currentTimeMillis() - start));
            log.error("Called: {}.{}() with arguments = {}, received result = {}",
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                    redactEmailAddresses(Arrays.toString(joinPoint.getArgs())),
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
            MDC.put(parametersNames[index].getName(), redactEmailAddresses(parametersValues[index].toString())));
    }

    @Cacheable("compiledEmailPattern")
    public Pattern getEmailPattern() {
        return Pattern.compile(EMAIL_REGEX);
    }

    public String redactEmailAddresses(String object) {
        Matcher matcher = getEmailPattern().matcher(object);
        return matcher.replaceAll("REDACTED");
    }
}