package inventory.aggregator.config.multithread;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.HashMap;
import java.util.Map;

//add curent MDC to new threads
public class TraceInformationTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> contextMap = new HashMap<>();
        if (MDC.getCopyOfContextMap() != null) {
            contextMap = MDC.getCopyOfContextMap();
        }

        Map<String, String> finalContextMap = contextMap;
        return () -> {
            try {
                Thread t = Thread.currentThread();
                String id = finalContextMap.get("X-Correlation-ID");
                finalContextMap.put("X-Correlation-ID", id + "-" + t.getName());
                MDC.setContextMap(finalContextMap);
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}

