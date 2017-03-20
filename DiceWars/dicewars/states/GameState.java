/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dicewars.states;

import dicewars.GameBoard;
import dicewars.Main1;
import dicewars.Settings;
import dicewars.players.Player;
import dicewars.Territory;
import gameTools.state.State;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 *  Ez az osztály a játék állapotát tartalmazza. sorban megjátszatja a
 * játékosokat és kirajzolja a játékot a képernyőre
 * @author ganter
 */
public class GameState extends State{
    private GameState THIS = this;
    public boolean gameOver;
    GameBoard gameboard;
    Thread playerThread;
    
    public GameState() {
        super("GameState", Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
        
        
        inputManager.addKeyMapping("ESC", KeyEvent.VK_ESCAPE);
        inputManager.addKeyMapping("Enter", KeyEvent.VK_ENTER);
        inputManager.addKeyMapping("debug",     KeyEvent.VK_F1);
        
        inputManager.addClickMapping("ButtonLeft", MouseEvent.BUTTON1);
        
        Settings.setMapGenerator("Rectangular Hexmap");
//        Settings.setMapGenerator("Paralelloid Hexmap Pointy");
//        Settings.setMapGenerator("Hexshaped Hexmap Pointy");
//        Settings.setMapGenerator("Paralelloid Hexmap Flat");
//        Settings.setMapGenerator("Hexshaped Hexmap Flat");
//        Settings.setMapGenerator("Linear Hexmap Pointy");
//        Settings.setMapGenerator("Linear Hexmap Flat");

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHints(rh);
    }
    
    public void create(){
        Settings.layout.size = new Point(6,3);
        Settings.layout.origin = new Point(0,0);
        Settings.mapTileN = new Dimension(1,1);
        gameboard = new GameBoard(Settings.getMapGenerator() , Settings.layout);
        gameboard.generateTerritories(Settings.TerrPerPlayer);
        Settings.mapTileN = gameboard.getDimensionInTiles();
        Settings.calcLayoutSize();
        Settings.mapWidth = gameboard.getDimensions().width-2*gameboard.getZeroPointOffset().width;
        Settings.mapHeight = gameboard.getDimensions().height-2*gameboard.getZeroPointOffset().height;
        Settings.centerLayout();
        
        for(Player p : Settings.PLAYERS){
            p.reincarnate();
        }
        playerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!THIS.gameOver){
                    THIS.gameOver = true;
                    int teamAlive = -1;
                    for(Player p : Settings.PLAYERS){
                        if(p.isAlive()){
                            p.play(gameboard, inputManager);
                            if(teamAlive == -1){
                                teamAlive = p.getTeam();
                            } else if (teamAlive != p.getTeam()){
                                THIS.gameOver = false;
                            }
                        }
                    }
                }
                gameOver();
            }
        });
        playerThread.start();
        repaint();
    }
    
    private void gameOver(){
        System.out.println("GAME OVER!");
    }
    
    @Override
    public void start(){
        gameOver=false;
        create();
        super.start();
    }
    
    @Override
    public void stop(){
        gameOver=true;
        super.stop();
    }
    
    @Override
    public void render(){
        if(ticks%15 == 1) redraw();
        gameboard.render(g);
        
        if(Settings.dbg){
            //fps
            g.setColor(new Color(230,230,230));
            g.fillRect(width-80, height-15, width, height);

            String s = fpsCounter.fps() + " fps";
            int rightJustifiedBase = width-3;
            g.setFont(new Font("Courier New", Font.PLAIN, 13));
            int stringWidth = g.getFontMetrics().stringWidth(s);
            int x = rightJustifiedBase - stringWidth;

            g.setColor(Color.WHITE);
            g.drawString(s, x, height-3);
        }
    }
    
    private void redraw(){
        //clear screen
        g.setColor(new Color(230,230,230));
        g.fillRect(0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
        g.setColor(new Color(210,210,210));
        g.fillRect(0, 0, Settings.GAME_WIDTH, Settings.GAME_HEIGHT);
        //mark all cells to be repaired
        for(Territory t : gameboard.territories){
            t.touch();
        }
//        for(int i = 0; i < COLORS.length+1; i++){
//            g.drawImage(ATTACK_ICON[i], i*50, Settings.GAME_HEIGHT,45,45,this);
//        }
        gameboard.updated=true;
    }
    
    @Override
    public void update(State s){
        if (inputManager.isKeyTyped("ESC")){
            Main1.getInstance().setState("MenuState");
        }
        if (inputManager.isKeyTyped("debug")){
            Settings.dbg = !Settings.dbg;
            if(Settings.dbg){
                System.out.println("Debugging On...");
            } else {
                System.out.println("Debugging Off...");
            }
        }
        
        try{
            gameboard.setHighlitCell(gameboard.fromPixel(inputManager.getMousePos().x, inputManager.getMousePos().y));
        } catch(NullPointerException ignore){}

    }
    
}
