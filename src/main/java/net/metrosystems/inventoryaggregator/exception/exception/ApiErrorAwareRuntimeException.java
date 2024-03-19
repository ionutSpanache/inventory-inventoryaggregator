package net.metrosystems.inventoryaggregator.exception.exception;

public abstract class ApiErrorAwareRuntimeException extends RuntimeException implements ApiErrorAware {
  public ApiErrorAwareRuntimeException() {}

  public ApiErrorAwareRuntimeException(String message) {
    super(message);
  }

  public ApiErrorAwareRuntimeException(String message, Throwable t) {
    super(message, t);
  }
}
