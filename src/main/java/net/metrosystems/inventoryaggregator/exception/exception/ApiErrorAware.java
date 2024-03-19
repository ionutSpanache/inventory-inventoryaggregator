package net.metrosystems.inventoryaggregator.exception.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public interface ApiErrorAware {

    default String getMessageId() {
        return null;
    }

    default Map<String, String> getMessageVariables() {
        return null;
    }

    HttpStatus getHttpStatus();
}
