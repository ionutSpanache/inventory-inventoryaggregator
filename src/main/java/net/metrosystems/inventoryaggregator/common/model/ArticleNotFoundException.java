package net.metrosystems.inventoryaggregator.common.model;

import net.metrosystems.inventoryaggregator.exception.exception.ApiErrorAwareRuntimeException;
import org.springframework.http.HttpStatus;

public class ArticleNotFoundException extends ApiErrorAwareRuntimeException {

	public ArticleNotFoundException() {
		super();
	}

	public ArticleNotFoundException(String message) {
		super(message);
	}

  public ArticleNotFoundException(String message, Throwable t) {
    super(message, t);
	}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.NO_CONTENT;
	}
}
