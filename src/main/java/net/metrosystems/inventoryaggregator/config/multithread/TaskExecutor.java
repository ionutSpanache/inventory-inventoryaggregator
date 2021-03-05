package net.metrosystems.inventoryaggregator.config.multithread;

import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author andreea.curia Management of Threads
 */
//@Service
//@Component
public class TaskExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(TaskExecutor.class);
    private static int executorSetQueueCapacity;
    //private static ThreadPoolTaskExecutor mainExecutor;
    private static final AtomicInteger task = new AtomicInteger(0);


}
