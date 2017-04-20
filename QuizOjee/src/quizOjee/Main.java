package quizOjee;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import controller.Commands;
import controller.Controller;
import controller.GameClient;
import controller.GameHost;
import controller.GameInputListener;
import controller.GameMessage;
import controller.exceptions.GameIsStartedException;
import controller.exceptions.HostDoesNotExistException;


public class Main {
	
	public static void main (String[] args){
		System.out.println("hello");
		
		//Controller c = new Controller();
		
		//DEMO kapcsolat a host-kliens kozott.
		//Kulon szalon futnak mintha kulon alkalmazas inditotta volna oket.
		//Megertesehez ajanlom a GameHost es GameClient osztaly leirasanak olvasgatasat :P
		
		hostThread().start();
		clientThread("asd",false).start();
		clientThread("asd2",false).start();
		clientThread("Mr.Troll",true).start();
	}
	
	public static Thread hostThread() {
		return new Thread() {
			public void run() {
				GameHost host = null;
				try {
					host = new GameHost();
					host.addInputListener(hostInputListener(host));
					while(!host.isStarted()) {Thread.sleep(100);}
					Set<String> uNames = host.getUserNames();
					String[] userNames = new String[uNames.size()];
					int i = 0;
					for(String uname : uNames) {
						userNames[i] = uname;
						++i;
					}
					host.broadCast(new GameMessage(Commands.ATTACK,userNames[0],"0"));
					host.sendMessage(userNames[0],new GameMessage(Commands.WHO_ARE_YOU,userNames[0]));
					Thread.sleep(3000);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					host.abort();
				}
			}
		};
	}
	
	public static GameInputListener hostInputListener(GameHost host) {
		return new GameInputListener() {
			public void gotMessage(GameMessage msg) {
				if(msg.getMessage().equals(Commands.ATTACK)) {
					System.out.println("host: " + msg.getParams()[0]+" megtamadta a "+ msg.getParams()[1]+" blockot!");
				} else if(msg.getMessage().equals(Commands.END)) {
					System.out.println("host: " + "Vege");
				} else if(msg.getMessage().equals(Commands.SOMEONE_LEFT)) {
					System.out.println("host: " + msg.getParams()[0]+ " egy kocsog es kilepett...");
				} else if(msg.getMessage().equals(Commands.CHOOSE)) {
					System.out.println("host: " + msg.getParams()[0]+" kivalasztotta a "+ msg.getParams()[1]+" blockot!");
				} else if(msg.getMessage().equals(Commands.RETURNED)) {
					System.out.println("host: " + msg.getParams()[0]+ " RETURNED");
					//FONTOS a varakozasi ido
					try {
						Thread.sleep(100); 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					host.sendMessage(msg.getParams()[0], new GameMessage(Commands.GAME,"ez a jatek","ez meg a map"));
				} else {
					System.out.println("host: " + msg.getMessage());
				}
			}
		};
	}
	
	public static GameInputListener clientInputListener(String name) {
		return new GameInputListener() {
			public void gotMessage(GameMessage msg) {
				if(msg.getMessage().equals(Commands.ATTACK)) {
					System.out.println(name + ": " + msg.getParams()[0]+" megtamadta a "+ msg.getParams()[1]+" blockot!");
				} else if(msg.getMessage().equals(Commands.END)) {
					System.out.println(name + ": " + "Vege");
				} else if(msg.getMessage().equals(Commands.SOMEONE_LEFT)) {
					System.out.println(name + ": " + msg.getParams()[0]+ " egy kocsog es kilepett...");
				} else if(msg.getMessage().equals(Commands.CHOOSE)) {
					System.out.println(name + ": " + msg.getParams()[0]+" kivalasztotta a "+ msg.getParams()[1]+" blockot!");
				} else if(msg.getMessage().equals(Commands.RETURNED)) {
					System.out.println(name + ": " + msg.getParams()[0]+ " RETURNED");
				} else {
					System.out.println(name + ": " + msg.getMessage());
				}
			}
		};
	}
	
	public static Thread clientThread(String uname,boolean trollkodik) {
		return new Thread() {
			public void run() {
				GameClient client = null;
				try {
					client = new GameClient("localhost", uname);
					client.addInputListener(clientInputListener(uname));
					while(!client.isStarted()) {Thread.sleep(100);}
					client.sendMessage(new GameMessage(Commands.CHOOSE,uname,"1"));
					if(trollkodik) {
						client.abort();
						Thread.sleep(1000);
						client = new GameClient("localhost", uname);
						client.addInputListener(clientInputListener(uname));
						while(!client.isStarted()) {Thread.sleep(100);}
						client.sendMessage(new GameMessage(Commands.CHOOSE,uname,"1"));
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (GameIsStartedException e) {
					e.printStackTrace();
				} catch (HostDoesNotExistException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
}
