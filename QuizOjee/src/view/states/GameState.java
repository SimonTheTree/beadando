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
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.print.DocFlavor.STRING;
import javax.swing.JOptionPane;

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

/**
 * Ez az osztaly a jatek allapotat tartalmazza. sorban megjatszatja a
 * jatekosokat es kirajzolja a jatekot a kepernyore
 * 
 * @author ganter
 */
public class GameState extends State implements GameInputListener {
	MainWindow root;
	private GameClient client;
	
	private boolean gameStarted;
	public boolean gameOver;
	private GameBoard gameboard;
	private Thread playerThread;
	private GameSettings settings;

	private String interrupt = "";
	private Thread interruptThread;
	private GameMessage lastMsg = null;
	private Stack<GameMessage> msgStack = new Stack<>();
	
	private DialogQuestion qDialog;
	private GButton selectedQButton = new GButton();
	private Thread questionTh = null; 
	private Player player;
	
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
			client.sendMessage(new GameMessage(Commands.NORM_ANSWER, selectedQButton.getText()));
		}
	};
	
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
		interruptThread =  new Thread() {
			public void run() {
				try {
					Thread.currentThread().setName("GameClient");
					System.out.println("client booting...");
					String uname = MainWindow.getInstance().getLoggedUser().getUsername();
					System.out.println(uname +"connecting to" + Settings.gameServer);
					client = new GameClient();
					client.addInputListener(GameState.this);
					client.start(Settings.gameServer, uname);
					while (!client.isStarted()) {
						System.out.println(uname + ": waiting for others...");
						Thread.sleep(1000);
					};
					System.out.println("	[GO]   "+uname + "done waiting");
					
					String s = waitForMsg(Commands.SETTINGS).getParams()[0];
					settings = (GameSettings) StringSerializer.deSerialize(s);
					System.out.println("recieved settings");
					for(Player p : settings.PLAYERS){
						System.out.println(p.getUser().getUsername());
						if(uname.equals(p.getUser().getUsername())){
							player = p;
							break;
						}
					}
					selectedUI = new GButtonUI(
							player.getColor(), 
							player.getColor().brighter(), 
							player.getColor().brighter(), 
							Settings.FONT_GBUTTON_DEFAULT, 
							Settings.color_GButtonFont_inGame
					);
					
					s = waitForMsg(Commands.GAMEBOARD).getParams()[0];
					gameboard= (GameBoard) StringSerializer.deSerialize(s);
					System.out.println("recieved gameboard");
					
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
	public void gotMessage(GameMessage msg) {
		System.out.println("client GotMessage:" +msg.getMessage());
		msgStack.push(msg);
		
		if(msg.getMessage().equals(interrupt)) {
			interrupt = "";
			lastMsg = msg;
			interruptThread.interrupt();
		};
		new Thread(){
			@Override
			public void run(){
				if(msg.getMessage().equals(Commands.NEW_OWNER)) {
					msgStack.pop();
					String newOwner = msg.getParams()[0];
					int terrId = Integer.parseInt(msg.getParams()[1]);
					Territory terr = gameboard.getTerrytoryById(terrId);
					for(Player p : settings.PLAYERS){
						if(newOwner.equals(p.getUser().getUsername())){
							terr.setOwner(p);
							return;
						}
					}
				} else if(msg.getMessage().equals(Commands.NORMAL_QUESTION)){
					boolean isItMine = msg.getParams()[0].equals(Commands.PARAM_YOURS);
					Question question = (Question) StringSerializer.deSerialize(msg.getParams()[1]);
					questionTh = Thread.currentThread();
					String ans;
					if(isItMine){
						ans = displayNormalQuestionAndGetAnswer(question);
					} else {
						displayNormalQuestion(question);
					}
					
				} else if(msg.getMessage().equals(Commands.NORM_ANSWER)){
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
				} else if(msg.getMessage().equals(Commands.NORM_PLAYER_ANSWER)){
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
			}
		}.start();
		
	}
	
	public void displayNormalQuestion(Question quest){
		qDialog.btnAnswerA.setUI(baseUI); 
		qDialog.btnAnswerB.setUI(baseUI); 
		qDialog.btnAnswerC.setUI(baseUI); 
		qDialog.btnAnswerD.setUI(baseUI); 
		qDialog.btnAnswerA.setText(quest.getAnswer1());
		qDialog.btnAnswerB.setText(quest.getAnswer2());
		qDialog.btnAnswerC.setText(quest.getAnswer3());
		qDialog.btnAnswerD.setText(quest.getRightAnswer());
		qDialog.lblQuestionText.setText("<html><body style='margin:10px;'>" +quest.getQuestion());
		qDialog.setVisible(true);
//		qDialog.requestFocus();
		try {
			Thread.sleep(settings.questionTime);
		} catch (InterruptedException e) {
			//everyone answered early
		}
		qDialog.setVisible(false);
	}

	public String displayNormalQuestionAndGetAnswer(Question quest){
		displayNormalQuestion(quest);
		qDialog.btnAnswerA.addActionListener(onClick);
		qDialog.btnAnswerB.addActionListener(onClick);
		qDialog.btnAnswerC.addActionListener(onClick);
		qDialog.btnAnswerD.addActionListener(onClick);
		displayNormalQuestion(quest);
		
		qDialog.btnAnswerA.removeActionListener(onClick);
		qDialog.btnAnswerB.removeActionListener(onClick);
		qDialog.btnAnswerC.removeActionListener(onClick);
		qDialog.btnAnswerD.removeActionListener(onClick);
		return selectedQButton.getText();
	}

	@Override
	public void onStop() {
		gameOver= true;
		client.abort();
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

		if (inputManager.isClicked("ButtonLeft") && !gameOver) {
			if(gameStarted){
				client.sendMessage(new GameMessage(
						Commands.ATTACK, 
						player.getUser().getUsername(), 
						String.valueOf(gameboard.getHighlitTerritory().id)
				));
				System.out.println("leftclicked, sent attack from "+ player.getUser().getUsername() +" to " +gameboard.getHighlitTerritory().id);
			}
		}

		try {
			gameboard.setHighlitCell(gameboard.fromPixel(inputManager.getMousePos().x, inputManager.getMousePos().y));
		} catch (NullPointerException ignore) {
		}

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
		
		interruptThread = Thread.currentThread();
		
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
}
