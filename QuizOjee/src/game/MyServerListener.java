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
	private int interruptCounter;
	private Thread clientThread;
	private Stack<GameMessage> lastMsg = new Stack<>();
	private Stack<GameMessage> msgStack = new Stack<>();
	private GameSettings settings = GameSettings.getInstance();
	public MyServerListener(Thread th){
		clientThread = th;	
	}
	
	/**
	 * Hangs the thread until all clients send a certain msg.
	 * @param msg
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) 
	 */
	public GameMessage[] waitForMsg(String msg){
		return waitForMsg(msg, 0);
	}
	/**
	 * Hangs the thread until all clients send a certain msg.
	 * @param msg
	 * @param maxTime the maximal time the thread will wait.
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) 
	 */
	public GameMessage[] waitForMsg(String msg, long maxTime){
		lastMsg.clear();
		
		interruptCounter = settings.PLAYERS.size();
		
		//check if msg was recieved already
		for (GameMessage gm : msgStack){
			if(gm.getMessage().equals(msg)){
				lastMsg.push(gm);
				msgStack.remove(gm);
				System.out.println( msg + " pulled from stack");
			
				if(--interruptCounter == 0){ 
					return lastMsg.toArray(new GameMessage[0]);
				}
			}
		}
		
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

	/**
	 * Hangs the thread until a certain client sends a certain msg.
	 * @param clientName
	 * @param msg
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) 
	 */
	public GameMessage waitForMsgFrom(String clientName, String msg){
		return waitForClientMsg(clientName, msg, 0);
	}
	/**
	 * Hangs the thread until a certain client sends a certain msg.
	 * @param clientName
	 * @param msg
	 * @param maxTime the maximal time the thread will wait.
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) 
	 */
	public GameMessage waitForClientMsg(String clientName, String msg, long maxTime){
		//check if msg was recieved already
		for (GameMessage gm : msgStack){
			if(gm.getMessage().equals(msg)){
				msgStack.remove(gm);
				System.out.println( msg + " pulled from stack");
				return gm;
			}
		}
		
		interruptCounter = 1;
		try {
			interrupt = msg;
			while (true){
				System.out.println("server waiting for " + msg);
				Thread.sleep(10000);
			}
		} catch (InterruptedException e) {
			GameMessage ret = lastMsg.pop();
			lastMsg = null;
			System.out.println("server recieved " + msg);
			return ret;
		}
	}
	
	@Override
	public void gotMessage(GameMessage msg) {
		
		msgStack.push(msg);
		if(msg.getMessage().equals(interrupt)) {
			lastMsg.push(msg);
			System.out.println("server recieved " + msg + " from " + msg.getSender() );
			if(--interruptCounter == 0){ 
				clientThread.interrupt(); //this one should interrupt the waitFor... method
			}
		};
		if(msg.getMessage().equals(Commands.ATTACK)) {
			msgStack.pop();
		} else {
		};
		
	}
	
}
