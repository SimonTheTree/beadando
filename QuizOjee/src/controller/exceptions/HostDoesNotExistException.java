package controller.exceptions;

public class HostDoesNotExistException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getMessage() {
		return "This host doesn't exist";
	}
	
}
