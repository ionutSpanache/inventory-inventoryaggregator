package inventory.aggregator.config.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan("inventory.aggregator")
@Aspect
public class AspectConfig {
    @Pointcut("execution (* inventory.aggregator.*.controller.*.*(..))")
    public void controller() {
    }

    @Pointcut("execution (* inventory.aggregator.*.service.*.*(..))")
    public void services() {
    }

    @Pointcut("execution (* inventory.aggregator.*.client.*.*(..))")
    public void client() {
    }

    @Pointcut("execution (* inventory.aggregator.articleId.client.article_cache_aaa.*.*.*(..))")
    public void articleCacheAAA() {
    }

    @Pointcut("execution (* inventory.aggregator.articleId.client.article_cache_aaa.config.*.*(..))")
    public void articleCacheAAAConfig() {
    }


    @Pointcut("(controller()||services()||client()||articleCacheAAA())&&!articleCacheAAAConfig()")
    public void appFlow() {
    }

    @Bean
    public PerformanceMonitorInterceptor performanceMonitorInterceptor() {
        return new PerformanceMonitorInterceptor(true);
    }

    @Bean
    public Advisor performanceMonitorAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("inventory.aggregator.config.aspect.AspectConfig.appFlow()");
        return new DefaultPointcutAdvisor(pointcut, performanceMonitorInterceptor());
    }
}


