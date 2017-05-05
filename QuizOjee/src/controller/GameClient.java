package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import controller.exceptions.GameIsStartedException;
import controller.exceptions.HostDoesNotExistException;
import game.StringSerializer;

/**
 * <b>GameClient</b> osztaly.
 * <p>
 * <b>Hasznalata:</b> <br>
 * - hozz letre egy uj objektumot belole.<br>
 * - addolj egy {@link GameInputListener}-t.<br>
 * - {@link #start} <br>
 * - varj addig, amig mindenki csatlakozott. Ezt az {@link #isStarted()}
 * metodussal tesztelheted.<br>
 * - {@link #sendMessage}-el kommunikalsz a {@link GameHost}-al<br>
 * - nem kotelezo {@link #abort()}-olnod, ha a host leall, automatikusan
 * megteszi.
 * <p>
 * 
 * <b>Kilepes eseten:</b> <br>
 * - ha hiba tortent az elkuldesnel, akkor tonkrement a kapcsolat.<br>
 * - ilyenkor {@link #abort()}-old a kapcsolatot.<br>
 * - varj egy masodpercet. <br>
 * - hozz letre egy uj <b>GameClient</b>-et.<br>
 * - az uj kliensnek erdemes elkuldeni a jatek allapotat egy
 * {@link Commands#GAME} uzenettel.
 * <p>
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
	private ConnectException connectException = null;
	private boolean started = false;
	private int waitingTime = 1000;

	/**
	 * Letrehoz egy {@link GameClient} objektumot. <br>
	 * A foprogram max - waitingTime - ideig varakozik, ezutan 3 lehetoseg van:
	 * <p>
	 * - Sikeres volt a csatlakozas, nem dobott hibat.<br>
	 * - {@link GameIsStartedExeption}-t dob, ha elutasitotta a host.<br>
	 * - {@link HostDoesNotExistException}-t dob, ha nem valaszolt a host....
	 * [van meg leiras]
	 * <p>
	 * Kozvetetten elinditja az inputListenert egy uj szalon.
	 */
	public GameClient() {
	}

	public void start(String ip, String userName) throws GameIsStartedException, HostDoesNotExistException, ConnectException {
		this.userName = userName;
		Thread t = new Thread(() -> {
			buildConnection(ip, userName);
		});
		t.start();
		while (!initialized && !failedToConnect && !gameIsStarted && !end && connectException != null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (failedToConnect)
			throw new HostDoesNotExistException();
		if (gameIsStarted)
			throw new GameIsStartedException();
		if (connectException != null)
			throw connectException;
		// else //System.out.println(userName + ": initialized ");
	}

	/** Leallitja az inputListener szalat, leallitja a klienst */
	public void abort() {
		end = true;
		//// System.out.println(userName + ": Akkor en most leallok");
	}

	/**
	 * Felepiti a kapcsolatot es beallitja a megfelelo valtozot:<br>
	 * - {@link #initialized}, ha minden sikeres volt.<br>
	 * - {@link #gameIsStarted}, ha elutasitottak<br>
	 * - {@link #failedToConnect}, ha nem valaszolt a host<br>
	 * Elinditja az inputListenert.
	 */
	private void buildConnection(String ip, String userName) {
		try {
			// System.out.println(userName + ": ok.. let's try this!");
			server = new Socket(ip, 19969);
			in = new BufferedReader(new InputStreamReader(server.getInputStream()));
			out = new PrintWriter(server.getOutputStream());
			Timer t = letsWait(waitingTime);
			while (!in.ready() && !tooMuchWaiting) {
				Thread.sleep(10);
			}
			t.cancel();
			if (!tooMuchWaiting) {
				String asd = in.readLine();
				GameMessage msg = (GameMessage)StringSerializer.deSerialize(asd);
				// System.out.println(userName +": Host said:
				// "+msg.getMessage());
				if (msg.getMessage().equals(Commands.WHO_ARE_YOU)) {
					sendMessage(new GameMessage(true, userName, Commands.LOG_IN, userName));
					while (!in.ready()) {
						Thread.sleep(10);
					}
					String predicate = in.readLine();
					msg = (GameMessage)StringSerializer.deSerialize(predicate);
					// System.out.println(userName +": Host said: "+
					// msg.getMessage());
					if (msg.getMessage().equals(Commands.JOINED)) {
						initialized = true;
						startInputListener();
						// ITT UGYSEM JOSSZ AT!!!
					} else if (msg.getMessage().equals(Commands.ALREADY_RUNNING)) {
						// System.out.println(userName + ": Oh no I came too
						// late!");
						gameIsStarted = true;
					}
				}
			} else {
				// System.out.println(userName + ": Oh no I came too late!");
				failedToConnect = true;
			}

		} catch (UnknownHostException e) {
			abort();
			// System.out.println(userName + ": You can't reach the server");
			e.printStackTrace();
		} catch (ConnectException e) {
			// System.err.println(userName + ": Something went wrong :P");
			e.printStackTrace();
			connectException = e;
		} catch (IOException e) {
			// System.err.println(userName + ": Something went wrong :P");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// System.err.println("nincs pihi");
			e.printStackTrace();
		} catch (Exception e) {
			// System.err.println(userName + " : valami elrontodott.");
			e.printStackTrace();
		} finally {
			// System.out.println(userName+": Finally...");
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
		// System.out.println("BuildConnection vege - "+userName);
	}

	/**
	 * @param time
	 *            - ennyi ido utan beallitja a tooMuchWaiting-t true-ra
	 */
	private Timer letsWait(int time) {
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				tooMuchWaiting = true;
				// System.out.println("IT'S TIME");
			}
		}, time);
		return t;
	}

	/**
	 * Ez az eljaras felelos az input beolvasasaert.<br>
	 * Vege lesz, ha az {@link #end}-et true-ra allitjuk.
	 */
	private void startInputListener() {
		while (!end) {
			try {
				while (!in.ready()) {
					Thread.sleep(100);
					if (end)
						break;
				}
				if (end)
					break;
				String msg = in.readLine();
				decode(msg);
			} catch (IOException e) {
				// System.out.println(userName + ": Mar figyelni se lehet
				// rendesen?!");
				e.printStackTrace();
			} catch (InterruptedException e) {
				// System.err.println(userName + ": Mar aludni se lehet
				// rendesen?!");
				e.printStackTrace();
			}
		}
		// System.out.println("InputListener vege - "+ userName + " client");

	}

	/**
	 * A bejovo kodolt uzenetbol {@link GameMessage}-t hoz letre, es ha az
	 * automatic:<br>
	 * - akkor ertelmezi, es vegrehajtja. - kulonben meghivja az osszes
	 * {@link GameInputListener#gotMessage(GameMessage)} metodusat.
	 */
	private void decode(String msg) {
		if (msg == null)
			return;
		GameMessage message = (GameMessage)StringSerializer.deSerialize(msg);
		if (message.getMessage().equals(Commands.PING))
			return;
		// System.out.print(userName + ": "+ message.getSender() +" said: ");
		// System.out.print(message.getMessage());
		String[] params = message.getParams();
		if (params != null) {
			for (int i = 0; i < params.length; ++i) {
				// System.out.print(params[i]+" ");
			}
		}
		if (message.getMessage().equals(Commands.END))
			end = true;
		if (message.getMessage().equals(Commands.START)) {
			sendMessage(new GameMessage(true, userName, Commands.IM_LISTENING));
			System.out.println("I'm happy! (I'm listening)");
			started = true;
		}
		if (!message.isAutomatic()) {
			for (GameInputListener listener : inputListeners) {
				listener.gotMessage(message);
			}
		}
		// System.out.println();
	}

	/**
	 * A hostnak valo uzenetkuldesre valo.<br>
	 * 
	 * @param message
	 *            egy {@link GameMessage} uzenet.
	 * @return sikerult-e elkuldeni.
	 */
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

	/** Elindult-e a jatek. */
	public boolean isStarted() {
		return started;
	}

}
