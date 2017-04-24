/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.players;

import game.GameBoard;
import game.Territory;
import gameTools.state.InputManager;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author ganter
 */
public class PlayerAI extends Player {
 
    public PlayerAI(){
        super();
    };
    
    public PlayerAI(int color, int team) {
        super(color, team);
    }
    public PlayerAI(int color) {
        super(color);
    }

    @Override
    public void selectBase(GameBoard board, InputManager input) throws EndOfTurnException{
        try{
            //get all owned territories, that have attackable neighbors
            ArrayList<Territory> validBase = new ArrayList<>();
            for(Territory t : territories){
                ArrayList<Territory> tn = t.getNeighborTerritories(board);
                for (Territory next : tn) {                        
                    if(next.getOwner().getTeam() != team && t.getStrength() > 1){
                        if(!validBase.contains(t)) validBase.add(t);
                    }
                }
            }
            if(validBase.isEmpty()) throw new EndOfTurnException();

            //select the strongest one [relative to the surrounding - hint for better ai]
            Territory strongest = validBase.get(0);
            for(Territory t : validBase){
                if(t.getStrength() > strongest.getStrength()) strongest = t;
            }

            if(strongest.getStrength() == 1) throw new EndOfTurnException();

            board.selectBase(strongest);

            Thread.sleep(50);
        } catch(NullPointerException | InterruptedException ignore){}
    }
    
    @Override
    public void selectTarget(GameBoard board, InputManager input) throws EndOfTurnException{
        try{
            //attack the strongest one still weaker of the neighbors
            Territory base = board.getSelectedBase();

            ArrayList<Territory> validTarget = new ArrayList<>();
            ArrayList<Territory> tn = base.getNeighborTerritories(board);
            for (Territory next : tn) {                        
                if(next.getOwner().getTeam() != team && next.getStrength() <= base.getStrength()){
                    if(!validTarget.contains(next)) validTarget.add(next);
                }
            }

            if(validTarget.isEmpty()) throw new EndOfTurnException();

            Territory target = validTarget.get(0);
            for (Territory t : validTarget) {    
                if(target.getStrength() <= t.getStrength() ) target = t;
            } 

            board.selectTarget(target);
            Thread.sleep(50);
        } catch(NullPointerException | InterruptedException ignore){}
    }
    
}
