package model.exceptions;

public class BadUsernameFormat extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getMessage() {
		return "The format of the username is bad!";
	}
	
}
