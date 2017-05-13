package view.states;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
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

import javax.annotation.Resource;
import javax.print.DocFlavor.STRING;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import model.RaceQuestion;
import model.Statistics;
import resources.Resources;
import view.Labels;
import view.MainWindow;
import view.Settings;
import view.components.DialogQuestion;
import view.components.DialogRaceQuestion;
import view.components.GButton;
import view.components.GButtonUI;
import view.components.GLabel;
import view.components.PlayerReport;

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
						//	private String interrupt = "";
						//	private Thread interruptThread;
	// mapkey is in every case the threads name
	/** the commands to wait for with {@link #waitForMsg(String)}  per thread*/
	private Map<String, String> interrupt = new HashMap<>();
	/** the threads (objects) that are sleeping while waiting for the messages*/
	private Map<String, Thread> interruptThread = new HashMap<>();
	/** the message already recieved using {@link #waitForMsg(String)}  per thread*/
	private Map<String, GameMessage> lastMsg = new HashMap<>();
	/**messages that were reciewed, but not processed*/
	private Stack<GameMessage> msgStack = new Stack<>();
	/**logged users username*/
	private String uname;

	
	//-----------------------------------------//
	//  GAME - GUI                             //
	//-----------------------------------------//
	MainWindow root;
	private DialogQuestion qDialog;
	private DialogRaceQuestion rqDialog;
	JLabel[][] rqAnsLabels;
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
	private ActionListener qDialogOnClick = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			qDialog.btnAnswerA.removeActionListener(qDialogOnClick);
			qDialog.btnAnswerB.removeActionListener(qDialogOnClick);
			qDialog.btnAnswerC.removeActionListener(qDialogOnClick);
			qDialog.btnAnswerD.removeActionListener(qDialogOnClick);
			selectedQButton.setUI(baseUI);
			GButton btn = (GButton) e.getSource();
			selectedQButton = btn;
			btn.setUI(selectedUI);
			System.out.println("button clicked, sendin message");
			client.sendMessage(new GameMessage(Commands.NORM_ANSWER, selectedQButton.getText()));
		}
	};
	private boolean rqSent; 
	private ActionListener rqDialogOnClick = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			rqSent = true;
			rqDialog.btnGo.removeActionListener(rqDialogOnClick);
			rqDialog.setInputEnabled(false);
			System.out.println("button clicked, sendin message");
			client.sendMessage(new GameMessage(Commands.RQ_ANSWER, rqDialog.getAnswer()));
		}
	};
	JPanel infoPanel;
	JScrollPane scrPane;
	JLabel[] unames;
	JLabel[] territories;
	JLabel[] points;
	
	//-----------------------------------------//
	//  GAME - LOGIC                           //
	//-----------------------------------------//
	public boolean gameOver;
	private boolean gameStarted;
	private boolean respondingToInput = true;
	
	private GameSettings settings;
	private GameBoard gameboard;
	private Timer countDownTimer = new Timer();
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
	Set<Territory> neighbors = new TreeSet<>(); //ezt a kijelolhetoseg tesztelesehez tudni kell az update-ben
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
		qDialog = new DialogQuestion(root);
		rqDialog = new DialogRaceQuestion(root);
		initRQAnsPanel(3, 3); //ez azer van igy kulon, mer hatha majd nem csak 2-en fognak igy kuzdeni egymas ellen...
		
	}

	private void gameOver() {
		System.out.println("GAME OVER!");
	}
	
	private void setMyTurnFalse() {
		gameboard.unLight();
		myTurn = false;
	}
	
	public void initRQAnsPanel(int rows, int cols) {
		rqAnsLabels = new JLabel[rows][cols];
		rqDialog.answersPanel.setLayout(new GridLayout(3, 2, 5, 5));
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				rqAnsLabels[i][j] = new JLabel();
				rqAnsLabels[i][j].setForeground(Color.BLACK);
				rqAnsLabels[i][j].setFont(Settings.FONT_DEFAULT);
				rqDialog.answersPanel.add(rqAnsLabels[i][j]);
			}			
		}
		rqDialog.pack();
	}
	public void clearRQAnsPanel() {
		for (int i = 0; i < rqAnsLabels.length; i++) {
			for (int j = 0; j < rqAnsLabels[i].length; j++) {
				rqAnsLabels[i][j].setText("");
			}			
		}
		rqDialog.clearAnswer();
	}

	public void initInfoPanel() {
		removeAll();
		setLayout(null);
		infoPanel = new JPanel();
		scrPane = new JScrollPane(infoPanel);
		add(infoPanel);
//		infoPanel.setBounds(0, settings.GAME_HEIGHT, settings.SCREEN_WIDTH ,settings.SCREEN_HEIGHT);
		infoPanel.setBounds(0, settings.GAME_HEIGHT, settings.SCREEN_WIDTH, settings.GAME_INFOLABEL_HEIGHT);

		
		int nRows = settings.players.size()+1;
		infoPanel.setLayout(new GridLayout(nRows, 3));
		unames = new JLabel[nRows];     
		territories = new JLabel[nRows];
		points = new JLabel[nRows];  
		
		unames[0] = new JLabel("Username", SwingConstants.CENTER);
		territories[0] = new JLabel("Territories", SwingConstants.CENTER);
		points[0] = new JLabel("Points", SwingConstants.CENTER);
		infoPanel.add(unames[0]);
		infoPanel.add(territories[0]);
		infoPanel.add(points[0]);
		unames[0].setFont(Settings.FONT_TITLE);
		territories[0].setFont(Settings.FONT_TITLE);
		points[0].setFont(Settings.FONT_TITLE);
		unames[0].setForeground(Color.BLACK);
		territories[0].setForeground(Color.BLACK);
		points[0].setForeground(Color.BLACK);
		for(int i = 1; i < nRows; i++) {
			unames[i] = new JLabel("", SwingConstants.CENTER);
			territories[i] = new JLabel("", SwingConstants.CENTER);
			points[i] = new JLabel("", SwingConstants.CENTER);
			if(uname.equals(settings.players.get(i-1).getUser().getUsername())) {
				unames[i].setBackground(Settings.color_lightGray);
				territories[i].setBackground(Settings.color_lightGray);
				points[i].setBackground(Settings.color_lightGray);
				unames[i].setOpaque(true);
				territories[i].setOpaque(true);
				points[i].setOpaque(true);
			}
			infoPanel.add(unames[i]);
			infoPanel.add(territories[i]);
			infoPanel.add(points[i]);
			unames[i].setFont(Settings.FONT_TITLE);
			territories[i].setFont(Settings.FONT_TITLE);
			points[i].setFont(Settings.FONT_TITLE);
			unames[i].setForeground(settings.players.get(i-1).getColor());
			territories[i].setForeground(settings.players.get(i-1).getColor());
			points[i].setForeground(settings.players.get(i-1).getColor());
		}

		updateInfoPanel();
	}
	public void updateInfoPanel() {
		int nPlayers = settings.players.size();
		for(int i = 0; i < nPlayers; i++) {
			String username = settings.players.get(i).getUser().getUsername();
			unames[i+1].setText(username);
			territories[i+1].setText(String.valueOf(settings.players.get(i).getTerritories().size()));
			points[i+1].setText(String.valueOf(settings.players.get(i).points));
			if(username.equals(currentPlayer)) {
				unames[i+1].setIcon(new ImageIcon(Resources.scaleImage(Resources.ARROW_RIGHT[i], 20, 20)));
			} else {
				unames[i+1].setIcon(null);
			}
		}
	}
	
	@Override
	public void onStart() {
		System.out.println("starting gamestate ");
		gameOver = false;
		gameStarted = false;
		uname = MainWindow.getInstance().getLoggedUser().getUsername();
		new Thread() {
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
					client.sendMessage(new GameMessage(Commands.ARE_YOU_READY));
					waitForMsg(Commands.START);
					
					client.sendMessage(new GameMessage(Commands.SETTINGS_REQUEST));
					manageSettings(waitForMsg(Commands.SETTINGS));

					client.sendMessage(new GameMessage(Commands.GAMEBOARD_REQUEST));
					manageGameboard(waitForMsg(Commands.GAMEBOARD));
					manageYourTurn(waitForMsg(Commands.YOUR_TURN));
					
					gameStarted = true;

					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (GameIsStartedException e) {
					JOptionPane.showMessageDialog(root, Labels.MSG_GAME_STARTED, Labels.MSG_SERVER_ERROR, JOptionPane.ERROR_MESSAGE);
					root.setState(MainWindow.STATE_MAIN);
				} catch (HostDoesNotExistException e) {
					JOptionPane.showMessageDialog(root, Labels.MSG_BAD_IP_ADDRESS, Labels.MSG_SERVER_ERROR, JOptionPane.ERROR_MESSAGE);
					root.setState(MainWindow.STATE_MAIN);
				} catch (ConnectException e) {
					JOptionPane.showMessageDialog(root, Labels.MSG_NO_SERVER_RUNNING_THERE, Labels.MSG_SERVER_ERROR, JOptionPane.ERROR_MESSAGE);
					root.setState(MainWindow.STATE_MAIN);
				} finally {
				}
			}
		}.start();
		
	}
	
	@Override
	public void onStop() {
		System.out.println("client disconnects");
		gameOver= true;
		client.abort();
		if(root.gameServer != null && root.gameServer.host != null) {
			root.gameServer.host.abort();
			root.gameServer = null;
		}
	}
	
	public void manageNewOwner(GameMessage msg) {
		String newOwner = msg.getParams()[0];
		int terrId = Integer.parseInt(msg.getParams()[1]);
		Territory terr = gameboard.getTerrytoryById(terrId);
		for(Player p : settings.players){
			if(newOwner.equals(p.getUser().getUsername())){
				terr.setOwner(p);
				return;
			}
		}
		calcNeighbors();
		updateInfoPanel();
	}
	public void manageAttack(GameMessage msg) {
		attacker = settings.getPlayerByUname(msg.getParams()[0]);
		attackerUname = attacker.getUser().getUsername();
		target = gameboard.getTerrytoryById( Integer.parseInt(msg.getParams()[1]) );
		defender = target.getOwner();
		defenderUname = defender.getUser().getUsername();
		target.markForAttack();
	}
	public void manageNormQuestion(GameMessage msg) {
		boolean isItMine = msg.getParams()[0].equals(Commands.PARAM_YOURS);
		Question question = (Question) StringSerializer.deSerialize(msg.getParams()[1]);
		questionTh = Thread.currentThread();
		if(isItMine){
			displayNormalQuestionAndGetAnswer(question, attackerUname, defenderUname);
		} else {
			displayNormalQuestion(question, attackerUname, defenderUname);
		}
	}
	public void manageNormQuestionRightAnswer(GameMessage msg) {
		try {
			Thread.sleep(1000); //kb megvarjuk, hogy elobb a manageNormQuestionPlayerAnswer fusson le
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
			Thread.sleep(settings.showRightAnswerDelay);
			if (questionTh != null) questionTh.interrupt();
			System.out.println("AREYOURREADY???!!");
			client.sendMessage(new GameMessage(Commands.ARE_YOU_READY));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
		boolean isItMine = msg.getParams()[0].equals(Commands.PARAM_YOURS);
		RaceQuestion question = (RaceQuestion) StringSerializer.deSerialize(msg.getParams()[1]);
		questionTh = Thread.currentThread();
		String ans;
		if(isItMine){
			ans = displayRaceQuestionAndGetAnswer(question, attackerUname, defenderUname);
		} else {
			displayRaceQuestion(question, attackerUname, defenderUname);
		}
	}
	public void manageRaceQuestionRightAnswer(GameMessage msg) {
		try {
			String rightAns = msg.getParams()[0];
			//helyes valasz bejelolese
			rqAnsLabels[0][0].setText("Helyes valasz: ");
			rqAnsLabels[0][1].setText(rightAns);
			Thread.sleep(1 * settings.showRightAnswerDelay);
			if (questionTh != null) questionTh.interrupt();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void manageRaceQuestionPlayerAnswer(GameMessage msg) {
		String uname = msg.getParams()[1];
		String ans = msg.getParams()[0];
		String time = msg.getParams()[2];
		int place = Integer.parseInt(msg.getParams()[3]);
		Color color = settings.getPlayerByUname(uname).getColor();
		rqAnsLabels[place][0].setText(uname+": ");
		rqAnsLabels[place][1].setText(ans);
		rqAnsLabels[place][2].setText(time);
		rqDialog.pack();
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
		Resources.loadGameIcons(settings);
		initInfoPanel();
	}
	public void manageYourTurn(GameMessage msg) {
		currentPlayer = msg.getParams()[0];
		System.out.println("current player: "+currentPlayer);
		myTurn = uname.equals(currentPlayer);
		if(myTurn) {
			calcNeighbors();
		} else {
			setMyTurnFalse(); //ezáltal, ha netán ki volt highlightolva valamilyen terület, az is unlightolódik.
		}
		updateInfoPanel();
	}
	public void managePoints(GameMessage msg) {
		settings.players = (List) StringSerializer.deSerialize(msg.getParams()[0]);
		updateInfoPanel();
	}
	public void manageEndGame(GameMessage msg) {
		PlayerReport[] pr = new PlayerReport[settings.players.size()];
		int i = 0;
		for(Player p : settings.players) {
			pr[i] =  new PlayerReport(p.getUser());
			Statistics stat = p.getGameStats();
			
			pr[i].setQuestNVal(p.getQuestionsAsked());
			pr[i].setRAnsVal(stat.getRightAnswers());
			pr[i].setWAnsVal(stat.getWrongAnswers());
			pr[i].setPointsVal(stat.getPoints());
			pr[i].setRTipsVal(stat.getRightTips());
			pr[i].setWTipsVal(stat.getWrongTips());
//			pr[i].setTerrLostVal(val); not implemented
//			pr[i].setTerrWonVal(val);
			pr[i].setTblDiff(p.getDiffN());
			i++;
		}
		
		((ReportState)root.report).setReports(pr);
//		MainWindow.getInstance().setState(MainWindow.STATE_MAIN);
		MainWindow.getInstance().setState(MainWindow.STATE_REPORT);
	}
	public void manage(GameMessage msg) {
		
	}
	public void manageAll(GameMessage msg) {
		boolean caught = false;
		if(msg.getMessage().equals(Commands.NEW_OWNER)) {
			caught = true;
			manageNewOwner(msg);
		} else if(msg.getMessage().equals(Commands.ATTACK)){
			caught = true;
			manageAttack(msg);
		} else if(msg.getMessage().equals(Commands.NORMAL_QUESTION)){
			caught = true;
			manageNormQuestion(msg);					
		} else if(msg.getMessage().equals(Commands.NORM_ANSWER)){
			caught = true;
			manageNormQuestionRightAnswer(msg);
		} else if(msg.getMessage().equals(Commands.NORM_PLAYER_ANSWER)){
			caught = true;
			manageNormQuestionPlayerAnswer(msg);
		} else if(msg.getMessage().equals(Commands.RACE_QUESTION)){
			caught = true;
			manageRaceQuestion(msg);					
		} else if(msg.getMessage().equals(Commands.RQ_ANSWER)){
			caught = true;
			manageRaceQuestionRightAnswer(msg);
		} else if(msg.getMessage().equals(Commands.RQ_PLAYER_ANSWER)){
			caught = true;
			manageRaceQuestionPlayerAnswer(msg);
		} else if(msg.getMessage().equals(Commands.GAMEBOARD)){
			caught = true;
			manageGameboard(msg);
		} else if(msg.getMessage().equals(Commands.SETTINGS)){
			caught = true;
			manageSettings(msg);
		} else if(msg.getMessage().equals(Commands.YOUR_TURN)){
			caught = true;
			manageYourTurn(msg);
		} else if(msg.getMessage().equals(Commands.POINTS)){
			caught = true;
			managePoints(msg);
		} else if(msg.getMessage().equals(Commands.END_GAME)){
			caught = true;
			manageEndGame(msg);
		}
		if (caught) {
			synchronized(msgStack){
				msgStack.remove(msg); 
			}
		}
	}
	@Override
	public void gotMessage(GameMessage msg) {
		new Thread(){
			@Override
			public void run(){
				Thread.currentThread().setName("client-gotMessage");
				System.out.print("client "+uname+" GotMessage: " +msg.getMessage());
				if(msg.getParams() != null)
					for(String s : msg.getParams()) {
						System.out.print("\t" + s);
					}
				System.out.println();
				
				synchronized (msgStack) {
					boolean caught = false;
					for(String key : interrupt.keySet()) {
						if(msg.getMessage().equals(interrupt.get(key))) {
							caught = true;
							System.out.println("interrupt-watcher caught the message" );
							lastMsg.put(key, msg);
							
							interruptThread.get(key).interrupt(); //this one should interrupt the waitFor... method
						};
					}
					
					if(caught) {
						return;
					}
					
					msgStack.push(msg);						
				}		
				
				while(!gameStarted) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				manageAll(msg);
				
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
		qDialog.lblQuestionText.setText("<html><div style='margin:10px;'>" +quest.getQuestion()+"</div></html>");
		qDialog.lblNorth.setText("<html><div style='margin:10px;'>" + attacker + " vs " + defender);
		qDialog.lblNorth.setHorizontalAlignment(SwingConstants.CENTER);				
		TimerTask countdown = displayCountdown(qDialog.lblSouth, settings.questionTime);
		qDialog.setLocationRelativeTo(root);
		qDialog.setVisible(true);
		respondingToInput = false;
		try {
			Thread.sleep(settings.questionTime + settings.showRightAnswerDelay + 1000);
		} catch (InterruptedException e) {
			//everyone answered early
		}
		countdown.cancel();
		respondingToInput = true;
		qDialog.dispose();
	}
	public String displayNormalQuestionAndGetAnswer(Question quest, String attacker, String defender){
		qDialog.btnAnswerA.addActionListener(qDialogOnClick);
		qDialog.btnAnswerB.addActionListener(qDialogOnClick);
		qDialog.btnAnswerC.addActionListener(qDialogOnClick);
		qDialog.btnAnswerD.addActionListener(qDialogOnClick);
		
		displayNormalQuestion(quest, attacker, defender);
		
		qDialog.btnAnswerA.removeActionListener(qDialogOnClick);
		qDialog.btnAnswerB.removeActionListener(qDialogOnClick);
		qDialog.btnAnswerC.removeActionListener(qDialogOnClick);
		qDialog.btnAnswerD.removeActionListener(qDialogOnClick);
		return selectedQButton.getText();
	}

	public void displayRaceQuestion(RaceQuestion quest, String attacker, String defender){
		rqDialog.lblQuestionText.setText("<html><body style='margin:10px;'>" +quest.getQuestion()+"</body></html>");
		rqDialog.lblNorth.setText("<html><body style='margin:10px;'>" + attacker + " vs " + defender);
		rqDialog.lblNorth.setHorizontalAlignment(SwingConstants.CENTER);
		clearRQAnsPanel();
		TimerTask countdown = displayCountdown(rqDialog.lblSouth, settings.raceTime);
		rqDialog.setLocationRelativeTo(root);
		rqDialog.setVisible(true);
		respondingToInput = false;
		rqDialog.focus();
		rqSent = false;

		try {
			Thread.sleep(settings.raceTime -200);
		} catch (InterruptedException e) {
			//everyone answered early or time's up
		}
			
		countdown.cancel();
		if(myTurn && !rqSent) {
			rqDialogOnClick.actionPerformed(null);
		}
		try {
			Thread.sleep(2*settings.showRightAnswerDelay + 1000);
		} catch (InterruptedException e) {}
		respondingToInput = true;
		rqDialog.dispose();
	}
	public String displayRaceQuestionAndGetAnswer(RaceQuestion quest, String attacker, String defender){
		rqDialog.btnGo.addActionListener(rqDialogOnClick);

		rqDialog.setInputEnabled(true);
		displayRaceQuestion(quest, attacker, defender);
		rqDialog.setInputEnabled(false);
		
		rqDialog.btnGo.removeActionListener(qDialogOnClick);
		return rqDialog.getAnswer();
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
		g.setColor(Settings.color_lightGray3);
		g.fillRect(0, 0, settings.GAME_WIDTH, settings.GAME_INFOLABEL_HEIGHT);
		g.setColor(Settings.color_lightGray2);
		g.fillRect(0, 0, settings.GAME_WIDTH, settings.GAME_HEIGHT);
//		 mark all territories to be repainted
		for (Territory t : gameboard.territories) {
			t.touch();
		}
		gameboard.needsRender = true;
		
		//print info
		g.setFont(Settings.FONT_DEFAULT);
		for(Player p : settings.players) {
			g.setColor(p.getColor());
			
		}
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
		
		if(respondingToInput && myTurn){
//			if (inputManager.isKeyTyped("Enter")) { The server controls everything... even the new turn
//				setMyTurnFalse();
//				client.sendMessage(new GameMessage(Commands.END_TURN, uname));
//			}
			if (inputManager.isClicked("ButtonLeft") && !gameOver) {
				if(!gameStarted) return;
				Territory lit = gameboard.getHighlitTerritory();
				if (lit.equals(Territory.NULL_TERRITORY)) return; //ezt sehogyse tamadjuk!
				client.sendMessage(new GameMessage(
						Commands.ATTACK, 
						player.getUser().getUsername(), 
						String.valueOf(lit.id)
				));
				setMyTurnFalse();
				System.out.println("leftclicked, sent attack from "+ player.getUser().getUsername() +" to " +gameboard.getHighlitTerritory().id);
				
			}
	
			try {
				synchronized (gameboard) {
					boolean valid = true;
					// gameboard.fromPixel(inputManager.getMousePos().x, inputManager.getMousePos().y) might be null
					Territory toBeLit = gameboard.fromPixel(inputManager.getMousePos().x, inputManager.getMousePos().y).getOwner();
//					Territory toBeLit = gameboard.getHighlitTerritory();
					if (toBeLit.equals(Territory.NULL_TERRITORY)) {
						valid = false; //ezt ne tamadjuk... 
//					} else if(valid && lit.getOwner().getTeam() == player.getTeam()) {
					} else if(valid && toBeLit.getOwner().getUser().getUsername().equals(uname)) {
						valid = false; //magunkat ne tamadjuk
					} else if(valid && !neighbors.contains(toBeLit)) {
						valid = false; //ne tamadjunk olyat aki nem szomszed
					}
				
					if(valid) {
						//if valid for attack highlight the territory under the cursor
						gameboard.setHighlitCell(gameboard.fromPixel(inputManager.getMousePos().x, inputManager.getMousePos().y));
					} else {
						//if not valid for attack dont highlight anything
//						gameboard.setHighlitCell(gameboard.fromPixel(-10, -10));
						gameboard.unLight();
					}
				}
				
			} catch (NullPointerException ignore) {
				gameboard.unLight();
			}
		}

	}

	/**
	 * Hangs the thread until the server sends a certain command.
	 * Calling this method will also skip any eventhandling 
	 * code for the first message of specified Command in {@link #gotMessage(GameMessage)}
	 * @param msg
	 * @return the first {@link GameMessage} of type command that was recieved 
	 */ 
	public GameMessage waitForMsg(String msg){
		return waitForMsg(msg, 0);
	}
	
	/**
	 * Hangs the thread until the server sends a certain command.
	 * Calling this method will also skip any eventhandling 
	 * code for the first message of specified type in {@link #gotMessage(GameMessage)}
	 * @param msg
	 * @param maxTime the maximal time the thread will wait.
	 * @return the first {@link GameMessage} of type command that was recieved 
	 */ 
	public GameMessage waitForMsg(String msg, int maxTime){
		String threadName = Thread.currentThread().getName();
		try {
			//check if msg was recieved already
			synchronized (msgStack) {
				for (Iterator<GameMessage> iterator = msgStack.iterator(); iterator.hasNext();) {
					GameMessage gm = iterator.next();
					if(gm.getMessage().equals(msg)){
						GameMessage ret = gm;
						iterator.remove();
						System.out.println( msg + " pulled from stack");
						return ret;
					}
				}
			
				//register this thread for interruption
				interruptThread.put(threadName, Thread.currentThread());
				/**request interrupt, when msg is reciewed by #gotMessage(GameMessage) */
				interrupt.put(threadName, msg);
			}
			if (maxTime != 0) {
				System.out.println("client waiting for " + msg);
				Thread.sleep(maxTime);
			} else {
				while (true){
					System.out.println("client waiting for " + msg);
					Thread.sleep(10000);
				}
			}
			
		} catch (InterruptedException e) {
			System.out.println("client reciewed " + msg);
			return lastMsg.get(threadName);
		} finally {
			//do cleanup
			lastMsg.remove(threadName);
			interrupt.remove(threadName);
			interruptThread.remove(threadName);
		}
		return null;
	}

}
