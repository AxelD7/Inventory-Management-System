package com.axeld7.inventory_management_system.exception;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDTO(
    int status,
    String error,
    String message,
    String path,
    Instant timestamp,
    Map<String, String> validationErrors) {

  //Constructor for non validation request errors
  public ErrorResponseDTO(int status, String error, String message, String path) {
    this(status, error, message, path, Instant.now(), null);
  }

  //Constructor for when there is a 400 bad request.
  public ErrorResponseDTO(
      int status, String error, String message, String path, Map<String, String> validationErrors) {
    this(status, error, message, path, Instant.now(), validationErrors);
  }
}
