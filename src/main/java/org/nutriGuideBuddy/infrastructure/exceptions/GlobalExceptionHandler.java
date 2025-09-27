package org.nutriGuideBuddy.infrastructure.exceptions;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(WebExchangeBindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Mono<ProblemDetail> handleWebExchangeBindException(WebExchangeBindException ex) {
    Map<String, String> errors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.put(error.getField(), error.getDefaultMessage());
    }

    var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setTitle("Validation Error");
    problemDetail.setDetail("Validation failed for one or more fields.");
    problemDetail.setProperty("errors", errors);

    return Mono.just(problemDetail);
  }

  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Mono<ProblemDetail> handleValidation(ValidationException ex, ServerHttpRequest req) {
    var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Validation Failed");
    pd.setDetail(ex.getMessage());
    pd.setInstance(req.getURI());
    if (ex.getResource() != null) pd.setProperty("resource", ex.getResource());
    pd.setProperty("errors", ex.getErrors());
    return Mono.just(pd);
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Mono<ProblemDetail> handleNotFoundException(
      NotFoundException ex, ServerHttpRequest request) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    pd.setTitle("Resource Not Found");
    pd.setInstance(request.getURI());

    if (ex.getResource() != null) pd.setProperty("resource", ex.getResource());
    if (ex.getField() != null) pd.setProperty("field", ex.getField());
    if (ex.getValue() != null) pd.setProperty("value", String.valueOf(ex.getValue()));
    pd.setProperty("timestamp", java.time.OffsetDateTime.now().toString());

    return Mono.just(pd);
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Mono<ProblemDetail> handleBadRequestException(
      BadRequestException ex, ServerHttpRequest request) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Bad Request");
    pd.setDetail(ex.getMessage());
    pd.setInstance(request.getURI());

    if (ex.getResource() != null) pd.setProperty("resource", ex.getResource());
    if (ex.getReason() != null) pd.setProperty("reason", ex.getReason());
    pd.setProperty("timestamp", java.time.OffsetDateTime.now().toString());

    return Mono.just(pd);
  }

  @ExceptionHandler(ServiceUnavailableException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public Mono<ProblemDetail> handleServiceUnavailableException(
      ServiceUnavailableException ex, ServerHttpRequest request) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
    pd.setTitle("Service Temporarily Unavailable");
    pd.setDetail(ex.getMessage());
    pd.setInstance(request.getURI());

    if (ex.getService() != null) {
      pd.setProperty("service", ex.getService());
    }
    if (ex.getReason() != null) {
      pd.setProperty("reason", ex.getReason());
    }

    pd.setProperty("timestamp", java.time.OffsetDateTime.now().toString());

    return Mono.just(pd);
  }
}
