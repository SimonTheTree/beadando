package game;

import controller.Commands;
import controller.GameInputListener;
import controller.GameMessage;

public class MyClientListener implements GameInputListener{

	private String interrupt = "";
	private Thread clientThread;
	private GameMessage lastMsg = null;
	
	public MyClientListener(Thread th){
		clientThread = th;	
	}
	
	/**
	 * Hangs the thread until the server sends a certain msg.
	 * @param msg
	 * @return the first {@link GameMessage} of type msg that was recieved 
	 */
	public GameMessage waitForMsg(String msg){
		interrupt = msg;
		try {
			while (true)
				Thread.sleep(10000);
		} catch (InterruptedException e) {
			GameMessage ret = lastMsg;
			lastMsg = null;
			return ret;
		}
	}
	
	@Override
	public void gotMessage(GameMessage msg) {
		
		if(msg.getMessage().equals(interrupt)) {
			interrupt = "";
			lastMsg = msg;
			clientThread.interrupt();
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
