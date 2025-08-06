package com.weather.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WeatherAppException.class)
    public ResponseEntity<ErrorResponse> handleWeatherAppException(WeatherAppException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex, request.getDescription(false));
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        WeatherAppException weatherEx = new ValidationException(message);
        ErrorResponse errorResponse = new ErrorResponse(weatherEx, request.getDescription(false));
        return new ResponseEntity<>(errorResponse, weatherEx.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        WeatherAppException weatherEx = new ValidationException(message);
        ErrorResponse errorResponse = new ErrorResponse(weatherEx, request.getDescription(false));
        return new ResponseEntity<>(errorResponse, weatherEx.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String message = ex.getName() + " should be of type " + ex.getRequiredType().getSimpleName();
        WeatherAppException weatherEx = new ValidationException(message);
        ErrorResponse errorResponse = new ErrorResponse(weatherEx, request.getDescription(false));
        return new ResponseEntity<>(errorResponse, weatherEx.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        log.error(ex.getMessage());
        WeatherAppException weatherEx = new WeatherAppException(
                "INTERNAL_ERROR",
                "An unexpected error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        ErrorResponse errorResponse = new ErrorResponse(weatherEx, request.getDescription(false));
        return new ResponseEntity<>(errorResponse, weatherEx.getHttpStatus());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, WebRequest request) {
        WeatherAppException weatherEx = new BadRequestException(ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(weatherEx, request.getDescription(false));
        return new ResponseEntity<>(errorResponse, weatherEx.getHttpStatus());
    }
}
