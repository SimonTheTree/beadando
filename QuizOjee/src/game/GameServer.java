package game;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.EmptyStackException;
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
	public GameHost host = null;
	
	private boolean lyukendzsoint = false;
	private boolean gameFinished;
	private boolean gameReady = false;
	private GameBoard board;
	private GameSettings settings;
	private String currentPlayer; 
	
	//mapkey is in every case the threads name
	/** the commands to wait for with {@link #waitForMsg(String, long, int)} or {@link #waitForMsgFrom(String, String, long)}  per thread*/
	private Map<String, String> interrupt = new HashMap<>();
	/** the usernames to watch when using {@link #waitForMsgFrom(String, String, long)} per thread*/
	private Map<String, String> interruptClientName = new HashMap<>();
	/** the ammount of messages to wait for per thread*/
	private Map<String, Integer> interruptCounter = new HashMap<>();
	/** the threads (objects) that are sleeping while waiting for the messages*/
	private Map<String, Thread> interruptThread = new HashMap<>();
	/** the messages already recieved using {@link #waitForMsg(String, long, int)} or {@link #waitForMsgFrom(String, String, long)}  per thread*/
	private Map<String, Stack<GameMessage>> lastMsg = new HashMap<>();
	
	/**messages that were reciewed, but not processed*/
	private Stack<GameMessage> msgStack = new Stack<>();
	/**Question stash for the game*/
	private Stack<Question> questions = new Stack<>();
	/**RaceQuestion stash for the game*/
	private Stack<RaceQuestion> raceQuestions = new Stack<>();
	
	private Question fetchQuestion() {
		System.out.println("server out of questions fetching some more...");
		Question quest;
		try{
			quest = questions.pop();
		} catch (EmptyStackException e) {
			for(int i =  0; i < Settings.game_numOfQuestions; i++){
				questions.push( MainWindow.getInstance().controller.getQuestion(Settings.game_difficulity*5, Settings.game_difficulity*5 +5, null, Settings.game_numOfQuestions) ); 
			}
			quest = questions.pop();
		}
		return quest;
	}
	private RaceQuestion fetchRQuestion() {
		System.out.println("server out of racequestions fetching some more...");
		RaceQuestion quest;
		
		try{
			quest = raceQuestions.pop();
		} catch (EmptyStackException e) {
			for(int i =  0; i < Settings.game_numOfRaceQuestions; i++){
				raceQuestions.push( MainWindow.getInstance().controller.getRaceQuestion(null, Settings.game_numOfRaceQuestions) ); 
			}
			quest = raceQuestions.pop();
		}
		return quest;
	}
	
 	public boolean isLyukendzsoint(){
		return lyukendzsoint;
	}
	
	public void createGame(){
		for(int i =  0; i < Settings.game_numOfQuestions; i++){
			questions.push( MainWindow.getInstance().controller.getQuestion(Settings.game_difficulity*5, Settings.game_difficulity*5 +5, null, Settings.game_numOfQuestions) ); 
		}
		for(int i =  0; i < Settings.game_numOfRaceQuestions; i++){
			raceQuestions.push( MainWindow.getInstance().controller.getRaceQuestion(null, Settings.game_numOfRaceQuestions) ); 
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
		settings.gameType = Settings.game_type;
		
		new Thread() {
			public void run() {
				try {
					Thread.currentThread().setName("GameServer");
					System.out.println("starting up server....");
					host = new GameHost();
					host.addInputListener(GameServer.this);
					System.out.println("server ready...");
//					host.setMaxPlayers(settings.PLAYERS.size());
					host.setMaxPlayers(Settings.game_numOfPlayers);
					host.start();
					System.out.println("server launched...");
					
					lyukendzsoint = true;
					while (!host.isStarted()) {
						System.out.println("server waiting for players...");
						Thread.sleep(1000);
					}
					System.out.println("	[GO]   server");
					
					//a logolt usereket hozzaadjuk a playerlistahoz a jatekban
					int i = 0;
					int c = -1;
					//#bot players not supported yet
					System.out.print("joined players: ");
					for(String uname : host.getUserNames().toArray(new String[0])){
						System.out.print(uname + ", ");
						c++; i++;
						if(! uname.startsWith("[bot")){
							User u = MainWindow.getInstance().controller.getUser(uname);
							settings.players.add(new PlayerHuman(u, c));
						} else {
							User u = MainWindow.getInstance().controller.getUser("bot");
							if(u == null){
								u = new User();
							}
							u.setUsername("bot" + String.valueOf(i));
							settings.players.add(new PlayerAI(u, c));							
						}
					}
					board.generateTerritories(settings.TerrPerPlayer);
					gameReady = true;
					
//					Thread.sleep(1000); //wait for players to reciewe everything

					//battle phase
					while(!gameFinished){
						for(Player p : settings.players){
							currentPlayer = p.getUser().getUsername();
							host.broadCast(new GameMessage(Commands.YOUR_TURN, currentPlayer));
							// ...meanwhile a serverlistener kezeli a kliens uzeneteit
							GameMessage msg  = waitForMsgFrom(currentPlayer, Commands.END_TURN);
							
							System.out.println("server: " + msg.getParams()[0] + "said he's done");
							if(Settings.GAME_TYPE_10_ROUNDS.equals(settings.gameType)) {
								checkGameOver30Rounds();
							} else if(Settings.GAME_TYPE_BLITZKRIEG.equals(settings.gameType)) {
								checkGameOver30Rounds();								
							} else { //default lastmasstanding
								checkGameOverLastOneStand();
							}
						}
						calcPoints();
					}
					
					host.broadCast(new GameMessage(Commands.END_GAME));
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
	
	private void shuffleQuestion(Question quest){
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
	
	/**
	 * broadcasts a normal question to all users
	 * @param question The question to be broadcast
	 * @param targets these usernames will reciewe a PARAM_YOURS, others will get PARAM_NOT_YOURS
	 * @return the right answer of the question broadcast
	 */
	private void broadcastNormQuestion(Question question, String... targets) {
		String serializedQuestion = StringSerializer.serialize(question);
		for(Player p : settings.players){
			String uname = p.getUser().getUsername();
			boolean isTarget = false;
			//check is player target
			for (String target : targets) {
				if(target.equals(uname)) {
					isTarget = true;
					break;
				};
			}
			if(isTarget) {
				host.sendMessage(uname, new GameMessage(
					Commands.NORMAL_QUESTION, 
					Commands.PARAM_YOURS, 
					serializedQuestion
				));				
			} else {
				host.sendMessage(uname, new GameMessage(
					Commands.NORMAL_QUESTION, 
					Commands.PARAM_NOT_YOURS, 
					serializedQuestion
				));									
			}
		}
	}
	
	/**
	 * Waits maximally $time ammount of time for RaceAnswers or till 
	 * $ammount of them were gathered. All the answers gathered are 
	 * padded with an additional parameter, which is the relative time
	 * they were reciewe to when listening was started.
	 * @param ammount maximal ammount to wait for
	 * @param time maxtime to wait
	 * @return
	 */
	private GameMessage[] waitForRaceAnswers(int ammount, long time) {
		List<GameMessage> list = new ArrayList<>();
		long startTime = System.currentTimeMillis();
		long timeLeft = time + 1000;
		int i = 1;
		do {
			GameMessage[] msg = waitForMsg(Commands.RQ_ANSWER, timeLeft, 1);
			long timePassed = System.currentTimeMillis() - startTime;
			timeLeft = time-timePassed;
			
			if(msg.length == 1) {
				String a = Commands.RQ_PLAYER_ANSWER;
				//adjuk hozza a recieve time infot
				String[] newParams = new String[4];
				newParams[0] = msg[0].getParams()[0];
				newParams[1] = msg[0].getSender();
				newParams[2] = String.valueOf(timePassed);
				newParams[3] = String.valueOf(i++);
				
				msg[0].setParams(newParams);
				list.add(msg[0]);				
			}
			if(list.size() == ammount) {
				break;
			}
			if (timeLeft <= 0) {
				System.out.println("no more time to wait for incoming raceQuestions! timeleft: "+timeLeft);
			}
		} while(timeLeft > 0);
		//clear msgStack from any residual rq-answers
		for (Iterator<GameMessage> iterator = msgStack.iterator(); iterator.hasNext();) {
			GameMessage gm = (GameMessage) iterator.next();
			if(gm.getMessage().equals(Commands.RQ_ANSWER)) {
				iterator.remove();
			}
			
		}
		return list.toArray(new GameMessage[0]);
	}
	
	/**
	 * broadcasts a race question to all users
	 * 
	 * @param targets these usernames will reciewe a PARAM_YOURS, others will get PARAM_NOT_YOURS
	 * @return the right answer of the question broadcast
	 */
	private void sendRaceQuestion(RaceQuestion rQuestion, String... targets) {
		String serializedRQuestion = StringSerializer.serialize(rQuestion);
		for(Player p : settings.players){
			String uname = p.getUser().getUsername();
			boolean isTarget = false;
			//check is player target
			for (String target : targets) {
				if(target.equals(uname)) {
					isTarget = true;
					break;
				};
			}
			if(isTarget) {
				host.sendMessage(uname, new GameMessage(
					Commands.RACE_QUESTION, 
					Commands.PARAM_YOURS, 
					serializedRQuestion
				));		
			} else {
				host.sendMessage(uname, new GameMessage(
					Commands.RACE_QUESTION, 
					Commands.PARAM_NOT_YOURS, 
					serializedRQuestion
				));										
			}
		}
	}
	
	private void manageAttack(GameMessage msg){
		//remove this attack from msgStack
		
		String attacker = msg.getSender();
		int targetTerritoryID = Integer.parseInt(msg.getParams()[1]);
		String defender = board.getTerrytoryById(targetTerritoryID).getOwner().getUser().getUsername();

		//inform everyone
		host.broadCast(new GameMessage(Commands.ATTACK, msg.getSender(), String.valueOf(targetTerritoryID)));
		//Wait for the clients to process the news
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Question question = fetchQuestion();
		String rightAnswer = question.getRightAnswer();
		shuffleQuestion(question);
		broadcastNormQuestion(question, attacker, defender);
		//should recieve 2 answers... wait for settings.questionTime max before continuing
		GameMessage[] ans =  waitForMsg(Commands.NORM_ANSWER, settings.questionTime);

		
		//share users answers
		for(GameMessage m : ans) {
			host.broadCast(new GameMessage(Commands.NORM_PLAYER_ANSWER, m.getParams()[0], m.getSender()));
		}
		
		//share the right answer
		host.broadCast(new GameMessage(Commands.NORM_ANSWER, rightAnswer));
		
		//wait for clients to process news
		waitForMsg(Commands.ARE_YOU_READY);

		if(ans.length == 2){ //in case of two answers
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
				settings.getPlayerByUname(attacker).points += question.getDifficulty();
				settings.getPlayerByUname(defender).points += question.getDifficulty();
				//another raceQuestion round
				RaceQuestion rQuestion = fetchRQuestion();
				double rightRQAnswer = rQuestion.getRightAnswer();
				rQuestion.setRightAnswer(Double.MIN_VALUE); 
				
				sendRaceQuestion(rQuestion, attacker, defender);
				
				//should have recieved 2 answers...
				GameMessage[] RQans =  waitForRaceAnswers(2, settings.raceTime);
				
				//share the right answer
				host.broadCast(new GameMessage(Commands.RQ_ANSWER, String.valueOf(rightRQAnswer)));
				
				//share users answers
				for(GameMessage m : RQans) {
					host.broadCast(new GameMessage(
						Commands.RQ_PLAYER_ANSWER, 
						m.getParams()[0], 
						m.getParams()[1],   
						String.format("%05.4f", Double.parseDouble(m.getParams()[2])/1000.0),
						m.getParams()[3]
					));
				}
				
				if (RQans.length == 2){
					double attAns = 0;
					int attTime; 
					double defAns = 0;
					int defTime;
					if(attacker.equals(RQans[0].getSender()) ){
						try {
							attAns = Double.parseDouble(RQans[0].getParams()[0]);
						} catch(NullPointerException | NumberFormatException e) {e.printStackTrace();}
						attTime = Integer.parseInt(RQans[0].getParams()[2]);
						try {
							defAns = Double.parseDouble(RQans[1].getParams()[0]);
						} catch(NullPointerException | NumberFormatException e) {e.printStackTrace();}
						defTime = Integer.parseInt(RQans[1].getParams()[2]);
					} else {
						try {
							attAns = Double.parseDouble(RQans[1].getParams()[0]);
						} catch(NullPointerException | NumberFormatException e) {e.printStackTrace();}
						attTime = Integer.parseInt(RQans[1].getParams()[2]);
						try {
							defAns = Double.parseDouble(RQans[0].getParams()[0]);
						} catch(NullPointerException | NumberFormatException e) {e.printStackTrace();}
						defTime = Integer.parseInt(RQans[0].getParams()[2]);
					}
					if(attAns == defAns){ //a gyorsabbik nyer
						if(attTime < defTime) {//tamado gyorsabb
							gameboard_newOwner(attacker, targetTerritoryID);							
						} else { //vedo gyorsabb
							
						}
					} else {
						if(Math.abs(rightRQAnswer-attAns) < Math.abs(rightRQAnswer-defAns)){
							gameboard_newOwner(attacker, targetTerritoryID);
							settings.getPlayerByUname(attacker).points += 5;
						} else {
							//nothin changes
						}
					}
				} else {
					if(attacker.equals(RQans[0].getSender())){
						gameboard_newOwner(attacker, targetTerritoryID);
						settings.getPlayerByUname(attacker).points += 5;
					} else {
						settings.getPlayerByUname(defender).points += 5;
						//nothin happens
					}
				}
			} else { //not both answers were right
				//only one right answer
				if(rightAnswer.equals(ansAttacker)){
					settings.getPlayerByUname(attacker).points += question.getDifficulty() * 2;
					settings.getPlayerByUname(defender).points += question.getDifficulty() * 0.5;
					gameboard_newOwner(attacker, targetTerritoryID);
				}
				if(rightAnswer.equals(ansDefender)){
					settings.getPlayerByUname(attacker).points += question.getDifficulty() * 0.5;
					settings.getPlayerByUname(defender).points += question.getDifficulty() * 2;
					//nothin changes
				}
			}
		} else { //less than two answers were submitted
			if(ans.length == 1){
				if(attacker.equals(ans[0].getSender()) && rightAnswer.equals(ans[0].getParams()[0])){
					settings.getPlayerByUname(attacker).points += question.getDifficulty() * 2;
					gameboard_newOwner(attacker, targetTerritoryID);
				} else {
					settings.getPlayerByUname(attacker).points += question.getDifficulty() * 0.5;
					//nothin happens
				}								
			}
		}
		System.out.println("sending points");
		host.broadCast(new GameMessage(Commands.POINTS, StringSerializer.serialize((Serializable) settings.players)));
	}
	private void manageSettingsRequest(GameMessage msg) {
		if(!gameReady) return;
		System.out.println("sending settings");
		String s =  StringSerializer.serialize(settings);
		host.sendMessage(msg.getSender(), new GameMessage(Commands.SETTINGS, s));
		if(currentPlayer != null)
			host.sendMessage(msg.getSender(), new GameMessage(Commands.YOUR_TURN, currentPlayer));
	}
	private void manageGameBoardRequest(GameMessage msg) {
		if(!gameReady) return;
		System.out.println("sending gameboard");
		String s =  StringSerializer.serialize(board);
		host.sendMessage(msg.getSender(), new GameMessage(Commands.GAMEBOARD, s));
	}
	private void manageIsStarted(GameMessage msg) {
		host.sendMessage(msg.getSender(), new GameMessage(Commands.START));
	}
	private void manage(GameMessage msg) {
		
	}
	
	private void calcPoints() {
		for (Territory t : board.territories) {
			t.getOwner().points++;
		}
		System.out.println("sending points");
		host.broadCast(new GameMessage(Commands.POINTS, StringSerializer.serialize((Serializable) settings.players)));
	}
	private void checkGameOverLastOneStand() { //ha mindenkinek ugyanaz az ownerje
		String uname = board.territories[0].getOwner().getUser().getUsername();
		gameFinished = true;
		for(int i = 1; i < board.territories.length; i++) {
			if(uname.equals(board.territories[i].getOwner().getUser().getUsername())) {
				gameFinished = false;
				break;
			}
		}
	}
	int round = 1;
	private void checkGameOver30Rounds() { //ha mindenkinek ugyanaz az ownerje
		if(round++ > 30) {
			gameFinished = true;
		}
	}
	
	@Override
	public void gotMessage(GameMessage msg) { //ez masik threaden fut!!
		new Thread(){
			public void run(){
				Thread.currentThread().setName("server-Msg-listener");
				System.out.print("server GotMessage: " +msg.getMessage() +" from: " + msg.getSender());
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
							interruptCounter.put(key, interruptCounter.get(key) -1); //interruptCounter--
							System.out.println("interrupt-wathcher caught message, " + interruptCounter.get(key) +" left" );
							
							lastMsg.get(key).push(msg);
							
							if(interruptCounter.get(key) == 0){ 
								interruptThread.get(key).interrupt(); //this one should interrupt the waitFor... method
							}
						};
					}
					
					if(caught) {
						return;
					}
					
					msgStack.push(msg);						
				}		
				
				try{
					while(!gameReady) {
						Thread.sleep(100);
					}
					if(msg.getMessage().equals(Commands.ATTACK)) {
						synchronized(msgStack){ msgStack.remove(msg); }
						manageAttack(msg);
					} else if(msg.getMessage().equals(Commands.GAMEBOARD_REQUEST)) {
						synchronized(msgStack){ msgStack.remove(msg); }
						manageGameBoardRequest(msg);
					} else if(msg.getMessage().equals(Commands.SETTINGS_REQUEST)) {
						synchronized(msgStack){ msgStack.remove(msg); }
						manageSettingsRequest(msg);
					} else if(msg.getMessage().equals(Commands.ARE_YOU_READY)) {
						synchronized(msgStack){ msgStack.remove(msg); }
						manageIsStarted(msg);
					} else {
												
					};
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}.start();
		
	}
	
	private void gameboard_newOwner(String uname, int territoryID){
		Player newOwner = settings.getPlayerByUname(uname);
		board.getTerrytoryById(territoryID).setOwner(newOwner); 
		host.broadCast(new GameMessage(Commands.NEW_OWNER, uname, String.valueOf(territoryID)));
		
	}
	
	
	/**
	 * Hangs the thread until "number of all clients" ammount of a certain msg is recieved.
	 * @param msg String message type
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) 
	 */
	private GameMessage[] waitForMsg(String msg){
		return waitForMsg(msg, 0, settings.players.size());
	}
	/**
	 * Hangs the thread until all clients send a certain msg or maxTime is up.
	 * @param msg
	 * @param maxTime the maximal time the thread will wait.
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) in due time
	 */
	private GameMessage[] waitForMsg(String msg, long maxTime){
		return waitForMsg(msg, maxTime, settings.players.size());
	}
	/**
	 * Hangs the thread until specified ammount of a certain msg is recieved.
	 * @param msg
	 * @param maxTime the maximal time the thread will wait.
	 * @param numberOfMessages the ammount of messages expected during maxTime
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) 
	 */
	private GameMessage[] waitForMsg(String msg, long maxTime, int numberOfMessages){
		System.out.println("server waiting for "+String.valueOf(numberOfMessages)+ "x " +msg+ " maxtime: "+ String.valueOf(maxTime));
		String threadName = Thread.currentThread().getName();
		Stack<GameMessage> localMsgStack = new Stack<>();
		try{
			//check if msg was recieved already
			interruptCounter.put(threadName, numberOfMessages);
			lastMsg.put(threadName, localMsgStack);
			
			synchronized (msgStack) {
				for (Iterator<GameMessage> iterator = msgStack.iterator(); iterator.hasNext();) {
					GameMessage gm = iterator.next();
					if(gm.getMessage().equals(msg)){
						lastMsg.get(threadName).push(gm);
						iterator.remove();
						System.out.println( msg + " pulled from msgStack");
						
						interruptCounter.put(threadName, interruptCounter.get(threadName) -1); //interruptCounter--;
						if(interruptCounter.get(threadName) == 0){ 
							System.out.println( "desired ammount of "+ msg + " was pulled from msgStack");
							return localMsgStack.toArray(new GameMessage[0]);
						}
					}					
				} 
				
			
				// start listening-collecting-interrupting for "msg" messages
				interruptThread.put(threadName, Thread.currentThread());
				interrupt.put(threadName, msg); 
			}
			
			System.out.println("server waiting for " + msg);
			if(maxTime != 0){
				Thread.sleep(maxTime);
			} else {
				while(true){
					Thread.sleep(10000);
					System.out.println("server waiting for " + msg);
				}				
			}
		}catch(InterruptedException e){
			System.out.println("server got all desired "+msg);
			// at this point lastMsg has collected settings.PLAYERS.size() GameMessages.
			return localMsgStack.toArray(new GameMessage[0]);
			
		} finally {
			synchronized (msgStack) {		
				lastMsg.remove(threadName);
				interruptCounter.remove(threadName);
				interruptThread.remove(threadName);
				interrupt.remove(threadName);
			}
		}
		//if time ran out:
		System.out.println("server didnt get all desired "+msg);
		return localMsgStack.toArray(new GameMessage[0]);
	}

	/**
	 * Hangs the thread until a certain client sends a certain msg.
	 * @param clientName
	 * @param msg
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) 
	 */
	private GameMessage waitForMsgFrom(String clientName, String msg){
		return waitForMsgFrom(clientName, msg, 0);
	}
	/**
	 * Hangs the thread until a certain client sends a certain msg.
	 * @param clientName
	 * @param msg
	 * @param maxTime the maximal time the thread will wait.
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) 
	 */
	private GameMessage waitForMsgFrom(String clientName, String msg, long maxTime){
		System.out.println("server waiting for " +msg+ "from "+ clientName +", maxtime: "+ String.valueOf(maxTime));
		String threadName = Thread.currentThread().getName();
		Stack<GameMessage> localMsgStack = new Stack<>();
		try {

			lastMsg.put(threadName, localMsgStack);
			
			// check if msg was recieved already
			synchronized (msgStack) {
				for (Iterator<GameMessage> iterator = msgStack.iterator(); iterator.hasNext();) {
					GameMessage gm = iterator.next();
					if (gm.getMessage().equals(msg)) {
						GameMessage ret = gm;
						iterator.remove();
						System.out.println(msg + " pulled from stack");
						return ret;
					}
				}
				
				// since msg was not recieved already
				//start listening-catchin for it, and interrupt me, when reciewed (once)
				interrupt.put(threadName, msg); //start listening for this kind of msg
				interruptCounter.put(threadName, 1);
				interruptClientName.put(threadName, clientName); //start listening for messages from spec user
				interruptThread.put(threadName, Thread.currentThread());
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
			System.out.println("server recieved " + msg);
			return localMsgStack.pop();
		} finally {
			synchronized (msgStack) {				
				//do cleanup
				lastMsg.remove(threadName);
				interrupt.remove(threadName);
				interruptClientName.remove(threadName);
				interruptCounter.remove(threadName);
				interruptThread.remove(threadName);
			}
		}
		//if not caught in time
		return null;
	}
	
}
