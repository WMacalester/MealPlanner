package com.macalester.mealplanner.exceptions;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }
}
