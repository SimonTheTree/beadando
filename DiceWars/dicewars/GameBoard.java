/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dicewars;

import dicewars.players.Player;
import gameTools.Graphical;
import gameTools.map.Layout;
import gameTools.map.Map;
import gameTools.map.generators.MapGenerator;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 *  ez az osztál a játék játéktáblája. ezen kell kijelölni az országokat, és ez
 * az osztál ( példányosított obj.) felelős a lépések kiértékeléséért
 * @author ganter
 */
public class GameBoard extends Map<Cell> implements Graphical{
    
    //tool variables   
//    public static final double GYOK3 = Math.sqrt(3);
    
    public Territory[] territories;
    public boolean updated = false;
    //play-variables
    private Territory mouseOver;
    
    private class Move{
        public Player attPlayer;
        public Player defPlayer;
        public Territory selectedBase = null;
        public Territory selectedTarget = null;
        public int[] baseRoll = new int[0];
        public int[] targetRoll = new int[0];
        public int baseRollSum=0, targetRollSum=0;        
    }
    
    private final Move[] move;
    
    public void setHighlitCell(Cell c){
        if(c != null)
            mouseOver = c.getOwner();
        else
            mouseOver=null;
    }
    
    public void setCurrentPlayer(Player p){
        if(move[0] != null){
            move[0].attPlayer = p;
        }
    }
    
    public GameBoard(MapGenerator<Cell> g, Layout layout){
        super(g, layout);
        move = new Move[2];
        move[0] = new Move();
    }
    
    /**
     * 
     * @param territoryNum ammount of territories per player (Territories Per Player = tpp)
     */
    public void generateTerritories(final int TPP){
        System.out.println("generating gameboard... ");
        
        final int territoryNum = TPP * Settings.PLAYERS.size();
        
        if(this.values().size() < territoryNum) throw new AssertionError("There is not enough tiles on the map for the players!");
        
        territories = new Territory[territoryNum];
        Cell[] cells = new Cell[0];
        cells = (Cell[]) values().toArray(cells);
        
        //generate random territory bases/origins
        for(short i = 0; i < territoryNum; i++){
            boolean ok;     
            int index;
            Cell f;
            do{
                ok=true;
                index = (int) Math.floor(Math.random()*size());
                f = cells[index];
                for(int j = 0; j < i; j++){
                    if(f.equals(territories[j].getCells().get(0))) ok = false;
                }
            }while(!ok);
//            territories[i] = Settings.usedTerritory.getInstance();
            territories[i] = new Territory();
            territories[i].add(f);
        }
        
        for(int i = 0; i< territories.length; i++){
            territories[i].setOwner(Settings.PLAYERS.get(i%Settings.PLAYERS.size()));
        }
        
        
        //distribute Dices
        for(Player p : Settings.PLAYERS){
            int num = 3*TPP;
            for(Territory t : p.getTerritories()){
                num -= t.addDices(1);
            }

            while(num>0){
                int rand = Settings.RANDOM.nextInt(p.getTerritories().size());
                num -= p.getTerritories().get(rand).addDices(1);
            }
        }
        
        //expand territories
        boolean emptyNeighborFound;
        do{
            emptyNeighborFound = false;
            ArrayList<Cell> unownedNeighborsOfCell; // unowned neighboring cells of random cell in territory
            
            for(Territory territory : territories){ //at the end of this loop every territory(that can) gets new member(s)
                ArrayList<Cell> unownedNeighborsOfTerritory = new ArrayList<>(); //unowned neighboring cells of the territory
                //collect the cells that have empty neighbors
                for(Cell cell: territory.getCells()){
                    unownedNeighborsOfCell = getSpecNeighborTiles(Cell.CELL_NOT_OWNED, cell.x, cell.y);
                    if (! unownedNeighborsOfCell.isEmpty()){
                        emptyNeighborFound = true;
                        for(Cell c : unownedNeighborsOfCell){
                            if(!unownedNeighborsOfTerritory.contains(c)) {
                                unownedNeighborsOfTerritory.add(c);
                            }
                        }
                    }
                }

                for(int i = 0; i< 10; i++){
                    if(!unownedNeighborsOfTerritory.isEmpty()){
                        //get random cell from the collected ownerless cells
                        Cell c = unownedNeighborsOfTerritory.remove(Settings.RANDOM.nextInt(unownedNeighborsOfTerritory.size()));
                        if (c == null) break;
                        territory.add(c);
                    }
                }
                //go on to the next territory    
            }
        }while(emptyNeighborFound);
        
//        for(Territory t : territories){
//            t.calcBoundary(layout);
//        }
        System.out.println("done generating");
    }
    
    public Territory getSelectedBase(){
        return move[0].selectedBase;
    }
    
    public Territory getSelectedTarget(){
        return move[0].selectedTarget;
    }
    
    public void selectBase(Territory t){
        if(t != null){
            move[0].selectedBase = t;
            move[0].selectedBase.highlight();
            move[0].attPlayer = t.getOwner();
            System.out.printf("Player%d selecting BASE   territory%d%n",t.getOwner().getId(), t.id);
        }
    }
    public void unSelectBase(){
        if(move[0].selectedBase != null) move[0].selectedBase.unLight();
        move[0].selectedBase = null;
    }
    
    public void selectTarget(Territory t){
        if(t != null){
            move[0].selectedTarget = t;
            move[0].selectedTarget.highlight();
            move[0].defPlayer = t.getOwner();
            System.out.printf("Player%d selecting TARGET territory%d%n",t.getOwner().getId(), t.id);
        }
    }
    public void unSelectTarget(){
        if(move[0].selectedTarget != null) move[0].selectedTarget.unLight();
        move[0].selectedTarget = null;
    }
    
    public void evaluateMove(){
        move[0].baseRoll = Dice.roll(move[0].selectedBase.getStrength());
        move[0].targetRoll = Dice.roll(move[0].selectedTarget.getStrength());
        move[0].baseRollSum=0;
        move[0].targetRollSum=0;
        for(int i : move[0].baseRoll) move[0].baseRollSum+=i;
        for(int i : move[0].targetRoll) move[0].targetRollSum+=i;
        move[1] = move[0];
        
        if(move[0].baseRollSum > move[0].targetRollSum){ //win!
            move[0].selectedTarget.setStrength(move[0].selectedBase.getStrength()-1);
            move[0].selectedTarget.setOwner(move[0].selectedBase.getOwner());
        }
        move[0].selectedBase.setStrength(1);
        
        unSelectBase();
        unSelectTarget();
        //swich moves
        move[0] = new Move();
        updated = true;
    }
    
    public void finishRound(Player p){
        int num = (int) Math.round(p.getTerritoryNum()/2.0);
        
        int maxNum = 0;
        for(Territory t : p.getTerritories()){
            maxNum += 8-t.getStrength();
        }
        num = (num > maxNum)? maxNum : num;
        
        while(num>0){
            int rand = Settings.RANDOM.nextInt(p.getTerritories().size());
            num -= p.getTerritories().get(rand).addDices(1);
        }

        unSelectBase();
        unSelectTarget();
        
        for(Player pp : Settings.PLAYERS){
            if(pp.getTerritoryNum() == 0)
                pp.kill();
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        
        for(Territory t : territories){
                t.render(g, layout);
        }
        
        
        try{
            move[0].selectedBase.highlight();
            move[0].selectedBase.render(g, layout);
            move[0].selectedBase.unLight();
        }catch(NullPointerException ignore){}
        
        try{
            mouseOver.highlight();
            mouseOver.render(g, layout);
            mouseOver.unLight();
        }catch(NullPointerException ignore){};
        
        if(updated && move[1] != null){
            int x0 = 0;
            int y0 = Settings.GAME_HEIGHT;
            int cellHeight = (int) ((Settings.SCREEN_HEIGHT-Settings.GAME_HEIGHT) / 2.0);
            int cellWidth =  cellHeight;
            int padding = 5;
            
            int attCol = move[1].attPlayer.getColorID()+1;
            int defCol = move[1].defPlayer.getColorID()+1;
//            System.out.println(attc);
//            System.out.println(move[1].selectedBase.id);
            int y = y0;
            int x = x0;

            //render attacker symbol and dices
            g.drawImage(Settings.ATTACK_ICON[attCol], x+padding, y+padding, cellWidth-2*padding, cellHeight-2*padding, null);

            for(int i = 0; i<move[1].baseRoll.length; i++){
                x += cellWidth;
                g.drawImage(Settings.DICES[move[1].baseRoll[i]-1][attCol], x+padding, y+padding, cellWidth-2*padding, cellHeight-2*padding, null);
            }

            //render defender symbol and dices

            x = x0;
            y = y0 + cellHeight;

            g.drawImage(Settings.SHIELD_ICON[defCol], x+padding, y+padding, cellWidth-2*padding, cellHeight-2*padding, null);

            for(int i = 0; i<move[1].targetRoll.length; i++){
                x += cellWidth;
                g.drawImage(Settings.DICES[move[1].targetRoll[i]-1][defCol], x+padding, y+padding, cellWidth-2*padding, cellHeight-2*padding, null);
            }
            
            updated = false;            
        }
    }
}
