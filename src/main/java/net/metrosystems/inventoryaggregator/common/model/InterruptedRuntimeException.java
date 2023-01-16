package net.metrosystems.inventoryaggregator.common.model;

import net.metrosystems.inventory.common.apierror.starter.exception.ApiErrorAwareRuntimeException;
import org.springframework.http.HttpStatus;

public class InterruptedRuntimeException extends ApiErrorAwareRuntimeException {

  public InterruptedRuntimeException(String message) {
    super(message);
  }

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
