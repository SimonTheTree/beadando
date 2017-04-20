package model.exceptions;

public class UserNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7085106559946284312L;

	public String getMessage() {
		return "This user doesn't exist";
	}
	
}
