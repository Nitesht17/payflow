package com.payflow.payflow.exception;

/**
 * Thrown when a transaction status change violates the allowed lifecycle
 * (e.g. trying to modify a transaction that is already SUCCESS or FAILED).
 */
public class InvalidStatusTransitionException extends RuntimeException{
    public InvalidStatusTransitionException(String msg)
    {
        super(msg);
    }
}
