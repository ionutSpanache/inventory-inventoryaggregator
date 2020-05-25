package net.metrosystems.inventoryaggregator.config.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Aspect
@Component
public class Aspects {

    @Around("net.metrosystems.inventoryaggregator.config.aspect.AspectConfig.appFlow()")
    public Object logAroundAllMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        final Logger LOG = getLogger(joinPoint.getTarget().getClass());
        long start = System.currentTimeMillis();
        try {

            Object[] signatureArgs = joinPoint.getArgs();
            LOG.debug("Run: {} with Params: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
            Object response = joinPoint.proceed();
            LOG.debug("{} returned with value {}", joinPoint.getSignature().getName(), response);
            return response;
        } catch (Exception ex) {
            LOG.error("{} returned with exception {}", joinPoint.getSignature().getName(), ex);
            throw ex;
        } finally {
            long end = System.currentTimeMillis();
            long time = end - start;
            LOG.debug("{} execution {} ms", joinPoint.getSignature().getName(), time);
        }
    }


}
