package com.macalester.mealplanner.exceptions;

public class UniqueConstraintViolationException extends RuntimeException {

  public UniqueConstraintViolationException(String message) {
    super(message);
  }
}
