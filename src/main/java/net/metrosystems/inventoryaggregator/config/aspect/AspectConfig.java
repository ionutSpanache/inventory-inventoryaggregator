package net.metrosystems.inventoryaggregator.config.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.*;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan("net.metrosystems.inventoryaggregator")
@Aspect
public class AspectConfig {
    @Pointcut("execution (* net.metrosystems.inventoryaggregator.*.controller.*.*(..))")
    public void controller() {
    }

    @Pointcut("execution (* net.metrosystems.inventoryaggregator.*.service.*.*(..))")
    public void services() {
    }

    @Pointcut("execution (* net.metrosystems.inventoryaggregator.*.client.*.*(..))")
    public void client() {
    }

    @Pointcut("execution (* net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.*.*.*(..))")
    public void articleCacheAAA() {
    }

    @Pointcut("execution (* net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.config.*.*(..))")
    public void articleCacheAAAConfig() {
    }


    @Pointcut("(articleCacheAAA())&&!articleCacheAAAConfig()")
    public void appFlow() {
    }

    @Bean
    @Lazy
    public PerformanceMonitorInterceptor performanceMonitorInterceptor() {
        return new PerformanceMonitorInterceptor(false);
    }

    @Bean
    public Advisor performanceMonitorAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("net.metrosystems.inventoryaggregator.config.aspect.AspectConfig.appFlow()");
        return new DefaultPointcutAdvisor(pointcut, performanceMonitorInterceptor());
    }
}


