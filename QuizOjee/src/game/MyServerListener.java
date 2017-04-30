package game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import controller.Commands;
import controller.GameInputListener;
import controller.GameMessage;
import game.players.Player;

public class MyServerListener implements GameInputListener{

	private String interrupt;
	private Thread clientThread;
	private Stack<GameMessage> lastMsg = new Stack<>();
	private GameSettings settings = GameSettings.getInstance();
	public MyServerListener(Thread th){
		clientThread = th;	
	}
	
	/**
	 * Hangs the thread until the server sends a certain msg.
	 * @param msg
	 * @return the first {@link GameMessage} of type msg that was recieved 
	 */
	public GameMessage[] waitForMsg(String msg, long maxTime){
		lastMsg.clear();
		
		try{
			interrupt = msg; // start listening-collecting-interrupting for "msg" messages
			
			System.out.println("server waiting for " + msg);
			if(maxTime != 0){
				Thread.sleep(maxTime);
			} else {
				while(true){
					Thread.sleep(10000);
				}				
			}
		}catch(InterruptedException e){
			// at this point lastMsg has collected settings.PLAYERS.size() GameMessages.
			return lastMsg.toArray(new GameMessage[0]);
			
		} finally {
			interrupt = null; // stop special listening
		}
		//if time ran out:
		return null;
	}
	
	@Override
	public void gotMessage(GameMessage msg) {
		
		if(msg.getMessage().equals(interrupt)) {
			lastMsg.push(msg);
			System.out.println("server recieved " + msg + " from " + msg.getSender() );
			if(settings.PLAYERS.size() == lastMsg.size()){ 
				clientThread.interrupt(); //this one should interrupt the waitFor... method
			}
		};
		if(msg.getMessage().equals(Commands.ATTACK)) {
		} else if(msg.getMessage().equals(Commands.END)) {
		} else if(msg.getMessage().equals(Commands.SOMEONE_LEFT)) {
		} else if(msg.getMessage().equals(Commands.CHOOSE)) {
		} else if(msg.getMessage().equals(Commands.RETURNED)) {
		} else {
		};
		
	}
	
}
