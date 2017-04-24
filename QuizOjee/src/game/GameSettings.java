package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.List;

import com.jcraft.jsch.jce.Random;

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
import view.Settings;

public class GameSettings {
	
	public static Tile usedTile = new Cell(0, 0);
    public static int volume;
    public static boolean generateHolesInMap;
    
    /* ----- GAME FLAGS ----- */
    public static boolean dbg = false;
    
    /* ----- CONSTANTS ----- */
    public static final Random RANDOM = new Random();
    public static final Color[] COLORS = new Color[]{
        new Color(155,  5,  0),
        new Color(160, 90, 20),
        new Color(255,216,  0),
        new Color(  5,120,  0),
        new Color( 75,  0,125),
        new Color(255,120,140),
        new Color(0,0,0)
    };
	
	public static String game_currentGameMap;
	public static List<Player> game_players;

	
	/* ----- MAP OPTIONS ----- */
	public static int TerrPerPlayer=7;
    private static int mapParamX = 50;
    private static int mapParamY = 50;
    public static int getMapParamX() {
        return mapParamX;
    }
    public static int getMapParamY() {
        return mapParamY;
    }
    public static void setMapParamX(int mapWidth) {
        mapParamX = mapWidth;
        getMapGenerator().setParameters(mapParamX, mapParamY);
    }
    public static void setMapParamY(int mapHeight) {
        mapParamY = mapHeight;
        getMapGenerator().setParameters(mapParamX, mapParamY);
    }
    
    /* ----- MAP LAYOUT OPTIONS ----- */
    public static Layout layout = new Layout(Orientation.LAYOUT_POINTY, new Point(6,3), new Point(0,0));
    
    public static Dimension dim = new Dimension(2,1); //!!!!!!!!!!!!!!!!!!!FIX
    public static Dimension mapTileN = new Dimension(1,1);
    public static int getCellWidth(){    
        if (layout.orientation == Orientation.LAYOUT_FLAT){
            return (int) (Settings.MAIN_WINDOW_WIDTH / (double) (2*mapTileN.width));
        } else 
        if (layout.orientation == Orientation.LAYOUT_POINTY){
            return (int) (Settings.MAIN_WINDOW_WIDTH / (Math.cos(Math.PI/6)*2*mapTileN.width));
        } else{
            System.out.println("error with orientation (Settings.java)");
            return 10;
        }
    }
    public static int getCellHeight(){
        if (layout.orientation == Orientation.LAYOUT_FLAT){
            return (int) (Settings.MAIN_WINDOW_HEIGHT / (Math.sin(Math.PI/3)*2*mapTileN.height));
        } else 
        if (layout.orientation == Orientation.LAYOUT_POINTY){
            return (int) (Settings.MAIN_WINDOW_HEIGHT / (double)(2*mapTileN.height));
        } else{
            System.out.println("error with orientation (Settings.java)");
            return 10;
        }
    }
    
    
    public static int mapWidth = 400;
    public static int mapHeight = 400;
    
    public static void setLayoutOrientation(Orientation o){
        layout.orientation = o;
    }
    public static void centerLayout(){
        layout.origin =new Point((Settings.MAIN_WINDOW_WIDTH-mapWidth)/2,(Settings.MAIN_WINDOW_HEIGHT-mapHeight)/2);
    }
    public static void calcLayoutSize(){
        layout.size = new Point(getCellWidth(),getCellHeight());
    }	
    
    private static final MapGenerator[] mapGeneratorsPointy = new MapGenerator[]{
            new MapGeneratorHexParalelogram<>("Paralelloid Hexmap Pointy", new Cell(0,0), mapParamX, mapParamY),
            new MapGeneratorHexHexagonPointy<>("Hexshaped Hexmap Pointy", new Cell(0,0), mapParamX),
            new MapGeneratorHexLinePointy<>("Linear Hexmap Pointy", new Cell(0,0), mapParamX),
        };
        private static final MapGenerator[] mapGeneratorsFlat = new MapGenerator[]{
            new MapGeneratorHexRectangleFlat<>("Rectangular Hexmap", new Cell(0,0), mapParamX, mapParamY),
            new MapGeneratorHexParalelogram<>("Paralelloid Hexmap Flat", new Cell(0,0), mapParamX, mapParamY),
            new MapGeneratorHexHexagonPointy<>("Hexshaped Hexmap Flat", new Cell(0,0), mapParamX),
            new MapGeneratorHexLineFlat<>("Linear Hexmap Flat", new Cell(0,0), mapParamX),
        };
        private static MapGenerator currentGenerator = mapGeneratorsPointy[1];

        public static void setMapGenerator(String gen){
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
        public static MapGenerator getMapGenerator(){
            return currentGenerator;
        }
        public static MapGenerator[] getMapGenerators(){
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
}
