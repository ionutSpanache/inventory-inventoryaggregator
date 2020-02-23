package inventory.aggregator.config.multithread;

import inventory.aggregator.config.multithread.config.ConfigPropertiesMultiThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author andreea.curia Management of Threads
 */
@Service
@Component
public class TaskExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(TaskExecutor.class);
    private static int executorSetQueueCapacity;
    private static ThreadPoolTaskExecutor mainExecutor;
    private static AtomicInteger task = new AtomicInteger(0);

    private TaskExecutor() {
    }


    private static ThreadPoolTaskExecutor initExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(ConfigPropertiesMultiThread.getMultithreadExecutorCorePoolSize());
        executor.setMaxPoolSize(ConfigPropertiesMultiThread.getMultithreadExecutorMaxPoolSize());
        executor.setQueueCapacity(ConfigPropertiesMultiThread.getMultithreadExecutorQueueCapacity());
        executor.setKeepAliveSeconds(ConfigPropertiesMultiThread.getMultithreadExecutorKeepAliveSeconds());
        executor.setAwaitTerminationSeconds(ConfigPropertiesMultiThread.getMultithreadExecutorAwaitTerminationSeconds());
        //executor.setWaitForTasksToCompleteOnShutdown(ConfigPropertiesMultiThread.getMultithreadExecutorWaitForTasksToCompleteOnShutdown());
        executor.setThreadNamePrefix(ConfigPropertiesMultiThread.getMultithreadExecutorThreadNamePrefix());
        executor.setTaskDecorator(new TraceInformationTaskDecorator());
        executor.initialize();
        return executor;
    }

    public static synchronized ThreadPoolTaskExecutor getExecutor() {
        if (mainExecutor == null) {
            mainExecutor = initExecutor();
        }
        return mainExecutor;
    }

    public static int addTask() {
        return task.incrementAndGet();

    }

    public static int removeTask() {
        return task.decrementAndGet();
    }

    public static int getNoTasks() {
        return task.get();
    }


}
