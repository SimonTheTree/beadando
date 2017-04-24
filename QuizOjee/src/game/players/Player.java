/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.players;

import game.GameBoard;
import view.Settings;
import game.Territory;
import gameTools.state.InputManager;
import java.awt.Color;
import java.util.ArrayList;

/**
 *  Abstaract játékos osztály, mely tulajdonképpen csak a játékos interfész miatt
 * fontos, hogy legyen.
 * @author ganter
 */
public abstract class Player {
    
    protected int team;
    protected int color;
    protected ArrayList<Territory> territories;
    protected boolean isAlive;
    
    public Player(){
        this(0);
    };
    public Player(int color) {
        this(color,0);
        this.team = getId();
    }
    public Player(int color, int team) {
        this.color = color;
        this.team = team;
        territories = new ArrayList<>();
        Settings.game_players.add(this);
    }

    public Color getColor() {
        return Settings.COLORS[color];
    }
    public int getColorID() {
        return color;
    }

    public int getId() {
        return Settings.PLAYERS.indexOf(this);
    }
    
    public int getTerritoryNum(){
        return territories.size();
    }
    
    public int getTeam(){
        return this.team;
    }
    
    public boolean isOwner(Territory t){
        for(Territory t2 : territories){
            if(t2.equals(t)){
                return true;
            }
        }
        return false;
    }
    
    public void addTerritory(Territory t){
        territories.add(t);
    }
    public ArrayList<Territory> getTerritories(){
        return territories;
    }
    public void removeTerritory(Territory t){
        territories.remove(t);
    }
    
    public void dispose(){
        Settings.PLAYERS.remove(this);
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public void setColor(int color) {
        this.color = color;
    }
    
    public boolean isAlive(){
        return isAlive;
    }
    
    public void kill(){
        this.isAlive=false;
    }
    
    public void reincarnate(){
        this.isAlive = true;
    }
    
    public abstract void selectBase(GameBoard board, InputManager input)  throws EndOfTurnException;
    
    public abstract void selectTarget(GameBoard board, InputManager input)  throws EndOfTurnException;
    
    public void play(GameBoard board, InputManager input){
        board.setCurrentPlayer(this);
        boolean endOfTurn=false;
        while(!endOfTurn){
            try{
                selectBase(board, input);
                selectTarget(board, input);
                board.evaluateMove();
            }catch(EndOfTurnException e){
                endOfTurn=true;
            }
        }
        board.finishRound(this);
    }
    
}
