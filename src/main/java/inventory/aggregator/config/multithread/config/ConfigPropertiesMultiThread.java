package inventory.aggregator.config.multithread.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Data
@Component
public class ConfigPropertiesMultiThread {

    @Getter
    private static Integer multithreadExecutorCorePoolSize;
    @Getter
    private static Integer multithreadExecutorMaxPoolSize;
    @Getter
    private static Boolean multithreadExecutorWaitForTasksToCompleteOnShutdown;
    @Getter
    private static String multithreadExecutorThreadNamePrefix;
    @Getter
    private static Integer multithreadExecutorQueueCapacity;
    @Getter
    private static Integer multithreadExecutorKeepAliveSeconds;
    @Getter
    private static Integer multithreadExecutorAwaitTerminationSeconds;

    @Autowired
    private Environment env;

    @Value("${multithread.executor.CorePoolSize}")
    public void setMultithreadExecutorCorePoolSize(Integer multithreadExecutorCorePoolSize) {
        this.multithreadExecutorCorePoolSize = multithreadExecutorCorePoolSize;
    }

    @Value("${multithread.executor.MaxPoolSize}")
    public void setMultithreadExecutorMaxPoolSize(Integer multithreadExecutorMaxPoolSize) {
        this.multithreadExecutorMaxPoolSize = multithreadExecutorMaxPoolSize;
    }

    @Value("${multithread.executor.ThreadNamePrefix}")
    public void setMultithreadExecutorWaitForTasksToCompleteOnShutdown(String multithreadExecutorThreadNamePrefix) {
        this.multithreadExecutorThreadNamePrefix = multithreadExecutorThreadNamePrefix;
    }

    @Value("${multithread.executor.QueueCapacity}")
    public void setMultithreadExecutorQueueCapacity(Integer multithreadExecutorQueueCapacity) {
        this.multithreadExecutorQueueCapacity = multithreadExecutorQueueCapacity;
    }

    @Value("${multithread.executor.KeepAliveSeconds}")
    public void setMultithreadExecutorKeepAliveSeconds(Integer multithreadExecutorKeepAliveSeconds) {
        this.multithreadExecutorKeepAliveSeconds = multithreadExecutorKeepAliveSeconds;
    }

    @Value("${multithread.executor.AwaitTerminationSeconds}")
    public void setMultithreadExecutorAwaitTerminationSeconds(Integer multithreadExecutorAwaitTerminationSeconds) {
        this.multithreadExecutorAwaitTerminationSeconds = multithreadExecutorAwaitTerminationSeconds;
    }


    public String getConfigValue(String configKey) {
        return env.getProperty(configKey);
    }


}
