package controller;

/**<b>GameInputListener</b> interfesz.<p>*/
public interface GameInputListener {

	/**Ha uzeneted erkezik, akkor lesz meghivva.<br>
	 * @param msg az uzenet, ami egy {@link GameMessage}*/
	public void gotMessage(GameMessage msg);
	
}
