package net.metrosystems.inventoryaggregator.common.model;

import net.metrosystems.inventory.common.apierror.starter.exception.ApiErrorAwareRuntimeException;
import org.springframework.http.HttpStatus;

public class ArticleNotFoundException extends ApiErrorAwareRuntimeException {

	public ArticleNotFoundException() {
		super();
	}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.NO_CONTENT;
	}
}
