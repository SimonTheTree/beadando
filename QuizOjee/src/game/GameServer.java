package game;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import controller.Commands;
import controller.GameHost;
import controller.GameInputListener;
import controller.GameMessage;
import game.players.Player;
import game.players.PlayerAI;
import game.players.PlayerHuman;
import gameTools.map.Orientation;
import gameTools.map.generators.MapGeneratorHexRectangleFlat;
import model.Question;
import model.RaceQuestion;
import model.User;
import view.MainWindow;
import view.Settings;


public class GameServer implements GameInputListener {
	GameHost host = null;
	
	private boolean lyukendzsoint = false;
	private boolean gameFinished;
	private boolean gameReady = false;
	private GameBoard board;
	private GameSettings settings;

	private String interrupt;
	private int interruptCounter;
	private Thread interruptThread;
	
	private Thread clientThread;
	private Stack<GameMessage> lastMsg = new Stack<>();
	private Stack<GameMessage> msgStack = new Stack<>();
	private Stack<Question> questions = new Stack<>();
	private Stack<RaceQuestion> RaceQuestions = new Stack<>();
	
	public boolean isLyukendzsoint(){
		return lyukendzsoint;
	}
	
	public void createGame(){
		for(int i =  0; i < Settings.game_numOfQuestions; i++){
			questions.push( MainWindow.getInstance().controller.getQuestion(Settings.game_difficulity*5, Settings.game_difficulity*5 +5, null, Settings.game_numOfQuestions) ); 
		}
		for(int i =  0; i < Settings.game_numOfRaceQuestions; i++){
			RaceQuestions.push( MainWindow.getInstance().controller.getRaceQuestion(null, Settings.game_numOfRaceQuestions) ); 
		}
		
		settings = GameSettings.getInstance();
		settings.TerrPerPlayer = 5;
		settings.setMapGenerator("Rectangular Hexmap");
		// settings.setMapGenerator("Paralelloid Hexmap Pointy");
		// settings.setMapGenerator("Hexshaped Hexmap Pointy");
		// settings.setMapGenerator("Paralelloid Hexmap Flat");
		// settings.setMapGenerator("Hexshaped Hexmap Flat");
		// settings.setMapGenerator("Linear Hexmap Pointy");
		// settings.setMapGenerator("Linear Hexmap Flat");
		
		settings.setLayoutOrientation(Orientation.LAYOUT_FLAT);
		settings.layout.size = new Point(32, 10);
		settings.layout.origin = new Point(0, 0);
		settings.mapTileN = new Dimension(1, 1);
		MapGeneratorHexRectangleFlat<Cell> gen = new MapGeneratorHexRectangleFlat<Cell>("name", new Cell(0,0), new int[]{25, 25});
		List<Cell> l = gen.generate();
		board = new GameBoard(gen, settings.layout);
		settings.mapTileN = board.getDimensionInTiles();
		settings.calcLayoutSize();
		settings.mapWidth = board.getDimensions().width - 2 * board.getZeroPointOffset().width;
		settings.mapHeight = board.getDimensions().height - 2 * board.getZeroPointOffset().height;
		settings.centerLayout();
		
		new Thread() {
			public void run() {
				try {
					Thread.currentThread().setName("GameServer");
					System.out.println("starting up server....");
					host = new GameHost();
					host.addInputListener(GameServer.this);
					System.out.println("server ready...");
//					host.setMaxPlayers(settings.PLAYERS.size());
					host.setMaxPlayers(1);
					host.start();
					System.out.println("server launched...");
					
					lyukendzsoint = true;
					while (!host.isStarted()) {
						System.out.println("server waiting for players...");
						Thread.sleep(1000);
					}
					System.out.println("	[GO]   server");
					
					//a logolt usereket hozzáadjuk a playerlistához a játékban
					int i = 0;
					int c = -1;
					//#bot players not supported yet
					System.out.print("joined players: ");
					for(String uname : host.getUserNames().toArray(new String[0])){
						System.out.print(uname + ", ");
						c++; i++;
						if(! uname.startsWith("[bot")){
							User u = MainWindow.getInstance().controller.getUser(uname);
							settings.PLAYERS.add(new PlayerHuman(u, c));
						} else {
							User u = MainWindow.getInstance().controller.getUser("bot");
							if(u == null){
								u = new User();
							}
							u.setUsername("bot" + String.valueOf(i));
							settings.PLAYERS.add(new PlayerAI(u, c));							
						}
					}
					
					board.generateTerritories(settings.TerrPerPlayer);
					gameReady = true;
					
					host.broadCast(new GameMessage(Commands.START));
					Thread.sleep(1000); //wait for players to reciewe everything
					/* test Commands.NEW_OWNER:
					System.out.println("sum(territories) = "+board.territories.length);
					for(Territory t : board.territories){
						if(t.getOwner().getUser().getUsername().equals(settings.PLAYERS.get(0).getUser().getUsername())){
							host.broadCast(new GameMessage(
									Commands.NEW_OWNER, 
									settings.PLAYERS.get(0).getUser().getUsername(), 
									String.valueOf(t.id)
							));
							Thread.sleep(1000);
						}
					}*/
					
					//battle phase
					while(!gameFinished){
						for(Player p : settings.PLAYERS){
							String uname = p.getUser().getUsername();
							host.broadCast(new GameMessage(Commands.YOUR_TURN, uname));
							// ...meanwhile a serverlistener kezeli a kliens uzeneteit
							waitForMsgFrom(uname, Commands.END_TURN);
						}
					}
					
					while(!gameFinished) {Thread.sleep(1000);}
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					host.abort();
				}
			}
		}.start();
		
		gameFinished = false;
		
	
	}
	
	public void shuffleQuestion(Question quest){
		Random rand = Settings.RANDOM;
		String[] q = new String[4];
		
		//fill the array qith the answers
		q[0] = quest.getRightAnswer();
		q[1] = quest.getAnswer1();
		q[2] = quest.getAnswer2();
		q[3] = quest.getAnswer3();
		
		//shuffle the array (Fisher-Yates shuffle) 
		for(int i = q.length-1; i>0; i--){
			int index = rand.nextInt(i+1);
			//swap
			String s = q[i];
			q[i] = q[index];
			q[index] = s;
		}
		
		//write back the answers
		quest.setAnswer1(q[0]);
		quest.setAnswer2(q[1]);
		quest.setAnswer3(q[2]);
		quest.setRightAnswer(q[3]);
	}
	
	public void manageAttack(GameMessage msg) throws InterruptedException {
		//remove this attack from msgStack
		synchronized (msgStack) {
			msgStack.pop();
		}
		
		//inform everyone
		String attacker = msg.getSender();
//		System.out.println("attacker = " + attacker);
		int targetTerritoryID = Integer.parseInt(msg.getParams()[1]);
		String targetPlayer = board.getTerrytoryById(targetTerritoryID).getOwner().getUser().getUsername();
//		System.out.println("defender = " + targetPlayer);
//		System.out.println("TARGET: " +targetPlayer);

		host.broadCast(new GameMessage(Commands.ATTACK, msg.getSender(), String.valueOf(targetTerritoryID)));
		//Wait for the clients to process the news
		Thread.sleep(1000);

		Question question = questions.pop();
		String rightAnswer = question.getRightAnswer();
		shuffleQuestion(question);
		String serializedQuestion = StringSerializer.serialize(question);
		host.sendMessage(targetPlayer, new GameMessage(Commands.NORMAL_QUESTION, Commands.PARAM_YOURS, serializedQuestion));
		host.sendMessage(attacker, new GameMessage(Commands.NORMAL_QUESTION, Commands.PARAM_YOURS, serializedQuestion));
		for(Player p : settings.PLAYERS){
			//for all players except the attacker and defender
			if( !attacker.equals(p.getUser().getUsername()) && !targetPlayer.equals(p.getUser().getUsername())){
				//send the question with "not yours"
				host.sendMessage(attacker, new GameMessage(Commands.NORMAL_QUESTION, Commands.PARAM_NOT_YOURS, serializedQuestion));					
			}
		}
		//should recieve 2 answers... wait for settings.questionTime max before continuing
		GameMessage[] ans =  waitForMsg(Commands.NORM_ANSWER, settings.questionTime);
		
		host.broadCast(new GameMessage(Commands.NORM_ANSWER, rightAnswer));

		if(ans.length == 2){ //in case of two answers
			host.broadCast(new GameMessage(Commands.NORM_PLAYER_ANSWER, ans[0].getParams()[0], ans[0].getSender()));
			host.broadCast(new GameMessage(Commands.NORM_PLAYER_ANSWER, ans[1].getParams()[0], ans[1].getSender()));
			String ansAttacker;
			String ansDefender; 
			if(ans[0].getSender().equals(attacker) ){
				ansAttacker = ans[0].getParams()[0];
				ansDefender = ans[1].getParams()[0];
			} else {
				ansAttacker = ans[1].getParams()[0];					
				ansDefender = ans[0].getParams()[0];
			}
			//both answers are right
			if( ansAttacker.equals( ansDefender ) && rightAnswer.equals(ansAttacker)){
//				//another raceQuestion round
//				RaceQuestion rQuestion = RaceQuestions.pop();
//				int rightRQAnswer = rQuestion.getRightAnswer();
//				rQuestion.setRightAnswer(Integer.MIN_VALUE); 
//				String serializedRQuestion = StringSerializer.serialize(rQuestion);
//				host.sendMessage(targetPlayer, new GameMessage(Commands.RACE_QUESTION, Commands.PARAM_YOURS, serializedRQuestion));
//				host.sendMessage(attacker, new GameMessage(Commands.RACE_QUESTION, Commands.PARAM_YOURS, serializedRQuestion));
//				for(Player p : settings.PLAYERS){
//					if( !attacker.equals(p.getUser().getUsername()) && !targetPlayer.equals(p.getUser().getUsername())){
//						host.sendMessage(attacker, new GameMessage(Commands.RACE_QUESTION, Commands.PARAM_NOT_YOURS, serializedRQuestion));					
//					}
//				}
//				//should have recieved 2 answers...
//				GameMessage[] RQans =  waitForMsg(Commands.RQ_ANSWER, settings.raceTime);
//				host.broadCast(new GameMessage(Commands.RQ_ANSWER, rightAnswer));
//				if (RQans.length == 2){
//					host.broadCast(new GameMessage(Commands.RQ_PLAYER_ANSWER, RQans[0].getParams()[0], RQans[0].getSender()));
//					host.broadCast(new GameMessage(Commands.RQ_PLAYER_ANSWER, RQans[1].getParams()[0], RQans[1].getSender()));
//					double attAns;
//					double defAns;
//					if(attacker.equals(RQans[0].getSender()) ){
//						attAns = Double.parseDouble(RQans[0].getParams()[0]);
//						defAns = Double.parseDouble(RQans[1].getParams()[0]);
//					} else {
//						attAns = Double.parseDouble(RQans[1].getParams()[0]);					
//						defAns = Double.parseDouble(RQans[0].getParams()[0]);
//					}
//					if(Double.compare(attAns, defAns) == 0){ //a gyorsabbik nyer
//						gameboard_changeOwner(attacker, targetTerritoryID);
//					} else {
//						if(Math.abs(rightRQAnswer-attAns) < Math.abs(rightRQAnswer-defAns)){
//							gameboard_changeOwner(attacker, targetTerritoryID);
//						} else {
//							//nothin changes
//						}
//					}
//				} else {
//					host.broadCast(new GameMessage(Commands.RQ_PLAYER_ANSWER, RQans[0].getParams()[0], RQans[0].getSender()));
//					if(attacker.equals(RQans[0].getSender())){
//						gameboard_changeOwner(attacker, targetTerritoryID);
//					} else {
//						//nothin happens
//					}
//				}
			} else { //not both answers were right
				//only one right answer
				if(rightAnswer.equals(ansAttacker)){
					gameboard_changeOwner(attacker, targetTerritoryID);
				}
				if(rightAnswer.equals(ansDefender)){
					//nothin changes
				}
			}
		} else { //less than two answers were submitted
			if(ans.length == 1){
				host.broadCast(new GameMessage(Commands.NORM_PLAYER_ANSWER, ans[0].getParams()[0], ans[0].getSender()));
				if(attacker.equals(ans[0].getSender()) && rightAnswer.equals(ans[0].getParams()[0])){
					gameboard_changeOwner(attacker, targetTerritoryID);
				} else {
					//nothin happens
				}								
			}
		}
	}
	
	@Override
	public void gotMessage(GameMessage msg) { //ez masik threaden fut!!
		System.out.println("server GotMessage: " +msg.toString());
		synchronized (msgStack) {
			msgStack.push(msg);
		}
		if(msg.getMessage().equals(interrupt)) {
			System.out.println("interruptcounter caught message, only " + interruptCounter +"left" );
			synchronized (msgStack) {
				lastMsg.push(msg);
				msgStack.pop();
			}
			if(--interruptCounter == 0){ 
				interruptThread.interrupt(); //this one should interrupt the waitFor... method
			}
		};
		if(!gameReady) return;
		new Thread(){
			public void run(){
				Thread.currentThread().setName("server-Msg-listener");
				try{
					if(msg.getMessage().equals(Commands.ATTACK)) {
						manageAttack(msg);
					} else if(msg.getMessage().equals(Commands.GAMEBOARD_REQUEST)) {
						if(!gameReady) return;
						System.out.println("sending gameboard");
						String s =  StringSerializer.serialize(board);
						host.sendMessage(msg.getSender(), new GameMessage(Commands.GAMEBOARD, s));
					} else if(msg.getMessage().equals(Commands.SETTINGS_REQUEST)) { 
						if(!gameReady) return;
						System.out.println("sending settings");
						String s =  StringSerializer.serialize(settings);
						host.sendMessage(msg.getSender(), new GameMessage(Commands.SETTINGS, s));
					} else if(msg.getMessage().equals(Commands.IS_STARTED)) { 
						host.sendMessage(msg.getSender(), new GameMessage(Commands.START));
					};
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}		
		}.start();
		
	}
	
	private void gameboard_changeOwner(String uname, int territoryID){
		Player newOwner = settings.getPlayerByUname(uname);
		board.getTerrytoryById(territoryID).setOwner(newOwner); 
		host.broadCast(new GameMessage(Commands.NEW_OWNER, uname, String.valueOf(territoryID)));
		
	}
	
	
	/**
	 * Hangs the thread until "number of all clients" ammount of a certain msg is recieved.
	 * @param msg String message type
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) 
	 */
	public GameMessage[] waitForMsg(String msg){
		return waitForMsg(msg, 0, settings.PLAYERS.size());
	}
	/**
	 * Hangs the thread until all clients send a certain msg or maxTime is up.
	 * @param msg
	 * @param maxTime the maximal time the thread will wait.
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) in due time
	 */
	public GameMessage[] waitForMsg(String msg, long maxTime){
		return waitForMsg(msg, maxTime, settings.PLAYERS.size());
	}
	/**
	 * Hangs the thread until specified ammount of a certain msg is recieved.
	 * @param msg
	 * @param maxTime the maximal time the thread will wait.
	 * @param numberOfMessages the ammount of messages expected during maxTime
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) 
	 */
	public GameMessage[] waitForMsg(String msg, long maxTime, int numberOfMessages){
		System.out.println("server waiting for "+String.valueOf(numberOfMessages)+ "x " +msg+ " maxtime: "+ String.valueOf(maxTime));
		try{
			lastMsg.clear();
		
			interruptCounter = numberOfMessages;
			//check if msg was recieved already
			synchronized (msgStack) {
				for (Iterator<GameMessage> iterator = msgStack.iterator(); iterator.hasNext();) {
					GameMessage gm = iterator.next();
					if(gm.getMessage().equals(msg)){
						lastMsg.push(gm);
						iterator.remove();
						System.out.println( msg + " pulled from stack");
						
						interruptCounter--;
						if(interruptCounter == 0){ 
							return lastMsg.toArray(new GameMessage[0]);
						}
					}
					
				} 
			
				interruptThread = Thread.currentThread();
				interrupt = msg; // start listening-collecting-interrupting for "msg" messages
			}
			System.out.println("server waiting for " + msg);
			if(maxTime != 0){
				Thread.sleep(maxTime);
			} else {
				while(true){
					Thread.sleep(10000);
				}				
			}
		}catch(InterruptedException e){
			// at this point lastMsg has collected settings.PLAYERS.size() GameMessages.
			return lastMsg.toArray(new GameMessage[0]);
			
		} finally {
			interrupt = null; // stop special listening
		}
		//if time ran out:
		return lastMsg.toArray(new GameMessage[0]);
	}

	/**
	 * Hangs the thread until a certain client sends a certain msg.
	 * @param clientName
	 * @param msg
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) 
	 */
	public GameMessage waitForMsgFrom(String clientName, String msg){
		return waitForClientMsg(clientName, msg, 0);
	}
	/**
	 * Hangs the thread until a certain client sends a certain msg.
	 * @param clientName
	 * @param msg
	 * @param maxTime the maximal time the thread will wait.
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) 
	 */
	public GameMessage waitForClientMsg(String clientName, String msg, long maxTime){
		System.out.println("server waiting for " +msg+ "from "+ clientName +", maxtime: "+ String.valueOf(maxTime));
		try {

			// check if msg was recieved already
			synchronized (msgStack) {
				for (Iterator<GameMessage> iterator = msgStack.iterator(); iterator.hasNext();) {
					GameMessage gm = iterator.next();
					if (gm.getMessage().equals(msg)) {
						iterator.remove();
						System.out.println(msg + " pulled from stack");
						return gm;
					}
				}

				interruptCounter = 1;
				interruptThread = Thread.currentThread();
				interrupt = msg; //start listening for this kind of msg
			}
			if (maxTime != 0) {
				System.out.println("server waiting for " + msg);
				Thread.sleep(maxTime);
			} else {
				while (true) {
					System.out.println("server waiting for " + msg);
					Thread.sleep(10000);
				}
			}
		} catch (InterruptedException e) {
			GameMessage ret = lastMsg.pop();
			lastMsg = null;
			System.out.println("server recieved " + msg);
			return ret;
		} finally {
			interrupt = null;
		}
		return null;
	}
	
}
