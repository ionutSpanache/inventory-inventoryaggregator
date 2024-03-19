package net.metrosystems.inventoryaggregator.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.valueOf;
import static org.springframework.util.StringUtils.hasText;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.exception.exception.ApiErrorAwareException;
import net.metrosystems.inventoryaggregator.exception.exception.ApiErrorAwareRuntimeException;
import net.metrosystems.inventoryaggregator.exception.model.ApiError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private final DomainSupplier domainSupplier;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> handleException(Exception ex) {
    logException(ex);
    return new ResponseEntity<>(
        apiErrorPrototype(INTERNAL_SERVER_ERROR, ex), INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  ResponseEntity<ApiError> handleException(ConstraintViolationException ex) {
    logException(ex);

    var apiError = apiErrorPrototype(BAD_REQUEST, ex);
    apiError.setMessage("The received object is not valid");

    Map<String, String> rejectedFields = new HashMap<>();
    ex.getConstraintViolations()
        .forEach(
            e ->
                rejectedFields.put(
                    e.getPropertyPath().toString(),
                    "rejected value [" + e.getInvalidValue() + "]"));
    apiError.setMessageVariables(rejectedFields);

    return new ResponseEntity<>(apiError, BAD_REQUEST);
  }

  @ExceptionHandler(ApiErrorAwareException.class)
  ResponseEntity<ApiError> handleApiErrorAwareException(ApiErrorAwareException ex) {
    logException(ex);
    ApiError apiError =
        apiErrorPrototype(ex.getHttpStatus(), ex)
            .setMessage(
                ex.getMessage() != null ? ex.getMessage() : ex.getHttpStatus().getReasonPhrase())
            .setMessageId(ex.getMessageId())
            .setMessageVariables(ex.getMessageVariables());
    return new ResponseEntity<>(apiError, ex.getHttpStatus());
  }

  @ExceptionHandler(ApiErrorAwareRuntimeException.class)
  ResponseEntity<ApiError> handleApiErrorAwareRuntimeException(ApiErrorAwareRuntimeException ex) {
    logException(ex);
    ApiError apiError =
        apiErrorPrototype(ex.getHttpStatus(), ex)
            .setMessage(
                ex.getMessage() != null ? ex.getMessage() : ex.getHttpStatus().getReasonPhrase())
            .setMessageId(ex.getMessageId())
            .setMessageVariables(ex.getMessageVariables());
    return new ResponseEntity<>(apiError, ex.getHttpStatus());
  }

  @ExceptionHandler(ExecutionException.class)
  ResponseEntity<ApiError> handleExecutionException(ExecutionException ex) {
    if (ex.getCause() instanceof ApiErrorAwareException) {
      ApiErrorAwareException exCause = (ApiErrorAwareException) ex.getCause();
      logException(ex);
        ApiError apiError =
          apiErrorPrototype(exCause.getHttpStatus(), exCause)
              .setMessage(
                  exCause.getMessage() != null
                      ? exCause.getMessage()
                      : exCause.getHttpStatus().getReasonPhrase())
              .setMessageId(exCause.getMessageId())
              .setMessageVariables(exCause.getMessageVariables());
      return new ResponseEntity<>(apiError, exCause.getHttpStatus());
    } else if (ex.getCause() instanceof ApiErrorAwareRuntimeException) {
      ApiErrorAwareRuntimeException exCause = (ApiErrorAwareRuntimeException) ex.getCause();
      logException(ex);
      ApiError apiError =
          apiErrorPrototype(exCause.getHttpStatus(), exCause)
              .setMessage(
                  exCause.getMessage() != null
                      ? exCause.getMessage()
                      : exCause.getHttpStatus().getReasonPhrase())
              .setMessageId(exCause.getMessageId())
              .setMessageVariables(exCause.getMessageVariables());
      return new ResponseEntity<>(apiError, exCause.getHttpStatus());
    }
    logException(ex);
    return new ResponseEntity<>(
        apiErrorPrototype(INTERNAL_SERVER_ERROR, ex), INTERNAL_SERVER_ERROR);
  }

//  @Override
//  protected ResponseEntity<Object> handleMethodArgumentNotValid(
//      MethodArgumentNotValidException ex,
//      HttpHeaders headers,
//      HttpStatus status,
//      WebRequest request) {
//    logException(ex);
//
//    var apiError = apiErrorPrototype(BAD_REQUEST, ex);
//    apiError.setMessage("The received object is not valid");
//    apiError.setDebugMessage(
//        String.format(
//            "The received object of type %s is not valid", ex.getBindingResult().getObjectName()));
//    Map<String, String> rejectedFields = new HashMap<>();
//    ex.getBindingResult()
//        .getFieldErrors()
//        .forEach(
//            t ->
//                rejectedFields.put(
//                    t.getField(),
//                    t.getDefaultMessage() + ", rejected value [" + t.getRejectedValue() + "]"));
//    apiError.setMessageVariables(rejectedFields);
//
//    return handleExceptionInternal(ex, apiError, headers, BAD_REQUEST, request);
//  }

  @ExceptionHandler(WebClientResponseException.class)
  ResponseEntity<ApiError> handleWebClientResponseException(WebClientResponseException ex) {
    logException(ex);

    Function<HttpStatus, ApiError> toFallbackApiError =
        httpStatus ->
            new ApiError()
                .setTimestamp(LocalDateTime.now().toString())
                .setStatus(ex.getStatusCode().value())
                .setDomain(DomainSupplier.UNKNOWN)
                .setMessage(valueOf(ex.getStatusCode().value()).getReasonPhrase());

    ApiError subError;
    var responseBody = ex.getResponseBodyAsString();
    if (hasText(responseBody)) {
      try {
        JsonNode data = objectMapper.readTree(responseBody);
        subError = objectMapper.treeToValue(data, ApiError.class);
        subError.setDebugMessage(null);
      } catch (Exception e) {
        log.debug("Unable to parse the body message as ApiError: {}", responseBody);
        subError = toFallbackApiError.apply(valueOf(ex.getStatusCode().value()));
      }
    } else {
      subError = toFallbackApiError.apply(valueOf(ex.getStatusCode().value()));
    }

    var apiError =
        apiErrorPrototype(INTERNAL_SERVER_ERROR, ex)
            .setMessage("Downstream call failed!")
            .setSubErrors(List.of(subError));

    return new ResponseEntity<>(apiError, INTERNAL_SERVER_ERROR);
  }

//  @Override
//  protected ResponseEntity<Object> handleExceptionInternal(
//      Exception ex,
//      @Nullable Object body,
//      HttpHeaders headers,
//      HttpStatus status,
//      WebRequest request) {
//    if (isNull(body)) {
//      body = apiErrorPrototype(status, ex);
//    }
//    return new ResponseEntity<>(body, headers, status);
//  }

  private ApiError apiErrorPrototype(HttpStatus httpStatus, Exception ex) {
    return new ApiError()
            .setTimestamp(LocalDateTime.now().toString())
        .setStatus(httpStatus.value())
        .setDomain(domainSupplier.getDomain())
        .setMessage(httpStatus.getReasonPhrase())
        .setDebugMessage(ex.getMessage());
  }

  private void logException(Exception ex) {
    log.error(
        "Exception occurred : {}",
        ex instanceof ExecutionException
            ? ex.getCause().getClass().getName()
            : ex.getClass().getName(),
        ex);
  }
}
