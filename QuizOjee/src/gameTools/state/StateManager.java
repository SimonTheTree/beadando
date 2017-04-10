package gameTools.state;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import javax.swing.JFrame;

/**
 * This class is managing a games states. use the start- stop- and setCurrentState
 * methods to manage your states.
 * @author ganter (idea from The Java Hub)
 * @see State
 */
public class StateManager {
    
    private ArrayList<State> states;
    
    private int currentState;
    
    private JFrame gameFrame;
    
    public StateManager(JFrame j){
        gameFrame = j;
        states = new ArrayList<>();
    }
    
    public void addState(State s){
        states.add(s);
    }
    
    public void startCurrentState(){
//        System.out.println("starting state: "+states.get(currentState).name);
        try{
            gameFrame.add(states.get(currentState));
            gameFrame.pack();
            states.get(currentState).start();
            states.get(currentState).requestFocus();
        } catch(IndexOutOfBoundsException e){
            System.err.println("[ERROR] IndexOutOfBoundsException - propably because there are no states registered!");
            e.printStackTrace();
        }
    }
    
    public void stopCurrentState(){
//        System.out.println("stopping state: "+states.get(currentState).name);
        states.get(currentState).stop();
        gameFrame.remove(states.get(currentState));
    }
    
    public void setCurrentState(String s){
//        System.out.println("setting state to: "+s);
        for(int i = 0; i < states.size(); i++){
            if(s.equals(states.get(i).name)){
                currentState = i;
            }
        }
    }
    
}
