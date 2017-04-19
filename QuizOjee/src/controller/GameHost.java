package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/* Tenyleges debug modhoz: Ctrl +F search: //System replace with ////System
 *                   majd: Ctrl +F search: //////System replace with //System
 */
/**<b>GameHost</b> osztaly.<p>
 * 
 * <b>Hasznalata:</b><br>
 * - hozz letre egy uj objektumot belole.<br>
 * - addolj egy {@link GameInputListener}t.<br>
 * - varj addig, amig mindenki csatlakozott. Ezt az {@link #isStarted()} metodussal tesztelheted.<br>
 * - {@link #broadCast} vagy {@link #sendMessage} metodusokkal kommunikalj a {@link GameClient}-ekkel.<br>
 * - vegen hivd meg az {@link #abort()} metodust.<p>
 * 
 * <b>Kilepes eseten:</b></br>
 * - az osszes {@link GameInputListener}-e kap egy {@link Commands#SOMEONE_LEFT} uzenetet.<p>
 * 
 * <b>Visszateres eseten:</b></br>
 * - az osszes {@link GameInputListener}-e kap egy {@link Commands#RETURNED} uzenetet.<br>
 * - varj egy keveset. (mert a returned uzenetet hamarabb kapod meg mint ahogy a visszatero kliens beallitja az inputlistenert)<br>
 * - annak a jatekosnak illene elkuldened a jatek allapotat egy {@link Commands#GAME} uzenettel.<p>
 */
public class GameHost {

	private List<GameInputListener> inputListeners = new ArrayList<GameInputListener>();
	private ServerSocket server;
	private Map<String,Socket> clients = new HashMap<String,Socket>();
	private Map<String,PrintWriter> outs = new HashMap<String,PrintWriter>();
	private Map<String,BufferedReader> ins = new HashMap<String,BufferedReader>();
	private Map<String,Boolean> alive = new HashMap<String,Boolean>();
	private final int maxPlayers = 3;
	private static final String host = "@host@";
	private boolean started = false;
	private boolean someoneExited = false;
	private boolean failedToStartServer = false;
	private boolean triedToOpenServer = false;
	private boolean end = false;
	private boolean joinedSuccessfully = false;

	/**
	 * Elindit egy uj szalat.<br> 
	 * Letrehoz egy szervert amihez {@link #maxPlayers} szamu jatekos csatlakozhat.<br>
	 * Ha csatlakoztak elegen automatikusan elindul.
	 * @throws IOException ha nem sikerult servert inditani.
	 */
	public GameHost() throws IOException {
		////System.err.println("GameHost");
		Thread t = new Thread(() -> {
			////System.err.println("GameHost-Thread");
			startHost();
			////System.err.println("GameHost-Thread END");
		}); 
		t.start();
		while(!triedToOpenServer);
		if(failedToStartServer) throw new IOException();
		////System.err.println("GameHost END");
	}
	
	/**
	 * Letrehoz egy szervert amihez {@link #maxPlayers} szamu jatekos csatlakozhat.<br>
	 * Elindit egy ping Timer-t.<br>
	 * Elindit egy refuser Timer-t<br>
	 * Var, amig az end be nem all.
	 * 
	 */
	private void startHost() {
		////System.err.println("StartHost");
		try {
			server = new ServerSocket(19969);
			triedToOpenServer = true;
			//System.out.println("Host: Server activated!");
			//clients[0] = new Socket("localhost",19969);
			while(!started && !end) {
				//System.out.println("Host: Not started yet!");
				while(clients.size() < maxPlayers && !end) {
					joinedSuccessfully = false; 
					Thread joiner = letItJoin();
					joiner.start();
					while(!joinedSuccessfully && !end) {Thread.sleep(100);};
					//Thread.sleep(1000);
				}
				started = true;
				//Thread.sleep(1000);
			}
			broadCast(new GameMessage(true,host,Commands.START));
			//System.out.println("Host: Let's freaking do this!!");
			////System.err.println("Ping");
			new Timer().schedule(new TimerTask() {
				public void run() {
					broadCast(new GameMessage(true,host,Commands.PING));
					if(end) {
						cancel();
						////System.err.println("Ping END");
					}
				}
			}, 10,100);
			////System.err.println("RefuserTimer");
			Timer refuser = refuser();
			while(!end) {Thread.sleep(100);}
			refuser.cancel();
			////System.err.println("RefuserTimer END");
		} catch(IOException e) {
			failedToStartServer = true;
			triedToOpenServer = true;
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			//System.out.println("Host: Finally...");
			for(String userName : clients.keySet()) {
				outs.get(userName).close();
				try {
					ins.get(userName).close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					clients.get(userName).close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				server.close();
			} catch (IOException e) {
				//System.err.println("Nem sikerult bezarni a servert!");
				e.printStackTrace();
			}
		}
		//System.out.println("StartHost vege");
		////System.err.println("StartHost END");
	}
	
	/**
	 * Az ujonnan erkezo kapcsolatokat kezeli.<br>
	 * Alapertelmezetten mindenkit elutasit {@link Commands#ALREADY_RUNNING} utasitassal<br>
	 * Ha valakit kidobott, akkor elfogadja. <br>
	 */	
	private Timer refuser() {
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				////System.err.println("refuser-Timer");
				Socket client = null;
				try {
					//System.out.println("Ha ki vagy te?!");
					client = server.accept();
					PrintWriter out = new PrintWriter(client.getOutputStream());
					out.println(new GameMessage(true,host,Commands.WHO_ARE_YOU));
					out.flush();
					if(someoneExited) {
						mayAccept(client,out);
					} else {
						refuse(client,out);
					}
				} catch (IOException e) {
					//e.printStackTrace();
					//System.err.println("Bezarodott, abbamarad a block");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				////System.err.println("refuser-Timer END");
			}
		},1,100);
		return t;
	}
	
	/**
	 * Elutasit {@link Commands#ALREADY_RUNNING} utasitassal.
	 */
	private void refuse(Socket badClient,PrintWriter out) throws IOException, InterruptedException {
		////System.err.println("refuse");
		//System.out.println("megvagy te szaros");
		out.println(new GameMessage(true,host,Commands.ALREADY_RUNNING));
		out.flush();
		out.close();
		try {
			badClient.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		////System.err.println("refuse END");
	}
	
	/** 
	 * Elfogadja a kapcsolatot, ha az bejelentkezett egy olyan nevvel, ami az indulasnal szerepelt.<br>
	 * Ilyenkor ujra erteket ad a clients, ins, outs, alive mapek megfelelo helyere.<br>
	 * Egyebkent elutasitja.
	 * 
	 */
	private void mayAccept(Socket potentialClient,PrintWriter out) throws IOException, InterruptedException {
		////System.err.println("mayAccept");
		BufferedReader in = new BufferedReader(new InputStreamReader(potentialClient.getInputStream()));
		//System.out.println("Na jo talan");
		while(!in.ready()) {Thread.sleep(10);};
		GameMessage msg = new GameMessage(in.readLine(),true);
		//System.out.println(msg.isAutomatic());
		String userName = msg.getSender();
		//System.out.println("Host: Ö: "+userName);
		if(msg.getMessage().equals(Commands.LOG_IN) && alive.containsKey(userName) && !alive.get(userName)) {
			broadCast(new GameMessage(Commands.RETURNED,userName));
			clients.put(userName,potentialClient);
			ins.put(userName,in);
			outs.put(userName,out);
			alive.put(userName, true);
			//System.out.println("Host: Welcome back "+ userName);
			out.println(new GameMessage(true,host,Commands.JOINED));
			out.println(new GameMessage(true,host,Commands.START));
			out.flush();
			for(int i=0;i<inputListeners.size();++i) {
				inputListeners.get(i).gotMessage(new GameMessage(false,host,Commands.RETURNED,userName));
			}
			boolean vaneMegAkiKilepett = false;
			for(String user : alive.keySet()) {
				if(!alive.get(user)) {
					vaneMegAkiKilepett = true;
					break;
				}
			}
			if(!vaneMegAkiKilepett) {
				someoneExited = false;
			}
			//System.out.println("vanMegAkiKilepett: "+vaneMegAkiKilepett);
		} else {
			refuse(potentialClient,out);
		}
		////System.err.println("mayAccept END");
	}
	
	
	/** 
	 * Megkezdi az input feldolgozasat az adott user-re.
	 * Csak akkor probal olvasni, ha eletben van a kapcsolat.
	*/
	private void startInputListener(String userName) {
		Thread thread = new Thread(() -> {
			////System.err.println("startInputListener "+ userName);
			try {
				while(!end) {
					Thread.sleep(100);
					while(alive.get(userName)) {
						if(end) break;
						while(!ins.get(userName).ready()) {
							Thread.sleep(100);
							if(end || !alive.get(userName)) break;
						}
						if(end || !alive.get(userName)) break;
						decode(ins.get(userName).readLine());
						Thread.sleep(100);
					}
				}
			} catch (Exception e) {
				//System.out.println("Exception by: the inputListener of "+userName);
				e.printStackTrace();
			}
			//System.out.println("InputListener vege");
			////System.err.println("startInputListener "+ userName +" END");
		});
		thread.start();
	}
	
	/**
	 * Megszakitja a {@link GameHost} mukodeset:<br>
	 *  - broadCast-ol egy {@link Commands#END} uzenetet<br>
	 *  - leallitja az osszes inputListener szalat.<br>
	 *  - bezarja a ping Timert.<br>
	 *  - bezarja az osszes kapcsolatot a kliensekkel.<br>
	 *  - bezarja a refuser szalat.
	 */
	public void abort() {
		broadCast(new GameMessage(false,host,Commands.END));
		end = true;
	}
	
	public void addInputListener(GameInputListener listener) {
		inputListeners.add(listener);
	}
	
	public void removeInputListener(GameInputListener listener) {
		inputListeners.remove(listener);
	}
	
	/** 
	 * Ha a bejovo uzenet automatikus:<br>
	 *  - akkor ertelmezi es vegrehajtja.<br>
	 *  - egyebkent {@link GameMessage}-t keszit a <b>message</b>-bol, es meghivja az osszed addolt  {@link GameInputListener}eit. 
	 */
	private void decode(String message) {
		////System.err.println("decode");
		if(message==null) {
			////System.err.println("decode END");
			return;
		}
		GameMessage msg = new GameMessage(message,true);
		//System.out.print("Host: "+ msg.getSender() + " said: " + msg.getMessage());
		String[] params = msg.getParams();
		for(int i=0;i<params.length;++i) {
			//System.out.print(params[i]+" ");
		}
		//System.out.println();
		if(!msg.isAutomatic()) {
			for(int i=0;i<inputListeners.size();++i) {
				inputListeners.get(i).gotMessage(msg);
			}
			return;
		}
		////System.err.println("decode END");
	}
	
	/**
	 * @return Egy uj szalat, ami a elfogadja az elso csatlakozot es beallitja mindenet.
	 */
	private Thread letItJoin() {
		return new Thread(() -> {
			////System.err.println("letItJoin-Thread");
			try {
				//System.out.println("Host: Let's try to catch!");
				Socket client = server.accept();
				//System.out.println("Host: Gotcha" + clients.size());
				PrintWriter out = new PrintWriter(client.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				out.println(new GameMessage(true,host,Commands.WHO_ARE_YOU));
				out.flush();
				while(!in.ready());
				GameMessage msg = new GameMessage(in.readLine(),true);
				//System.out.println("Host: Ö: "+msg.getSender());
				if(msg.getMessage().equals(Commands.LOG_IN)) {
					clients.put(msg.getSender(),client);
					ins.put(msg.getSender(),in);
					outs.put(msg.getSender(),out);
					alive.put(msg.getSender(), true);
				}
				startInputListener(msg.getSender());
				out.println(new GameMessage(true,host,Commands.JOINED));
				out.flush();
				joinedSuccessfully = true;
			} catch(IOException e) {
				//System.err.println("bezartak a hostot mielott csatlakozhattak volna");
				e.printStackTrace();
			}
			////System.err.println("letItJoin-Thread END");
		});
	}
	
	/** 
	 * <b>Kikuldi mindenkinek az uzenetet.</b><br>
	 * Ha nem sikerul:<br>
	 * - broadCastol egy uzenetet {@link Commands#SOMEONE_LEFT} parancssal es <b>userName</b> parameterrel.
	 * @param message egy {@link GameMessage} uzenet.*/
	public void broadCast(GameMessage message) {
		////System.err.println("BroadCast");
		List<String> leftUsers = new ArrayList<String>();
		for(String userName : clients.keySet()) {
			if(alive.get(userName)) {
				message.setSender(host);
				outs.get(userName).println(message);
				outs.get(userName).flush();
				if(outs.get(userName).checkError()) {
					//System.out.println("-- nem ment at: "+userName+" -> "+message);
					alive.put(userName, false);
					leftUsers.add(userName);
					someoneExited = true;
					if(!end) {
						for(int i=0;i<inputListeners.size();++i) {
							inputListeners.get(i).gotMessage(new GameMessage(false,host,Commands.SOMEONE_LEFT,userName));
						}
					}
				}
			}
		}
		for(String userName : leftUsers) {
			broadCast(new GameMessage(false,host,Commands.SOMEONE_LEFT,userName));
		}
		////System.err.println("BroadCast END");
	}
	
	/** <b>Elkuldi az uzenetet.</b><br>
	 * Ha nem sikerul:<br>
	 * -- broadCastol egy uzenetet {@link Commands#SOMEONE_LEFT} parancssal es userName parameterrel.<br>
	 * @param userName neki kuldi.
	 * @param message egy {@link GameMessage} uzenet.*/
	public void sendMessage(String userName, GameMessage message) {
		message.setSender(host);
		outs.get(userName).println(message);
		outs.get(userName).flush();
		if(outs.get(userName).checkError()) {
			alive.put(userName, false);
			someoneExited = true;
			broadCast(new GameMessage(false,host,Commands.SOMEONE_LEFT,userName));
			if(!end) {
				for(int i=0;i<inputListeners.size();++i) {
					inputListeners.get(i).gotMessage(new GameMessage(false,host,Commands.SOMEONE_LEFT,userName));
				}
			}
		}
	}
	
	/** Visszaadja a jatekosok halmazat.*/
	public Set<String> getUserNames() {
		return clients.keySet();
	}
	
	/** Elindult-e a jatek, azaz, mindenki csatlakozott es elindult a host.*/
	public boolean isStarted() {
		return started;
	}
	
}
