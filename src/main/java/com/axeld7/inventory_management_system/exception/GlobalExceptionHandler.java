package com.axeld7.inventory_management_system.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {
    ErrorResponseDTO error =
        new ErrorResponseDTO(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<ErrorResponseDTO> handleDuplicateEntity(
      DuplicateResourceException ex, HttpServletRequest request) {
    ErrorResponseDTO error =
        new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(AssetNotAvailableException.class)
  public ResponseEntity<ErrorResponseDTO> handleAssetNotAvailable(
      AssetNotAvailableException ex, HttpServletRequest request) {
    ErrorResponseDTO error =
        new ErrorResponseDTO(
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }
    @ExceptionHandler(AssetNotCheckedOutException.class)
  public ResponseEntity<ErrorResponseDTO> handleAssetNotCheckedOut(
      AssetNotCheckedOutException ex, HttpServletRequest request) {
    ErrorResponseDTO error =
        new ErrorResponseDTO(
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
  public ResponseEntity<ErrorResponseDTO> handleOptimisticLockingFailure(
      ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
    ErrorResponseDTO error =
        new ErrorResponseDTO(
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            "This record was updated by another user or process, please refresh and try again.",
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleUnhandledException(
      Exception ex, HttpServletRequest request) {

    log.error(
        "Unhandled exception caught on path: {} - Message: {}",
        request.getRequestURI(),
        ex.getMessage(),
        ex);

    ErrorResponseDTO error =
        new ErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "An unexpected internal error occurred.",
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
