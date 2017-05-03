package view.states;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import javax.print.DocFlavor.STRING;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

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
import model.Question;
import view.Labels;
import view.MainWindow;
import view.Settings;
import view.components.DialogQuestion;
import view.components.GButton;
import view.components.GButtonUI;
import view.components.GLabel;

/**
 * Ez az osztaly a jatek allapotat tartalmazza. sorban megjatszatja a
 * jatekosokat es kirajzolja a jatekot a kepernyore
 * 
 * @author ganter
 */
public class GameState extends State implements GameInputListener {
	//-----------------------------------------//
	//  SERVER-CLIENT COMMUNICATION			   //
	//-----------------------------------------//
	private GameClient client;
	private String interrupt = "";
	private Thread interruptThread;
	private GameMessage lastMsg = null;
	private Stack<GameMessage> msgStack = new Stack<>();
	private String uname;
	
	//-----------------------------------------//
	//  GAME - GUI                             //
	//-----------------------------------------//
	MainWindow root;
	private DialogQuestion qDialog;
	private GButton selectedQButton = new GButton();
	private GButtonUI baseUI = new GButtonUI(
			Settings.color_GButton_inGame, 
			Settings.color_GButtonHover_inGame, 
			Settings.color_GButtonClick_inGame, 
			Settings.FONT_GBUTTON_DEFAULT, 
			Settings.color_GButtonFont_inGame
			);
	private GButtonUI selectedUI;
	private GButtonUI successUI = new GButtonUI(
			Settings.color_success, 
			Settings.color_success.brighter(), 
			Settings.color_success.brighter(), 
			Settings.FONT_GBUTTON_DEFAULT, 
			Settings.color_GButtonFont_inGame
			);
	private ActionListener onClick = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			qDialog.btnAnswerA.removeActionListener(onClick);
			qDialog.btnAnswerB.removeActionListener(onClick);
			qDialog.btnAnswerC.removeActionListener(onClick);
			qDialog.btnAnswerD.removeActionListener(onClick);
			selectedQButton.setUI(baseUI);
			GButton btn = (GButton) e.getSource();
			selectedQButton = btn;
			btn.setUI(selectedUI);
			System.out.println("button clicked, sendin message");
			client.sendMessage(new GameMessage(Commands.NORM_ANSWER, selectedQButton.getText()));
		}
	};
	
	//-----------------------------------------//
	//  GAME - LOGIC                           //
	//-----------------------------------------//
	public boolean gameOver;
	private boolean gameStarted;
	private boolean respondingToInput = true;
	
	private GameSettings settings;
	private GameBoard gameboard;
	private Timer countDownTimer = new Timer();
	private Thread playerThread;
	private Thread questionTh = null; 
	private Player player;
	
	//-----------------------------------------//
	//  CURRENT MOVE                           //
	//-----------------------------------------//
	private Player attacker;
	private String attackerUname;
	private Player defender;
	private String defenderUname;
	private Territory target;
	Set<Territory> neighbors = new TreeSet<>(); //ezt a kijelölhetőség teszteléséhez tudni kell az update-ben
	private boolean myTurn = false;
	private String currentPlayer;
	
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
		qDialog = new DialogQuestion();
	}

	private void gameOver() {
		System.out.println("GAME OVER!");
	}

	@Override
	public void onStart() {
		System.out.println("starting gamestate ");
		gameOver = false;
		gameStarted = false;
		uname = MainWindow.getInstance().getLoggedUser().getUsername();
		interruptThread =  new Thread() {
			public void run() {
				try {
					Thread.currentThread().setName("GameClient");
					System.out.println("client booting...");
					System.out.println(uname +"connecting to" + Settings.gameServer);
					client = new GameClient();
					client.addInputListener(GameState.this);
					client.start(Settings.gameServer, uname);
					while (!client.isStarted()) {
						System.out.println(uname + ": waiting for others...");
						Thread.sleep(1000);
					};
					System.out.println("	[GO]   "+uname + "done waiting");
					client.sendMessage(new GameMessage(Commands.IS_STARTED));
					waitForMsg(Commands.START);
					
					client.sendMessage(new GameMessage(Commands.SETTINGS_REQUEST));
					manageSettings(waitForMsg(Commands.SETTINGS));

					client.sendMessage(new GameMessage(Commands.GAMEBOARD_REQUEST));
					manageGameboard(waitForMsg(Commands.GAMEBOARD));
					
					gameStarted = true;

					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (GameIsStartedException e) {
					JOptionPane.showMessageDialog(root, Labels.MSG_GAME_STARTED, Labels.MSG_SERVER_ERROR, JOptionPane.ERROR_MESSAGE);
					root.setState(MainWindow.STATE_MAIN);
				} catch (HostDoesNotExistException e) {
					JOptionPane.showMessageDialog(root, Labels.MSG_BAD_IP_ADDRESS, Labels.MSG_SERVER_ERROR, JOptionPane.ERROR_MESSAGE);
					root.setState(MainWindow.STATE_MAIN);
				} finally {
				}
			}
		};
		interruptThread.start();
	}
	
	@Override
	public void onStop() {
		gameOver= true;
		client.abort();
	}
	
	public void manageNewOwner(GameMessage msg) {
		String newOwner = msg.getParams()[0];
		int terrId = Integer.parseInt(msg.getParams()[1]);
		Territory terr = gameboard.getTerrytoryById(terrId);
		for(Player p : settings.PLAYERS){
			if(newOwner.equals(p.getUser().getUsername())){
				terr.setOwner(p);
				return;
			}
		}
		calcNeighbors();
	}
	public void manageAttack(GameMessage msg) {
		attacker = settings.getPlayerByUname(msg.getParams()[0]);
		attackerUname = attacker.getUser().getUsername();
		target = gameboard.getTerrytoryById( Integer.parseInt(msg.getParams()[1]) );
		defender = target.getOwner();
		defenderUname = defender.getUser().getUsername();
	}
	public void manageNormQuestion(GameMessage msg) {
		boolean isItMine = msg.getParams()[0].equals(Commands.PARAM_YOURS);
		Question question = (Question) StringSerializer.deSerialize(msg.getParams()[1]);
		questionTh = Thread.currentThread();
		String ans;
		if(isItMine){
			ans = displayNormalQuestionAndGetAnswer(question, attackerUname, defenderUname);
		} else {
			displayNormalQuestion(question, attackerUname, defenderUname);
		}
	}
	public void manageNormQuestionRightAnswer(GameMessage msg) {
		String rightAns = msg.getParams()[0];
		//helyes valasz bejelolese
		if(qDialog.btnAnswerA.getText().equals(rightAns)){
			qDialog.btnAnswerA.setUI(successUI);
		} else if(qDialog.btnAnswerB.getText().equals(rightAns)){
			qDialog.btnAnswerB.setUI(successUI);
		} else if(qDialog.btnAnswerC.getText().equals(rightAns)){
			qDialog.btnAnswerC.setUI(successUI);
		} else if(qDialog.btnAnswerD.getText().equals(rightAns)){
			qDialog.btnAnswerD.setUI(successUI);
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (questionTh != null) questionTh.interrupt();
	}
	public void manageNormQuestionPlayerAnswer(GameMessage msg) {
		String uname = msg.getParams()[1];
		String rightAns = msg.getParams()[0];
		Player pl = settings.getPlayerByUname(uname);
		GButtonUI playerUI = new GButtonUI(
				pl.getColor(), 
				pl.getColor().brighter(), 
				pl.getColor().brighter(), 
				Settings.FONT_GBUTTON_DEFAULT, 
				Settings.color_GButtonFont_inGame
		);
		if(qDialog.btnAnswerA.getText().equals(rightAns)){
			qDialog.btnAnswerA.setUI(playerUI);
		} else if(qDialog.btnAnswerB.getText().equals(rightAns)){
			qDialog.btnAnswerB.setUI(playerUI);
		} else if(qDialog.btnAnswerC.getText().equals(rightAns)){
			qDialog.btnAnswerC.setUI(playerUI);
		} else if(qDialog.btnAnswerD.getText().equals(rightAns)){
			qDialog.btnAnswerD.setUI(playerUI);
		}
	}
	public void manageRaceQuestion(GameMessage msg) {
		
	}
	public void manageGameboard(GameMessage msg) {
		System.out.println("recieved gameboard");
		gameboard = (GameBoard) StringSerializer.deSerialize(msg.getParams()[0]);
		calcNeighbors();
	}
	public void manageSettings(GameMessage msg) {
		settings = (GameSettings) StringSerializer.deSerialize(msg.getParams()[0]);		
		System.out.println("recieved settings");
		player = settings.getPlayerByUname(uname);
		selectedUI = new GButtonUI(
				player.getColor(), 
				player.getColor().brighter(), 
				player.getColor().brighter(), 
				Settings.FONT_GBUTTON_DEFAULT, 
				Settings.color_GButtonFont_inGame
		);
	}
	public void manageYourTurn(GameMessage msg) {
		currentPlayer = msg.getParams()[0];
		if(uname.equals(currentPlayer)) {
			myTurn = true;
		}
	}
	public void manage(GameMessage msg) {
		
	}
	
	@Override
	public void gotMessage(GameMessage msg) {
		System.out.println("client GotMessage:" +msg.toString());
		synchronized(msgStack){
			msgStack.push(msg);			
		}
		
		if(msg.getMessage().equals(interrupt)) {
			System.out.println("interrupt-watcher caught the message" );
			interrupt = "";
			lastMsg = msg;
			interruptThread.interrupt();
			//when we wait for something we handle the message separately
			return; 
		};
		if(!gameStarted) return;
		new Thread(){
			@Override
			public void run(){
				Thread.currentThread().setName("client-gotMessage");
				if(msg.getMessage().equals(Commands.NEW_OWNER)) {
					synchronized(msgStack){ msgStack.remove(msg); }
					manageNewOwner(msg);
				} else if(msg.getMessage().equals(Commands.ATTACK)){
					synchronized(msgStack){ msgStack.remove(msg); }
					manageAttack(msg);
				} else if(msg.getMessage().equals(Commands.NORMAL_QUESTION)){
					synchronized(msgStack){ msgStack.remove(msg); }
					manageNormQuestion(msg);					
				} else if(msg.getMessage().equals(Commands.NORM_ANSWER)){
					synchronized(msgStack){ msgStack.remove(msg); }
					manageNormQuestionRightAnswer(msg);
				} else if(msg.getMessage().equals(Commands.NORM_PLAYER_ANSWER)){
					synchronized(msgStack){ msgStack.remove(msg); }
					manageNormQuestionPlayerAnswer(msg);
				} else if(msg.getMessage().equals(Commands.GAMEBOARD)){
					synchronized(msgStack){ msgStack.remove(msg); }
					manageGameboard(msg);
				} else if(msg.getMessage().equals(Commands.SETTINGS)){
					synchronized(msgStack){ msgStack.remove(msg); }
					manageSettings(msg);
				} else if(msg.getMessage().equals(Commands.SETTINGS)){
					synchronized(msgStack){ msgStack.remove(msg); }
					manageYourTurn(msg);
				}
			}
		}.start();
		
	}
	
	public void displayNormalQuestion(Question quest, String attacker, String defender){
		qDialog.btnAnswerA.setUI(baseUI); 
		qDialog.btnAnswerB.setUI(baseUI); 
		qDialog.btnAnswerC.setUI(baseUI); 
		qDialog.btnAnswerD.setUI(baseUI); 
		qDialog.btnAnswerA.setText(quest.getAnswer1());
		qDialog.btnAnswerB.setText(quest.getAnswer2());
		qDialog.btnAnswerC.setText(quest.getAnswer3());
		qDialog.btnAnswerD.setText(quest.getRightAnswer());
		qDialog.lblQuestionText.setText("<html><body style='margin:10px;'>" +quest.getQuestion()+"</body></html>");
		qDialog.lblNorth.setText("<html><body style='margin:10px;'>" + attacker + " vs " + defender);
		qDialog.lblNorth.setHorizontalAlignment(SwingConstants.CENTER);				
		TimerTask countdown = displayCountdown(qDialog.lblSouth, settings.questionTime);
		qDialog.setLocationRelativeTo(MainWindow.getInstance());
		qDialog.setVisible(true);
		respondingToInput = false;
		try {
			Thread.sleep(settings.questionTime + 2000);
		} catch (InterruptedException e) {
			//everyone answered early
		}
		countdown.cancel();
		respondingToInput = true;
		qDialog.setVisible(false);
	}

	public String displayNormalQuestionAndGetAnswer(Question quest, String attacker, String defender){
		qDialog.btnAnswerA.addActionListener(onClick);
		qDialog.btnAnswerB.addActionListener(onClick);
		qDialog.btnAnswerC.addActionListener(onClick);
		qDialog.btnAnswerD.addActionListener(onClick);
		
		displayNormalQuestion(quest, attacker, defender);
		
		qDialog.btnAnswerA.removeActionListener(onClick);
		qDialog.btnAnswerB.removeActionListener(onClick);
		qDialog.btnAnswerC.removeActionListener(onClick);
		qDialog.btnAnswerD.removeActionListener(onClick);
		return selectedQButton.getText();
	}

	/**
	 * This function displays a timer countdown onto the specified {@link JLabel}. 
	 * The labels original content is erased. The method creates a new {@link TimerTask} and registers it on the 
	 * {@link #countDownTimer}, the coundown has second precision.
	 * @param label target {@link JLabel}
	 * @param ms number of milliseconds to count
	 * @return {@link TimerTask} the timertask that was registered. It can be cancelled calling {@link TimerTask#cancel()} 
	 */
	public TimerTask displayCountdown(GLabel label, int ms){
		int interval = 1000; 	
		TimerTask task = new TimerTask() {
			int msLeft = ms - interval;
			@Override
			public void run() {
				label.setText("<html><center>" + String.valueOf(msLeft/1000) +"</center></html>");
 				if (msLeft < 0){
					cancel();
				}
				msLeft -= interval; //the zeroeth second counts too! 
				
			}
		};
		countDownTimer.schedule(task, 0, interval);
		return task;
	}
	public void calcNeighbors() {
		List<Territory> own = player.getTerritories();
		neighbors.clear();
		for(Territory t : gameboard.territories) {
			if(t.getOwner().getUser().getUsername().equals(uname)) continue;
			for(Territory tp : own) {
				tp.highlight();
				if(t.isNeighbor(tp, gameboard)) {
					neighbors.add(t);
					break;
				}
			}	
		}
	}
	
	@Override
	public void render() {
		if(!gameStarted) return;
		if (ticks % 15 == 1)
			redraw();
			gameboard.render(g);	
		
		//print info
			g.setFont(Settings.FONT_DEFAULT);
			

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
		g.fillRect(0, 0, settings.GAME_WIDTH, settings.GAME_INFOLABEL_HEIGHT);
		g.setColor(new Color(210, 210, 210));
		g.fillRect(0, 0, settings.GAME_WIDTH, settings.GAME_HEIGHT);
		// mark all territories to be repainted
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
		
		if(respondingToInput){
			if (inputManager.isKeyTyped("Enter")) {
				myTurn=false;
				client.sendMessage(new GameMessage(Commands.END_TURN));
			}
			if (inputManager.isClicked("ButtonLeft") && !gameOver) {
				if(!gameStarted) return;
				Territory lit = gameboard.getHighlitTerritory();
				if (lit.equals(Territory.NULL_TERRITORY)) return; //ezt sehogse támadjuk!
				client.sendMessage(new GameMessage(
						Commands.ATTACK, 
						player.getUser().getUsername(), 
						String.valueOf(lit.id)
				));
				System.out.println("leftclicked, sent attack from "+ player.getUser().getUsername() +" to " +gameboard.getHighlitTerritory().id);
				
			}
	
			try {
				synchronized (gameboard) {
					boolean valid = true;
					Territory toBeLit = gameboard.fromPixel(inputManager.getMousePos().x, inputManager.getMousePos().y).getOwner();
//					Territory toBeLit = gameboard.getHighlitTerritory();
					if (toBeLit.equals(Territory.NULL_TERRITORY)) {
						valid = false; //ezt ne támadjuk... 
//					} else if(valid && lit.getOwner().getTeam() == player.getTeam()) {
					} else if(valid && toBeLit.getOwner().getUser().getUsername().equals(uname)) {
						valid = false; //magunkat ne támadjuk
					} else if(valid && !neighbors.contains(toBeLit)) {
						valid = false; //ne támadjunk olyat aki nem szomszéd
					}
				
					if(valid) {
						//if not valid for attack highlight the territory under the cursor
						gameboard.setHighlitCell(gameboard.fromPixel(inputManager.getMousePos().x, inputManager.getMousePos().y));
					} else {
						//if not valid for attack dont highlight anything
						gameboard.setHighlitCell(gameboard.fromPixel(-10, -10));
					}
				}
				
			} catch (NullPointerException ignore) {
			}
		}

	}

	/**
	 * Hangs the thread until the server sends a certain command.
	 * Calling this method will also skip any eventhandling 
	 * code for the first message of specified Command in {@link #gotMessage(GameMessage)}
	 * @param command
	 * @return the first {@link GameMessage} of type command that was recieved 
	 */ 
	public GameMessage waitForMsg(String command){
		
		try {
			//check if msg was recieved already
			synchronized (msgStack) {
				for (Iterator<GameMessage> iterator = msgStack.iterator(); iterator.hasNext();) {
					GameMessage gm = iterator.next();
					if(gm.getMessage().equals(command)){
						iterator.remove();
						System.out.println( command + " pulled from stack");
						return gm;
					}
				}
			
				interruptThread = Thread.currentThread();
			
				interrupt = command;
			}
			while (true){
				System.out.println("client waiting for " + command);
				Thread.sleep(10000);
			}
		} catch (InterruptedException e) {
			GameMessage ret = lastMsg;
			lastMsg = null;
			System.out.println("client reciewed " + command);
			return ret;
		}
	}
}
