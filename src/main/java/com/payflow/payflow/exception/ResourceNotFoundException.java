package com.payflow.payflow.exception;

public class ResourceNotFoundException extends RuntimeException{
  public  ResourceNotFoundException(String message)
    {
        super(message);
    }
}
