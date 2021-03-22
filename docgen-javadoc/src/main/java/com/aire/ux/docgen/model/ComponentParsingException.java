package com.aire.ux.docgen.model;

public class ComponentParsingException extends RuntimeException {

  public ComponentParsingException() {
    super();
  }

  public ComponentParsingException(String message) {
    super(message);
  }

  public ComponentParsingException(String message, Throwable cause) {
    super(message, cause);
  }

  public ComponentParsingException(Throwable cause) {
    super(cause);
  }

  protected ComponentParsingException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
