package net.metrosystems.inventoryaggregator.config.aspect;

import java.util.Collections;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class TraceInformation {
    public static Map<String, String> getTraceInformationForComunication() {
        Map<String, String> traceInfo = Collections.synchronizedMap(new HashMap<>());
        traceInfo.put("X-Correlation-ID", MDC.get("X-Correlation-ID"));
        traceInfo.put("Flow", MDC.get("Flow"));
        return traceInfo;
    }

}
