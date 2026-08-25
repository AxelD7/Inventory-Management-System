package com.axeld7.inventory_management_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AssetNotAvailableException extends RuntimeException {
  public AssetNotAvailableException(String message) {
    super(message);
  }
}
