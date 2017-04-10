package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import controller.exceptions.GameIsStartedException;
import controller.exceptions.HostDoesNotExistException;

/**
 * Ez az osztaly felelos a GameHost - GameClient kozotti kommunikacioert.<br>
 * Kepessegei: <br>
 *  - Letrehozasakor megprobal csatlakozni a szerverhez, amit 2 hibauzenettel utasithat el.<br>
 *  - majd megirom egyszer amikor kesz lesz.
 */
public class GameClient {

	private List<GameInputListener> inputListeners = new ArrayList<GameInputListener>();
	private Socket server = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private boolean end = false;
	private String userName;
	private boolean initialized = false;
	private boolean failedToConnect = false;
	private boolean gameIsStarted = false;
	private boolean tooMuchWaiting = false;
	private int waitingTime = 1000;
	
	/** Letrehoz egy {@link GameClient} objektumot. <br>
	 * A foprogram max - waitingTime - ideig varakozik, ezutan 3 lehetoseg van:<p>
	 *  - Sikeres volt a csatlakozas, nem dobott hibat.<br>
	 *  - {@link GameIsStartedExeption}-t dob, ha elutasitotta a host.<br>
	 *  - {@link HostDoesNotExistException}-t dob, ha nem valaszolt a host.... [van meg leiras]<p>
	 * Kozvetetten elinditja az inputListenert egy uj szalon.
	 */
	public GameClient(String ip, String userName) throws GameIsStartedException, HostDoesNotExistException {
		this.userName = userName;
		Thread t = new Thread(() -> {
			buildConnection(ip,userName);
		});
		t.start();
		while(!initialized && !failedToConnect && !gameIsStarted && !end) {
				try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			};
		}
		if(failedToConnect) throw new HostDoesNotExistException();
		if(gameIsStarted) throw new GameIsStartedException();
		//else System.out.println(userName + ": initialized ");
	}
	
	/**Leallitja az inputListenert, leallitja a klienst*/
	public void abort() {
		end = true;
		//System.out.println(userName + ": Akkor en most leallok");
	}
	
	/**Felepiti a kapcsolatot es beallitja a megfelelo valtozot:<br>
	 * - initialized, ha minden sikeres volt.<br>
	 * - gameIsStarted, ha elutasitottak<br>
	 * - failedToConnect, ha nem valaszolt a host<br>
	 * Elinditja az inputListenert.
	 * */
	private void buildConnection(String ip, String userName) {
		try {
			System.out.println(userName + ": ok.. let's try this!");
			server = new Socket(ip, 19969);
			in = new BufferedReader(new InputStreamReader(server.getInputStream()));
			out = new PrintWriter(server.getOutputStream());
			Timer t = letsWait(waitingTime);
			while(!in.ready() && !tooMuchWaiting) {Thread.sleep(10);}
			t.cancel();
			if(!tooMuchWaiting) {
				String asd = in.readLine();
				GameMessage msg = new GameMessage(asd);
				System.out.println(userName +": Host said: "+msg.getMessage());
				if(msg.getMessage().equals(Commands.WHO_ARE_YOU)) {
					sendMessage(new GameMessage(true, userName,Commands.LOG_IN,userName));
					while(!in.ready()) {Thread.sleep(10);}
					String predicate = in.readLine();
					msg = new GameMessage(predicate);
					System.out.println(userName +": Host said: "+ msg.getMessage());
					if(msg.getMessage().equals(Commands.JOINED)) {
						initialized = true;
						startInputListener();
						//ITT UGYSEM JOSSZ AT!!!	
					} else if(msg.getMessage().equals(Commands.ALREADY_RUNNING)) {
						System.out.println(userName + ": Oh no I came too late!");
						gameIsStarted = true;
					}
				}
			} else {
				System.out.println(userName + ": Oh no I came too late!");
				failedToConnect = true;
			}
			
		} catch (UnknownHostException e) {
			abort();
			System.out.println(userName + ": You can't reach the server");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(userName + ": Something went wrong :P");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.err.println("nincs pihi");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println(userName + " : valami elrontodott.");
			e.printStackTrace();
		} finally {
			System.out.println(userName+": Finally...");
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			out.close();
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("BuildConnection vege - "+userName);
	}

	/** 
	 * @param time - ennyi ido utan beallitja a tooMuchWaiting-t true-ra
	 * */
	private Timer letsWait(int time) {
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				tooMuchWaiting = true;
				System.out.println("IT'S TIME");
			}
		}, time);
		return t;
	}
	
	/** 
	 * Ez az eljaras felelos az input beolvasasaert.<br>
	 * Vege lesz, ha az end-et true-ra allitjuk.
	 * */
	private void startInputListener() {
		while(!end) {
			try {
				while(!in.ready()) {Thread.sleep(100); if(end) break;}
				if(end) break;
				String msg = in.readLine();
				decode(msg);
			} catch (IOException e) {
				System.out.println(userName + ": Mar figyelni se lehet rendesen?!");
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.err.println(userName +  ": Mar aludni se lehet rendesen?!");
				e.printStackTrace();
			}
		}
		System.out.println("InputListener vege - "+ userName + " client");

	}
	
	/** 
	 * A bejovo kodolt uzenetbol GameMessage-t hoz letre, es ha az automatic:<br>
	 *  - akkor ertelmezi, es vegrehajtja.
	 *  - kulonben meghivja az osszes {@link GameInputListener}.gotMessage(GameMessage) metodusat.
	 * */
	private void decode(String msg) {
		if(msg == null) return;
		GameMessage message = new GameMessage(msg);
		if(message.getMessage().equals(Commands.PING)) return;
		System.out.print(userName + ": "+ message.getSender() +" said: ");
		System.out.print(message.getMessage());
		String[] params = message.getParams();
		if(params != null) {
			for(int i=0;i<params.length;++i) {
				System.out.print(params[i]+" ");
			}
		}
		if(message.getMessage().equals(Commands.END)) end = true;
		if(!message.isAutomatic()) {
			for(GameInputListener listener : inputListeners) {
				listener.gotMessage(message);
			}
		}
		System.out.println();
	}
	
	/**
	 * A hostnak valo uzenetkuldesre valo.<br>
	 * @return sikerult-e elkuldeni
	 *  */
	public boolean sendMessage(GameMessage message) {
		message.setSender(userName);
		out.println(message);
		out.flush();
		return out.checkError();
	}
	
	public void addInputListener(GameInputListener listener) {
		inputListeners.add(listener);
	}
	
	public void removeInputListener(GameInputListener listener) {
		inputListeners.remove(listener);
	}
}
