package com.vsthost.rnd.jribinding;

/**
 * Indicates that launching an R instance has failed.
 */
@SuppressWarnings("serial")
public class RBindingBootstrapException extends Exception {
    public RBindingBootstrapException(String message, Throwable cause) {
        super(message, cause);
    }
}
