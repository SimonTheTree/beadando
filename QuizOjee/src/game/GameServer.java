package game;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
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
import gameTools.map.generators.MapGeneratorHexRectangleFlat;
import model.Question;
import model.RaceQuestion;
import model.User;
import view.MainWindow;
import view.Settings;


public class GameServer implements GameInputListener {
	GameHost host = null;
	
	private boolean gameFinished;
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
	
	
	MyServerListener serverListener;
	Thread serverThread;
	
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
//		User u = new User();
//		u.setUsername("egy");
//		settings.PLAYERS.add(new PlayerHuman(u, 0));
//		u.setUsername("ketto");
//		settings.PLAYERS.add(new PlayerHuman(u, 1));
//		u.setUsername("harom");
//		settings.PLAYERS.add(new PlayerHuman(u, 2));
		
		settings.layout.size = new Point(12, 8);
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
		
		serverThread = new Thread() {
			public void run() {
				try {
					System.out.println("starting up server....");
					host = new GameHost();
					host.addInputListener(GameServer.this);
					System.out.println("server ready...");
//					host.setMaxPlayers(settings.PLAYERS.size());
					host.setMaxPlayers(2);
					host.start();
					System.out.println("server launched...");
					while (!host.isStarted()) {
						System.out.println("server waiting for players...");
						Thread.sleep(1000);
					}
					System.out.println("	[GO]   server");
					
					int i = 0;
					int c = -1;
					for(String uname : host.getUserNames()){
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

					System.out.println("sending settings");
					host.broadCast(new GameMessage(Commands.SETTINGS, StringSerializer.serialize(settings)));

					System.out.println("sending gameboard");
					host.broadCast(new GameMessage(Commands.GAMEBOARD, StringSerializer.serialize(board)));
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
//					while(!gameFinished){
//						for(Player p : settings.PLAYERS){
//							String uname = p.getUser().getUsername();
//							host.sendMessage(uname, new GameMessage(Commands.YOUR_TURN));
//							// ...meanwhile a serverlistener kezeli a kliens uzeneteit
//							serverListener.waitForMsgFrom(uname, Commands.END_TURN);
//						}
//					}
					
					while(!gameFinished) {Thread.sleep(1000);}
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					host.abort();
				}
			}
		};
		
		gameFinished = false;
		serverThread.start();
	
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
	
	@Override
	public void gotMessage(GameMessage msg) { //ez masik threaden fut!!
		System.out.println("server GotMessage:" +msg.getMessage());
		msgStack.push(msg);
		if(msg.getMessage().equals(interrupt)) {
			lastMsg.push(msg);
			System.out.println("server recieved " + msg + " from " + msg.getSender() );
			if(--interruptCounter == 0){ 
				interruptThread.interrupt(); //this one should interrupt the waitFor... method
			}
		};
		new Thread(){
			public void run(){
				if(msg.getMessage().equals(Commands.ATTACK)) {
					msgStack.pop();
					String attacker = msg.getSender();
					int targetTerritoryID = Integer.parseInt(msg.getParams()[1]);
					String target = board.getTerrytoryById(targetTerritoryID).getOwner().getUser().getUsername();
					System.out.println("TARGET: " +target);
					host.broadCast(new GameMessage(Commands.ATTACK, msg.getSender(), String.valueOf(targetTerritoryID)));
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Question question = questions.pop();
					shuffleQuestion(question);
					String rightAnswer = question.getRightAnswer();
					String serializedQuestion = StringSerializer.serialize(question);
					host.sendMessage(target, new GameMessage(Commands.NORMAL_QUESTION, Commands.PARAM_YOURS, serializedQuestion));
					host.sendMessage(attacker, new GameMessage(Commands.NORMAL_QUESTION, Commands.PARAM_YOURS, serializedQuestion));
					for(Player p : settings.PLAYERS){
						if( !attacker.equals(p.getUser().getUsername()) && !target.equals(p.getUser().getUsername())){
							host.sendMessage(attacker, new GameMessage(Commands.NORMAL_QUESTION, Commands.PARAM_NOT_YOURS, serializedQuestion));					
						}
					}
					//should have recieved 2 answers...
					GameMessage[] ans =  waitForMsg(Commands.NORM_ANSWER, settings.questionTime);
					host.broadCast(new GameMessage(Commands.NORM_ANSWER, rightAnswer));
					
					if(ans.length == 2){
						host.broadCast(new GameMessage(Commands.NORM_PLAYER_ANSWER, ans[0].getParams()[0], ans[0].getSender()));
						host.broadCast(new GameMessage(Commands.NORM_PLAYER_ANSWER, ans[1].getParams()[0], ans[1].getSender()));
						String ansAttacker;
						String ansDef; 
						if(attacker.equals(ans[0].getSender()) ){
							ansAttacker = ans[0].getParams()[0];
							ansDef = ans[1].getParams()[0];
						} else {
							ansAttacker = ans[1].getParams()[0];					
							ansDef = ans[0].getParams()[0];
						}
						//both answers are right
						if( ansAttacker.equals( ansDef ) && rightAnswer.equals(ansAttacker)){
							//another raceQuestion round
							RaceQuestion rQuestion = RaceQuestions.pop();
							int rightRQAnswer = rQuestion.getRightAnswer();
							rQuestion.setRightAnswer(Integer.MIN_VALUE); 
							String serializedRQuestion = StringSerializer.serialize(rQuestion);
							host.sendMessage(target, new GameMessage(Commands.RACE_QUESTION, Commands.PARAM_YOURS, serializedRQuestion));
							host.sendMessage(attacker, new GameMessage(Commands.RACE_QUESTION, Commands.PARAM_YOURS, serializedRQuestion));
							for(Player p : settings.PLAYERS){
								if( !attacker.equals(p.getUser().getUsername()) && !target.equals(p.getUser().getUsername())){
									host.sendMessage(attacker, new GameMessage(Commands.RACE_QUESTION, Commands.PARAM_NOT_YOURS, serializedRQuestion));					
								}
							}
							//should have recieved 2 answers...
							GameMessage[] RQans =  waitForMsg(Commands.RQ_ANSWER, settings.raceTime);
							host.broadCast(new GameMessage(Commands.RQ_ANSWER, rightAnswer));
							if (RQans.length == 2){
								host.broadCast(new GameMessage(Commands.RQ_PLAYER_ANSWER, RQans[0].getParams()[0], RQans[0].getSender()));
								host.broadCast(new GameMessage(Commands.RQ_PLAYER_ANSWER, RQans[1].getParams()[0], RQans[1].getSender()));
								double attAns;
								double defAns;
								if(attacker.equals(RQans[0].getSender()) ){
									attAns = Double.parseDouble(RQans[0].getParams()[0]);
									defAns = Double.parseDouble(RQans[1].getParams()[0]);
								} else {
									attAns = Double.parseDouble(RQans[1].getParams()[0]);					
									defAns = Double.parseDouble(RQans[0].getParams()[0]);
								}
								if(Double.compare(attAns, defAns) == 0){ //a gyorsabbik nyer
									host.broadCast(new GameMessage(Commands.NEW_OWNER, RQans[0].getSender(), String.valueOf(targetTerritoryID)));
								} else {
									if(Math.abs(rightRQAnswer-attAns) < Math.abs(rightRQAnswer-defAns)){
										host.broadCast(new GameMessage(Commands.NEW_OWNER, attacker, String.valueOf(targetTerritoryID)));
									} else {
										//nothin changes
									}
								}
							} else {
								host.broadCast(new GameMessage(Commands.RQ_PLAYER_ANSWER, RQans[0].getParams()[0], RQans[0].getSender()));
								if(attacker.equals(RQans[0].getSender())){
									host.broadCast(new GameMessage(Commands.NEW_OWNER, attacker, String.valueOf(targetTerritoryID)));
								} else {
									//nothin happens
								}
							}
						} else {
							//only one right answer
							if(rightAnswer.equals(ansAttacker)){
								host.broadCast(new GameMessage(Commands.NEW_OWNER, attacker, String.valueOf(targetTerritoryID)));
							}
							if(rightAnswer.equals(ansDef)){
								//nothin changes
							}
						}
					} else { 
						host.broadCast(new GameMessage(Commands.NORM_PLAYER_ANSWER, ans[0].getParams()[0], ans[0].getSender()));
						if(attacker.equals(ans[0].getSender()) && rightAnswer.equals(ans[0].getParams()[0])){
							host.broadCast(new GameMessage(Commands.NEW_OWNER, attacker, String.valueOf(targetTerritoryID)));
						} else {
							//nothin happens
						}
					}
				} else {
				};
			}		
		}.start();
		
	}
	
	
	
	/**
	 * Hangs the thread until all clients send a certain msg.
	 * @param msg
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) 
	 */
	public GameMessage[] waitForMsg(String msg){
		return waitForMsg(msg, 0);
	}
	/**
	 * Hangs the thread until all clients send a certain msg.
	 * @param msg
	 * @param maxTime the maximal time the thread will wait.
	 * @return the first n {@link GameMessage}s of type msg that were recieved (n = num of clients) 
	 */
	public GameMessage[] waitForMsg(String msg, long maxTime){
		lastMsg.clear();
		
		interruptCounter = settings.PLAYERS.size();
		
		//check if msg was recieved already
		for (GameMessage gm : msgStack){
			if(gm.getMessage().equals(msg)){
				lastMsg.push(gm);
				msgStack.remove(gm);
				System.out.println( msg + " pulled from stack");
			
				if(--interruptCounter == 0){ 
					return lastMsg.toArray(new GameMessage[0]);
				}
			}
		}
		
		interruptThread = Thread.currentThread();
		try{
			interrupt = msg; // start listening-collecting-interrupting for "msg" messages
			
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
		return null;
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
		//check if msg was recieved already
		for (GameMessage gm : msgStack){
			if(gm.getMessage().equals(msg)){
				msgStack.remove(gm);
				System.out.println( msg + " pulled from stack");
				return gm;
			}
		}
		
		interruptCounter = 1;
		interruptThread = Thread.currentThread();
		try {
			interrupt = msg;
			if(maxTime != 0){
				System.out.println("server waiting for " + msg);
				Thread.sleep(maxTime);
			} else {
				while(true){
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
