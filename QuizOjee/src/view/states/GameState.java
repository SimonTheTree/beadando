package view.states;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.print.DocFlavor.STRING;

import controller.Commands;
import controller.GameClient;
import controller.GameInputListener;
import controller.GameMessage;
import controller.exceptions.GameIsStartedException;
import controller.exceptions.HostDoesNotExistException;
import game.Territory;
import game.Cell;
import game.GameBoard;
import game.GameSettings;
import game.MyClientListener;
import game.StringSerializer;
import game.players.Player;
import gameTools.map.Layout;
import gameTools.map.Orientation;
import gameTools.state.State;
import view.MainWindow;
import view.Settings;

/**
 * Ez az osztály a játék állapotát tartalmazza. sorban megjátszatja a
 * játékosokat és kirajzolja a játékot a képernyőre
 * 
 * @author ganter
 */
public class GameState extends State {
	MainWindow root;
	private GameState THIS = this;
	private boolean gameStarted;
	public boolean gameOver;
	GameBoard gameboard;
	Thread playerThread;
	GameSettings settings;

	MyClientListener clientListener;
	Thread clientThread;
	
	public GameState(MainWindow r) {
		super(MainWindow.STATE_GAME, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;

		inputManager.addKeyMapping("ESC", KeyEvent.VK_ESCAPE);
		inputManager.addKeyMapping("Enter", KeyEvent.VK_ENTER);
		inputManager.addKeyMapping("debug", KeyEvent.VK_F1);

		inputManager.addClickMapping("ButtonLeft", MouseEvent.BUTTON1);

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHints(rh);
	}

	

	private void gameOver() {
		System.out.println("GAME OVER!");
	}

	@Override
	public void onStart() {
		System.out.println("starting gamestate ");
		gameOver = false;
		gameStarted = false;
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		clientThread =  new Thread() {
			public void run() {
				GameClient client = null;
				try {
					System.out.println("client booting...");
					String uname = MainWindow.getInstance().getLoggedUser().getUsername();
					clientListener = new MyClientListener(this);
					System.out.println(uname +"connecting to" + Settings.gameServer);
					client = new GameClient(Settings.gameServer, uname);
					client.addInputListener(clientListener);
					while (!client.isStarted()) {
						System.out.println(uname + ": waiting for others...");
						Thread.sleep(100);
					};
					System.out.println("	[GO]   "+uname + "done waiting");
					
					String s = clientListener.waitForMsg(Commands.SETTINGS).getParams()[0];
					settings = (GameSettings) StringSerializer.deSerialize(s);
					System.out.println("recieved settings");
					
					s = clientListener.waitForMsg(Commands.GAMEBOARD).getParams()[0];
					gameboard= (GameBoard) StringSerializer.deSerialize(s);
					System.out.println("recieved gameboard");
					
					gameStarted = true;

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (GameIsStartedException e) {
					e.printStackTrace();
				} catch (HostDoesNotExistException e) {
					e.printStackTrace();
				}
			}
		};
		clientThread.start();
	}

	@Override
	public void onStop() {
		gameOver = true;
	}

	@Override
	public void render() {
		if(!gameStarted) return;
		if (ticks % 15 == 1)
			redraw();
		gameboard.render(g);

		if (settings.dbg) {
			// fps
			g.setColor(new Color(230, 230, 230));
			g.fillRect(width - 80, height - 15, width, height);

			String s = fpsCounter.fps() + " fps";
			int rightJustifiedBase = width - 3;
			g.setFont(new Font("Courier New", Font.PLAIN, 13));
			int stringWidth = g.getFontMetrics().stringWidth(s);
			int x = rightJustifiedBase - stringWidth;

			g.setColor(Color.WHITE);
			g.drawString(s, x, height - 3);
		}

	}

	private void redraw() {
		// clear screen
		g.setColor(new Color(230, 230, 230));
		g.fillRect(0, 0, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		g.setColor(new Color(210, 210, 210));
		g.fillRect(0, 0, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		// mark all cells to be repaired
		for (Territory t : gameboard.territories) {
			t.touch();
		}
		// for(int i = 0; i < COLORS.length+1; i++){
		// g.drawImage(ATTACK_ICON[i], i*50, GameSettings.GAME_HEIGHT,45,45,this);
		// }
		gameboard.needsRender = true;
	}

	@Override
	public void update() {
		if (inputManager.isKeyTyped("ESC")) {
			MainWindow.getInstance().setState(MainWindow.STATE_MAIN);
		}
		if (inputManager.isKeyTyped("debug")) {
			settings.dbg = !settings.dbg;
			if (settings.dbg) {
				System.out.println("Debugging On...");
			} else {
				System.out.println("Debugging Off...");
			}
		}

		try {
			gameboard.setHighlitCell(gameboard.fromPixel(inputManager.getMousePos().x, inputManager.getMousePos().y));
		} catch (NullPointerException ignore) {
		}

	}
}
