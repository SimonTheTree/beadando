package model.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2630771900776621333L;
	
	public String getMessage() {
		return "This user already exists";
	}
}
