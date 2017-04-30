package game;

import java.util.Stack;

import controller.Commands;
import controller.GameInputListener;
import controller.GameMessage;

public class MyClientListener implements GameInputListener{

	private String interrupt = "";
	private Thread clientThread;
	private GameMessage lastMsg = null;
	private Stack<GameMessage> msgStack = new Stack<>();
	
	public MyClientListener(Thread th){
		clientThread = th;	
	}
	
	/**
	 * Hangs the thread until the server sends a certain msg.
	 * @param msg
	 * @return the first {@link GameMessage} of type msg that was recieved 
	 */ 
	public GameMessage waitForMsg(String msg){
		
		//check if msg was recieved already
		for (GameMessage gm : msgStack){
			if(gm.getMessage().equals(msg)){
				msgStack.remove(gm);
				System.out.println( msg + " pulled from stack");
				return gm;
			}
		}
		
		try {
			interrupt = msg;
			while (true){
				System.out.println("client waiting for " + msg);
				Thread.sleep(10000);
			}
		} catch (InterruptedException e) {
			GameMessage ret = lastMsg;
			lastMsg = null;
			System.out.println("client reciewed " + msg);
			return ret;
		}
	}
	
	@Override
	public void gotMessage(GameMessage msg) {
		
		msgStack.push(msg);
		
		if(msg.getMessage().equals(interrupt)) {
			interrupt = "";
			lastMsg = msg;
			clientThread.interrupt();
		};
		
	}
	
}
