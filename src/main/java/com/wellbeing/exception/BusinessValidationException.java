package com.wellbeing.exception;

/** Thrown when a request is well-formed but violates a domain/business rule
 *  (equivalent to the FacesMessage errors your JSF beans/validators used to raise). */
public class BusinessValidationException extends RuntimeException {
    public BusinessValidationException(String message) {
        super(message);
    }
}
