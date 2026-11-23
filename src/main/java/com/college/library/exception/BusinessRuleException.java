package com.college.library.exception;

/** Generic business-rule violation (max 3 issues, already returned, delete blocked, ...). Maps to 400. */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
