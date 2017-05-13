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
import model.Statistics;
import model.User;
import model.exceptions.UserNotFoundException;
import view.MainWindow;
import model.Question;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    protected Statistics globStats;
    protected Statistics localStats;
    private int[][] diffN; //number of answered questions in a difficulity
    private int questionsAsked = 0;
    public double points;
    public double statsPoints;
    
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
        this.user = MainWindow.getInstance().controller.getUser(u.getUsername());
        this.globStats = MainWindow.getInstance().controller.getUserStatistics(u.getUsername());
    	this.color = color;
        this.team = team;
        points = 0;
        statsPoints = globStats.getPoints();
        territories = new ArrayList<>();
        localStats = new Statistics();
        	localStats.setUname(u.getUsername());
        	localStats.setPoints(0);      
        	localStats.setWins(0);        
        	localStats.setDefeats(0);     
        	localStats.setRightAnswers(0);
        	localStats.setWrongAnswers(0);
        	localStats.setRightTips(0);   
        	localStats.setWrongTips(0);

    	//init difficulitycounter array
		diffN =  new int[15][2];
		int i = 0;
		for(int[] arr : diffN){
			arr[0] = i++;
			arr[1] = 0;
		}
    }
    
    public void save() {
    	try {
    		globStats.setPoints( (int) (statsPoints + points));
    		localStats.setPoints( (int) (points));
			MainWindow.getInstance().controller.updateStatistics(globStats);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
		}
    }
    public void incRAnswer() {
    	globStats.setRightAnswers(globStats.getRightAnswers()+1);
    	localStats.setRightAnswers(localStats.getRightAnswers()+1);
    }
    public void incWAnswer() {
    	globStats.setWrongAnswers(globStats.getWrongAnswers()+1);
    	localStats.setWrongAnswers(localStats.getWrongAnswers()+1);
    }
    public void incRTip() {
    	globStats.setRightTips(globStats.getRightTips()+1);
    	localStats.setRightTips(localStats.getRightTips()+1);
    }
    public void incWTip() {
    	globStats.setWrongTips(globStats.getWrongTips()+1);
    	localStats.setWrongTips(localStats.getWrongTips()+1);
    }
    public void incWins() {
    	globStats.setWins(globStats.getWins()+1);
    }
    public void incDefeats() {
    	globStats.setDefeats(globStats.getDefeats()+1);
    	localStats.setDefeats(localStats.getDefeats()+1);
    }
    public void incDiffN(int diff) {
    	diffN[diff][1]++;
    }
    public int[][] getDiffN(){
    	return diffN;
    }
    public void incQuestionsAsked() {
    	questionsAsked++;
    }
    public int getQuestionsAsked() {
    	return questionsAsked;
    }
    public Statistics getGameStats() {
    	return localStats;
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
        return GameSettings.getInstance().players.indexOf(this);
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
    public List<Territory> getTerritories(){
        return territories;
    }
    public void removeTerritory(Territory t){
        territories.remove(t);
    }
    
    public void dispose(){
        GameSettings.getInstance().players.remove(this);
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
    
    public abstract void selectTarget(GameBoard board, InputManager input) throws EndOfTurnException;
    
    public abstract String askQuestion(Question quest);
    
    public abstract double askRaceQuestion(RaceQuestion quest);
    
    public void play(GameBoard board, InputManager input) throws EndOfTurnException{
        board.setCurrentPlayer(this);
        selectTarget(board, input);
        board.evaluateMove();

        board.finishRound(this);
    }
    
}
