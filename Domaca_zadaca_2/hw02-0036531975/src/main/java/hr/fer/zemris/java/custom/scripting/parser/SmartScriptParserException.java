package hr.fer.zemris.java.custom.scripting.parser;


/**
 * Class SmartScriptParserException is a Runtime Exception class
 * ment to signal the client that there was an illegal
 * operation performed with parsing the document
 * 
 * @author Branimir Stankovic
 * @version 1.0
 * */
public class SmartScriptParserException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Initialize class
	 */
	public SmartScriptParserException() {}

	/**
	 * @param message message with what has gone wrong
	 */
	public SmartScriptParserException(String message) {
		super(message);
	}

	/**
	 * @param cause stack trace with what has gone wrong
	 */
	public SmartScriptParserException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message, cause message and stack trace with what has gone wrong
	 */
	public SmartScriptParserException(String message, Throwable cause) {
		super(message, cause);
	}
}
