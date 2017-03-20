/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dicewars;

import dicewars.states.GameState;
import dicewars.Settings;
import dicewars.states.MenuState;
import dicewars.states.SettingsState;
import gameTools.state.StateManager;
import java.util.Random;
import javax.swing.JFrame;

/**
 *
 * @author ganter
 */
public class Main1 extends JFrame{
    
    public static int numOfPlayers;
    
    private StateManager sm = new StateManager(this);
    private GameState game;
    private MenuState menu;
    private SettingsState settings;
    
    private Main1(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setSize(GAME_WIDTH,GAME_HEIGHT);
        this.setResizable(false);
        this.setTitle("DiceWars");
        this.setLocationRelativeTo(null);
        
        Settings.init();
        
        game = new GameState();
        menu = new MenuState();
        settings = new SettingsState();
        
        sm.addState(game);
        sm.addState(menu);
        sm.addState(settings);
        
        sm.setCurrentState("MenuState");
        sm.startCurrentState();
        this.setVisible(true);
    }
    
    public void setState(String s){
        sm.stopCurrentState();
        sm.setCurrentState(s);
        sm.startCurrentState();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
    }
    private static Main1 main;
    
    public static Main1 getInstance(){
        if (main == null){
            main = new Main1();
        } 
        return main;
    }
    
    
}
