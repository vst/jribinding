/**
 * 
 */
package com.vsthost.rnd.jribinding;

/**
 * Indicates that there was a problem with the parsing and evaluation of an expression.
 * 
 * @author vst
 */
@SuppressWarnings("serial")
public class RBindingParseAndEvalException extends Exception {
	/**
	 * @param message The exception message.
	 * @param cause The underlying cause of the exception.
	 */
	public RBindingParseAndEvalException(String message, Throwable cause) {
		super(message, cause);
	}
}
