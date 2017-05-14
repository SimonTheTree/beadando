package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controller.Controller;
import game.players.Player;
import gameTools.map.Layout;
import gameTools.map.Orientation;
import gameTools.map.Tile;
import gameTools.map.generators.MapGenerator;
import gameTools.map.generators.MapGeneratorHexHexagonPointy;
import gameTools.map.generators.MapGeneratorHexLineFlat;
import gameTools.map.generators.MapGeneratorHexLinePointy;
import gameTools.map.generators.MapGeneratorHexParalelogram;
import gameTools.map.generators.MapGeneratorHexRectangleFlat;
import view.MainWindow;
import view.Settings;

public class GameSettings implements Serializable{
	
	/* ----- EXPERIMENTAL ----- */
//  public static Territory usedTerritory = new TerritoryHexMulti();
  public Tile usedTile = new Cell(0, 0);
  public int volume;
  public boolean generateHolesInMap;
  
  /* ----- GAME FLAGS ----- */
  public boolean dbg = false;
  
  /* ----- CONSTANTS ----- */
  public final Random RANDOM = new Random();
  public final Color[] COLORS = new Color[]{
      new Color(155,  5,  0),
      new Color(160, 90, 20),
      new Color(255,216,  0),
      new Color(  5,120,  0),
      new Color( 75,  0,125),
      new Color(255,120,140),
      new Color(0,0,0)
  };
  public final int questionTime = 20000; //in microseconds
  public final int raceTime = 20000; //in microseconds
  public int showRightAnswerDelay = 2000; //ms
  
  /* ----- GAME DIMENSIONS ----- */
  public int SCREEN_WIDTH = Settings.MAIN_WINDOW_WIDTH;
  public int SCREEN_HEIGHT = Settings.MAIN_WINDOW_HEIGHT;
  public int GAME_INFOLABEL_HEIGHT =(int) (SCREEN_WIDTH * 1/6.0);
  public int GAME_WIDTH = SCREEN_WIDTH;
  public int GAME_HEIGHT = SCREEN_HEIGHT-GAME_INFOLABEL_HEIGHT;
  
  /* ----- GAME VARIABLES ----- */
  public int numOfRounds;
  public String gameType;
  public List<Player> players = new ArrayList<>();
  public int getNumOfPlayers(){
      return players.size();
  }
  /**
   * Finds player with specified username in PLAYERS
   * @param uname
   * @return the {@link Player} or null if not found
   */
  public Player getPlayerByUname(String uname){
	  for (Player p : players){
		  if(uname.equals(p.getUser().getUsername())){
			  return p;
		  }
	  }
	  return null;
  }
  
  /* ----- MAP OPTIONS ----- */
//  public static boolean allowHoles = true;
  public int TerrPerPlayer=7;
  private int mapParamX = 50;
  private int mapParamY = 50;
  public int getMapParamX() {
      return mapParamX;
  }
  public int getMapParamY() {
      return mapParamY;
  }
  public void setMapParamX(int mapWidth) {
      mapParamX = mapWidth;
      getMapGenerator().setParameters(mapParamX, mapParamY);
  }
  public void setMapParamY(int mapHeight) {
      mapParamY = mapHeight;
      getMapGenerator().setParameters(mapParamX, mapParamY);
  }

  private final MapGenerator[] mapGeneratorsPointy = new MapGenerator[]{
      new MapGeneratorHexParalelogram<>("Paralelloid Hexmap Pointy", new Cell(0,0), mapParamX, mapParamY),
      new MapGeneratorHexHexagonPointy<>("Hexshaped Hexmap Pointy", new Cell(0,0), mapParamX),
      new MapGeneratorHexLinePointy<>("Linear Hexmap Pointy", new Cell(0,0), mapParamX),
  };
  private final MapGenerator[] mapGeneratorsFlat = new MapGenerator[]{
      new MapGeneratorHexRectangleFlat<>("Rectangular Hexmap", new Cell(0,0), mapParamX, mapParamY),
      new MapGeneratorHexParalelogram<>("Paralelloid Hexmap Flat", new Cell(0,0), mapParamX, mapParamY),
      new MapGeneratorHexHexagonPointy<>("Hexshaped Hexmap Flat", new Cell(0,0), mapParamX),
      new MapGeneratorHexLineFlat<>("Linear Hexmap Flat", new Cell(0,0), mapParamX),
  };
  private MapGenerator currentGenerator = mapGeneratorsPointy[1];

  public void setMapGenerator(String gen){
      for(MapGenerator g : mapGeneratorsPointy){
          if(g.name.equals(gen)){
              currentGenerator = g;
              setLayoutOrientation(Orientation.LAYOUT_POINTY);
              getMapGenerator().setParameters(mapParamX, mapParamY);
              return;
          }
      }
      for(MapGenerator g : mapGeneratorsFlat){
          if(g.name.equals(gen)){
              currentGenerator = g;
              setLayoutOrientation(Orientation.LAYOUT_FLAT);
              getMapGenerator().setParameters(mapParamX, mapParamY);
              return;
          }
      }
  }
  public MapGenerator getMapGenerator(){
      return currentGenerator;
  }
  public MapGenerator[] getMapGenerators(){
      MapGenerator[] gens = new MapGenerator[mapGeneratorsFlat.length+mapGeneratorsPointy.length];
      int i = 0;
      for(;i<mapGeneratorsFlat.length;i++){
          gens[i] = mapGeneratorsFlat[i];
      }
      for(int j = i; j<gens.length; j++){
          gens[j] = mapGeneratorsPointy[j-i];
      }
      return gens;
  }
  
  /* ----- MAP LAYOUT OPTIONS ----- */
  public Layout layout = new Layout(Orientation.LAYOUT_POINTY, new Point(6,3), new Point(0,0));
  
  public Dimension dim = new Dimension(2,1); //!!!!!!!!!!!!!!!!!!!FIX
  public Dimension mapTileN = new Dimension(1,1);
  public int getCellWidth(){    
      if (layout.orientation == Orientation.LAYOUT_FLAT){
          return (int) (GAME_WIDTH / (double) (2*mapTileN.width));
      } else 
      if (layout.orientation == Orientation.LAYOUT_POINTY){
          return (int) (GAME_WIDTH / (Math.cos(Math.PI/6)*2*mapTileN.width));
      } else{
          System.out.println("error with orientation (Settings.java)");
          return 10;
      }
  }
  public int getCellHeight(){
      if (layout.orientation == Orientation.LAYOUT_FLAT){
          return (int) (GAME_HEIGHT / (Math.sin(Math.PI/3)*2*mapTileN.height));
      } else 
      if (layout.orientation == Orientation.LAYOUT_POINTY){
          return (int) (GAME_HEIGHT / (double)(2*mapTileN.height));
      } else{
          System.out.println("error with orientation (Settings.java)");
          return 10;
      }
  }
  
  
  public int mapWidth = 400;
  public int mapHeight = 400;
  
  public void setLayoutOrientation(Orientation o){
      layout.orientation = o;
  }
  public void centerLayout(){
      layout.origin =new Point((GAME_WIDTH-mapWidth)/2,(GAME_HEIGHT-mapHeight)/2);
  }
  public void calcLayoutSize(){
      layout.size = new Point(getCellWidth(),getCellHeight());
  }
  
  //-------- SINGLETON SELF ---------//
  private static GameSettings self = null;
  public static GameSettings getInstance(){
  	if (self == null){
  		self = new GameSettings();
  	}
  	return self;
  }
  public static void setInstance(GameSettings newSettings){
	  self = newSettings;
  }
  private GameSettings(){};
  
}
