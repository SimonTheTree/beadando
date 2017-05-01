/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.players.Player;
import gameTools.Graphical;
import gameTools.map.Layout;
import gameTools.map.Map;
import gameTools.map.generators.MapGenerator;
import model.Question;
import model.RaceQuestion;

import java.awt.Graphics2D;
import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *  ez az osztal a jatek jatektablaja. ezen kell kijelolni az orszagokat, es ez
 * az osztaly (peldanyositott obj.) felelos a lepesek kiertekeleseert.<br>
 * Ő a gameClient, es kommunikal a szerverrel
 * @author ganter
 */
public class GameBoard extends Map<Cell> implements Graphical{
    
    //tool variables   
//    public static final double GYOK3 = Math.sqrt(3);
	private static enum QuestionType {
		RACE,
		NORMAL
	}
    
    public Territory[] territories;
    public boolean needsRender = false;
    //play-variables
    private Territory mouseOver = Territory.NULL_TERRITORY;
    
    private class Move implements Serializable{
        public QuestionType type;
    	public Player attPlayer;
        public Player defPlayer;
        public Territory selectedTarget = Territory.NULL_TERRITORY;
        public Question question;
        public RaceQuestion rQuestion;
        public String answerString;
        public double answerValue;     
    }
    
    private final Move[] move;
    
    public void setHighlitCell(Cell c){
        synchronized (mouseOver) {
        	if(c != null)
        		mouseOver = c.getOwner();
        	else
        		mouseOver=Territory.NULL_TERRITORY;			
		}
    }
    
    public Territory getHighlitTerritory(){
    	return mouseOver;
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
        
        final int territoryNum = TPP * GameSettings.getInstance().PLAYERS.size();
        
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
//            territories[i] = GameSettings.getInstance().usedTerritory.getInstance();
            territories[i] = new Territory();
            territories[i].add(f);
        }
        
        for(int i = 0; i< territories.length; i++){
            territories[i].setOwner(GameSettings.getInstance().PLAYERS.get(i%GameSettings.getInstance().PLAYERS.size()));
        }
        
        
        //distribute Dices
//        for(Player p : GameSettings.getInstance().PLAYERS){
//            int num = 3*TPP;
//            for(Territory t : p.getTerritories()){
////                num -= t.addDices(1);
//            }
//
//            while(num>0){
//                int rand = GameSettings.getInstance().RANDOM.nextInt(p.getTerritories().size());
//                num -= p.getTerritories().get(rand).addDices(1);
//            }
//        }
        
        //expand territories
        boolean emptyNeighborFound;
        do{
            emptyNeighborFound = false;
            List<Cell> unownedNeighborsOfCell; // unowned neighboring cells of random cell in territory
            
            for(Territory territory : territories){ //at the end of this loop every territory(that can) gets new member(s)
                List<Cell> unownedNeighborsOfTerritory = new ArrayList<>(); //unowned neighboring cells of the territory
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
                        Cell c = unownedNeighborsOfTerritory.remove(GameSettings.getInstance().RANDOM.nextInt(unownedNeighborsOfTerritory.size()));
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
    
    /**
     * Kap egy {@link GameBoard}-t, adatait atveszi, a sajatjait ezzel felulirva.
     * @param map
     */
    public void mimic(GameBoard other){}

    /**
     * vasolatot keszit magarol(shallow), amit majd vki arra hasznal hogy atvegye a 
     * tulajdonsagait a {@link #mimic(GameBoard)}-el.... nem biztos h erre szukseg lesz
     */
    public GameBoard clone(){return this;}

    public Territory getSelectedTarget(){
        return move[0].selectedTarget;
    }
    
    /**
     * Selects the terrytory with specified id on the {@link GameBoard}
     * @param id
     * @return the {@link Territory} with the specified id or {@link Territory#NULL_TERRITORY} if none found
     */
    public Territory getTerrytoryById(int id){
    	for(Territory t : territories){
    		if(t.id == id){
    			return t;
    		}
    	}
    	return Territory.NULL_TERRITORY;
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
        unSelectTarget();
        //swich moves
        move[0] = new Move();
        needsRender = true;
    }
    
    public void finishRound(Player p){
        int num = (int) Math.round(p.getTerritoryNum()/2.0);
        
        unSelectTarget();
        
        for(Player pp : GameSettings.getInstance().PLAYERS){
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
//            move[0].selectedBase.highlight();
//            move[0].selectedBase.render(g, layout);
//            move[0].selectedBase.unLight();
        }catch(NullPointerException ignore){}
        
    	synchronized (mouseOver) {
    		mouseOver.highlight();
    		mouseOver.render(g, layout);
    		mouseOver.unLight();				
		}
        
        if(needsRender && move[1] != null){
            int x0 = 0;
            int y0 = GameSettings.getInstance().GAME_HEIGHT;
            int cellHeight = (int) ((GameSettings.getInstance().SCREEN_HEIGHT-GameSettings.getInstance().GAME_HEIGHT) / 2.0);
            int cellWidth =  cellHeight;
            int padding = 5;
            
            int attCol = move[1].attPlayer.getColorID()+1;
            int defCol = move[1].defPlayer.getColorID()+1;
//            System.out.println(attc);
//            System.out.println(move[1].selectedBase.id);
            int y = y0;
            int x = x0;

            //render attacker symbol and dices
//            g.drawImage(GameSettings.getInstance().ATTACK_ICON[attCol], x+padding, y+padding, cellWidth-2*padding, cellHeight-2*padding, null);

//            for(int i = 0; i<move[1].baseRoll.length; i++){
//                x += cellWidth;
//                g.drawImage(GameSettings.getInstance().DICES[move[1].baseRoll[i]-1][attCol], x+padding, y+padding, cellWidth-2*padding, cellHeight-2*padding, null);
//            }

            //render defender symbol and dices

            x = x0;
            y = y0 + cellHeight;

//            g.drawImage(GameSettings.getInstance().SHIELD_ICON[defCol], x+padding, y+padding, cellWidth-2*padding, cellHeight-2*padding, null);

//            for(int i = 0; i<move[1].targetRoll.length; i++){
//                x += cellWidth;
//                g.drawImage(GameSettings.getInstance().DICES[move[1].targetRoll[i]-1][defCol], x+padding, y+padding, cellWidth-2*padding, cellHeight-2*padding, null);
//            }
            
            needsRender = false;            
        }
    }
}
