/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dicewars;

import dicewars.players.Player;
import dicewars.src.ResourceLoader;
import gameTools.DyeImage;
import gameTools.map.Layout;
import gameTools.map.Orientation;
import gameTools.map.Tile;
import gameTools.map.TileHex;
import gameTools.map.generators.MapGenerator;
import gameTools.map.generators.MapGeneratorHexHexagonPointy;
import gameTools.map.generators.MapGeneratorHexLineFlat;
import gameTools.map.generators.MapGeneratorHexLinePointy;
import gameTools.map.generators.MapGeneratorHexParalelogram;
import gameTools.map.generators.MapGeneratorHexRectangleFlat;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/**
 * ebben az osztályban van letározva minden játék állapot, és z összes beállítás.
 * @author ganter
 */
public final class Settings {
    
    /* ----- EXPERIMENTAL ----- */
//    public static Territory usedTerritory = new TerritoryHexMulti();
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

    public static final BufferedImage[][] DICES = new BufferedImage[6][COLORS.length+1];
    
    public static final BufferedImage[] ATTACK_ICON = new BufferedImage[COLORS.length+1];
    public static final BufferedImage[] PLAYER_ICON = new BufferedImage[COLORS.length+1];
    public static final BufferedImage[] SHIELD_ICON = new BufferedImage[COLORS.length+1];
    public static final BufferedImage ADDUSER_ICON = ResourceLoader.getImage("graphics/addUserIcon.png");
    public static final BufferedImage GROUP_ICON =   ResourceLoader.getImage("graphics/groupIcon.png");
    public static final BufferedImage TITLE_SCR_BG =   ResourceLoader.getImage("graphics/background.png");
    
    /* ----- GAME DIMENSIONS ----- */
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;
    public static final int GAME_INFOLABEL_HEIGHT =(int) (SCREEN_WIDTH * 1/6.0);
    public static final int GAME_WIDTH = SCREEN_WIDTH;
    public static final int GAME_HEIGHT = SCREEN_HEIGHT-GAME_INFOLABEL_HEIGHT;
    
    /* ----- GAME VARIABLES ----- */
    public static final ArrayList<Player> PLAYERS = new ArrayList<>();
    public static int getNumOfPlayers(){
        return PLAYERS.size();
    }
    
    /* ----- MAP OPTIONS ----- */
//    public static boolean allowHoles = true;
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
        Settings.mapParamX = mapWidth;
        getMapGenerator().setParameters(mapParamX, mapParamY);
    }
    public static void setMapParamY(int mapHeight) {
        Settings.mapParamY = mapHeight;
        getMapGenerator().setParameters(mapParamX, mapParamY);
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
    
    /* ----- MAP LAYOUT OPTIONS ----- */
    public static Layout layout = new Layout(Orientation.LAYOUT_POINTY, new Point(6,3), new Point(0,0));
    
    public static Dimension dim = new Dimension(2,1); //!!!!!!!!!!!!!!!!!!!FIX
    public static Dimension mapTileN = new Dimension(1,1);
    public static int getCellWidth(){    
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
    public static int getCellHeight(){
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
    
    
    public static int mapWidth = 400;
    public static int mapHeight = 400;
    
    public static void setLayoutOrientation(Orientation o){
        layout.orientation = o;
    }
    public static void centerLayout(){
        layout.origin =new Point((GAME_WIDTH-mapWidth)/2,(GAME_HEIGHT-mapHeight)/2);
    }
    public static void calcLayoutSize(){
        layout.size = new Point(getCellWidth(),getCellHeight());
    }
    
    public static void init(){
        
        //DICES[i][j] = dice vith value i and color j
        for(int i = 0; i < DICES.length; i++){
            DICES[i][0] = ResourceLoader.getImage("graphics/dices/Kocka"+(i+1)+".png");
        }
        for(int i = 0; i < DICES.length; i++){
            
            for(int j = 0; j < COLORS.length; j++){
                DICES[i][j+1] = DyeImage.dye(ResourceLoader.getImage("graphics/dices/Kocka"+(i+1)+".png"), DyeImage.addTransparency(COLORS[j], 125));
            }
        }
        
        ATTACK_ICON[0] = ResourceLoader.getImage("graphics/attackIcon.png");
        for(int i = 0; i < COLORS.length; i++){
        ATTACK_ICON[i+1] = DyeImage.dye(ATTACK_ICON[0], DyeImage.addTransparency(COLORS[i], 230));
        }
        
        PLAYER_ICON[0] = ResourceLoader.getImage("graphics/PlayerIcon.png");
        for(int i = 0; i < COLORS.length; i++){
        PLAYER_ICON[i+1] = DyeImage.dye(PLAYER_ICON[0], DyeImage.addTransparency(COLORS[i], 230));
        }
        
        SHIELD_ICON[0] = ResourceLoader.getImage("graphics/shieldIcon.png");
        for(int i = 0; i < COLORS.length; i++){
        SHIELD_ICON[i+1] = DyeImage.dye(SHIELD_ICON[0], DyeImage.addTransparency(COLORS[i], 230));
        }
    };
}
