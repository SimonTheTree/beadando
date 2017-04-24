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

/**
 *  emberi játékos, billentyűzetről és egérről kér be
 * @author ganter
 */
public class PlayerHuman extends Player{
    
    public PlayerHuman(){
        super();
    };
    
    public PlayerHuman(int color, int team) {
        super(color, team);
    }
    public PlayerHuman(int color) {
        super(color);
    }
    
    @Override
    public void selectBase(GameBoard board, InputManager input) throws EndOfTurnException{
        while(true){
            try{
                if(input.isKeyTyped("Enter")){
                    throw new EndOfTurnException();
                }
                if(input.isClicked("ButtonLeft")){
                    Territory t = board.fromPixel(input.getMousePos().x, input.getMousePos().y).getOwner();
                    if(t != null && isOwner(t) && t.getStrength()>1){
                        board.selectBase(t);
                        return;
                    }
                }
                Thread.sleep(50);
            } catch(NullPointerException | InterruptedException ignore){}
        }
    }
    
    @Override
    public void selectTarget(GameBoard board, InputManager input) throws EndOfTurnException{
        while(true){
            try{
                if(input.isKeyTyped("Enter")){
                    throw new EndOfTurnException();
                }
                if(input.isClicked("ButtonLeft")){
                    Territory t = board.fromPixel(input.getMousePos().x, input.getMousePos().y).getOwner();
                    
                    if(board.getSelectedBase().equals(t)){
                        board.unSelectBase();
                        selectBase(board, input);
                    }else if(board.getSelectedBase().isNeighbor(t, board) && (t.getOwner().team != this.team)){
                        board.selectTarget(t);
                        return;
                    }
                }
                Thread.sleep(50);
            } catch(NullPointerException | InterruptedException ignore){}
        }
    }
}
