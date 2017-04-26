package game;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import controller.Commands;
import controller.GameHost;
import controller.GameInputListener;
import controller.GameMessage;
import game.players.Player;
import game.players.PlayerHuman;
import gameTools.map.generators.MapGeneratorHexRectangleFlat;


public class GameServer  {

	GameBoard board;
	Player[] players = new Player[3];
	boolean gameFinished;
	GameSettings settings;
	
	MyServerListener serverListener;
	Thread serverThread;
	
	public void createGame(){
		settings = GameSettings.getInstance();
		settings.TerrPerPlayer = 5;
		settings.setMapGenerator("Rectangular Hexmap");
		// settings.setMapGenerator("Paralelloid Hexmap Pointy");
		// settings.setMapGenerator("Hexshaped Hexmap Pointy");
		// settings.setMapGenerator("Paralelloid Hexmap Flat");
		// settings.setMapGenerator("Hexshaped Hexmap Flat");
		// settings.setMapGenerator("Linear Hexmap Pointy");
		// settings.setMapGenerator("Linear Hexmap Flat");
		
		settings.layout.size = new Point(6, 3);
		settings.layout.origin = new Point(0, 0);
		settings.mapTileN = new Dimension(1, 1);
		MapGeneratorHexRectangleFlat<Cell> gen = new MapGeneratorHexRectangleFlat<Cell>("name", new Cell(0,0), new int[]{50, 50});
		List<Cell> l = gen.generate();
		board = new GameBoard(gen, settings.layout);
		settings.PLAYERS.add(new PlayerHuman(0));
		settings.PLAYERS.add(new PlayerHuman(1));
		settings.PLAYERS.add(new PlayerHuman(2));
		board.generateTerritories(settings.TerrPerPlayer);
		settings.mapTileN = board.getDimensionInTiles();
		settings.calcLayoutSize();
		settings.mapWidth = board.getDimensions().width - 2 * board.getZeroPointOffset().width;
		settings.mapHeight = board.getDimensions().height - 2 * board.getZeroPointOffset().height;
		settings.centerLayout();
		
//		players[0] = settings.PLAYERS.get(0);
//		players[1] = settings.PLAYERS.get(1);
//		players[2] = settings.PLAYERS.get(2);
		
		serverThread = new Thread() {
			public void run() {
				GameHost host = null;
				try {
					System.out.println("starting up server....");
					host = new GameHost();
					serverListener = new MyServerListener(this);
					host.addInputListener(serverListener);
					System.out.println("server launched...");
					while (!host.isStarted()) {
						System.out.println("server waiting for players...");
						Thread.sleep(3000);
					}
					System.out.println("waited for long enough...");

					host.broadCast(new GameMessage(Commands.SETTINGS, StringSerializer.serialize(settings)));
					System.out.println("sending gameboard");
					System.out.println("territories: " + board.territories.length);
					int c = 0;
					for(Territory t : board.territories){
						c += t.getCells().size();
					}
					System.out.println("cells: " +c);
					
					host.broadCast(new GameMessage(Commands.GAMEBOARD, StringSerializer.serialize(board)));
					
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
	
}
