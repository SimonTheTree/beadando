package controller.exceptions;

public class GameIsStartedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getMessage() {
		return "The game is already started!";
	}
}
