/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.players;

import game.GameBoard;
import game.GameSettings;
import game.Territory;
import gameTools.state.InputManager;
import model.RaceQuestion;
import model.User;
import model.Question;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *  Abstaract jatekos osztaly, mely tulajdonkeppen csak a jatekos interfesz miatt
 * fontos, hogy legyen.
 * @author ganter
 */
public abstract class Player implements Serializable{
    
    protected int team;
    protected int color;
    protected ArrayList<Territory> territories;
    protected boolean isAlive;
    protected User user;
    
    public Player(){
        this(null, 0);
    };
    public Player(User u){
    	this(u, 0);
    };
    public Player(User u, int color) {
        this(u, color, 0);
        this.team = getId();
    }
    public Player(User u, int color, int team) {
        this.user = u;
    	this.color = color;
        this.team = team;
        territories = new ArrayList<>();
        GameSettings.getInstance().PLAYERS.add(this);
    }

    public User getUser(){
    	return user;
    }
    
    public Color getColor() {
        return GameSettings.getInstance().COLORS[color];
    }
    public int getColorID() {
        return color;
    }

    public int getId() {
        return GameSettings.getInstance().PLAYERS.indexOf(this);
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
        GameSettings.getInstance().PLAYERS.remove(this);
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
    
    public abstract void selectTarget(GameBoard board, InputManager input);
    
    public abstract String askQuestion(Question quest);
    
    public abstract double askRaceQuestion(RaceQuestion quest);
    
    public void play(GameBoard board, InputManager input){
        board.setCurrentPlayer(this);
        selectTarget(board, input);
        board.evaluateMove();

        board.finishRound(this);
    }
    
}
