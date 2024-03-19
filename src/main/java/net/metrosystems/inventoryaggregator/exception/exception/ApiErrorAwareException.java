package net.metrosystems.inventoryaggregator.exception.exception;

public abstract class ApiErrorAwareException extends Exception implements ApiErrorAware {
  public ApiErrorAwareException() {}

  public ApiErrorAwareException(String message) {
    super(message);
  }

  public ApiErrorAwareException(String message, Throwable t) {
    super(message, t);
  }
}
