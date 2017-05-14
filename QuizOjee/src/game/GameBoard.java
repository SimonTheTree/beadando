/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.players.Player;
import game.players.PlayerAI;
import game.players.PlayerHuman;
import gameTools.Graphical;
import gameTools.map.Layout;
import gameTools.map.Map;
import gameTools.map.Tile;
import gameTools.map.generators.MapGenerator;
import model.Question;
import model.RaceQuestion;
import model.User;
import view.Settings;

import java.awt.Graphics2D;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *  ez az osztal a jatek jatektablaja. ezen kell kijelolni az orszagokat, es ez
 * az osztaly (peldanyositott obj.) felelos a lepesek kiertekeleseert.<br>
 *  a gameClient, es kommunikal a szerverrel
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
    private String name = "";
    
    public synchronized void setHighlitCell(Cell c){
        synchronized (mouseOver) {
        	if(c != null)
        		mouseOver = c.getOwner();
        	else
        		mouseOver=Territory.NULL_TERRITORY;			
		}
    }
    
    public synchronized Territory getHighlitTerritory(){
    	return mouseOver;
    }
    
    public synchronized void unLight() {
    	mouseOver=Territory.NULL_TERRITORY;
    }
    
    public GameBoard(MapGenerator<Cell> g, Layout layout){
        super(g, layout);
    }
    
    public GameBoard(Document xml,Layout layout) {
    	this(xml,layout,new Cell(0,0));
    }
    
    public GameBoard(Document xml,Layout layout,Cell t) {
        super(layout,t);
        initFromXML(xml);
    }
    
    /**
     * 
     * @param territoryNum ammount of territories per player (Territories Per Player = tpp)
     */
    public void generateTerritories(final int TPP){
        System.out.println("generating gameboard... ");
        
        final int territoryNum = TPP * GameSettings.getInstance().players.size();
        
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
            territories[i].setOwner(GameSettings.getInstance().players.get(i%GameSettings.getInstance().players.size()));
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
        toXMLString();
    }

    
    /**
     * Selects the territory with specified id on the {@link GameBoard}
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
            System.out.printf("Player%d selecting TARGET territory%d%n",t.getOwner().getId(), t.id);
        }
    }

    public void finishRound(Player p){
        int num = (int) Math.round(p.getTerritoryNum()/2.0);
                
        for(Player pp : GameSettings.getInstance().players){
            if(pp.getTerritoryNum() == 0)
                pp.kill();
        }
    }
    
    @Override
    public synchronized void render(Graphics2D g) {
        
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
        
        if(needsRender){
            int x0 = 0;
            int y0 = GameSettings.getInstance().GAME_HEIGHT;
            int cellHeight = (int) ((GameSettings.getInstance().SCREEN_HEIGHT-GameSettings.getInstance().GAME_HEIGHT) / 2.0);
            int cellWidth =  cellHeight;
            int padding = 5;
            
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
    
    public String toXMLString() {
    	String re = "";
    	try {
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.newDocument();
	    	// root element
	    	Element rootElement = doc.createElement("Map");
	    	rootElement.setAttribute("name", name);
	    	doc.appendChild(rootElement);
	    	for(Player player : GameSettings.getInstance().players) {
	    		addPlayersXML(doc,rootElement,player);
	    	}
	    	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    	Transformer transformer = transformerFactory.newTransformer();
	    	DOMSource source = new DOMSource(doc);
	    	StreamResult consoleResult = new StreamResult(System.out);
	    	transformer.transform(source, consoleResult);
	    	StreamResult fileResult = new StreamResult(re);
	    	transformer.transform(source, fileResult);
    	} catch(ParserConfigurationException e) {
    		e.printStackTrace();
    	} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
    	
    	return re;
    }
        
    private void addPlayersXML(Document doc, Element parentElement, Player player) {
    	Element playerElement = doc.createElement("Player");
    	parentElement.appendChild(playerElement);
    	for(Territory territory : player.getTerritories()) {
    		addTerritoryXML(doc,playerElement,territory);
    	}
    }
    
    private void addTerritoryXML(Document doc, Element parentElement, Territory territory) {
    	Element territoryElement = doc.createElement("Territory");
    	parentElement.appendChild(territoryElement);
    	for(Cell cell : territory.cells) {
    		addCellXML(doc,territoryElement,cell);
    	}
    }
    
    private void addCellXML(Document doc, Element parentElement, Cell cell) {
    	Element cellElement = doc.createElement("Cell");
    	parentElement.appendChild(cellElement);
    	Attr attr = doc.createAttribute("x");
    	attr.setValue(cell.x+"");
    	cellElement.setAttributeNode(attr);
    	Attr attr2 = doc.createAttribute("y");
    	attr2.setValue(cell.y+"");
    	cellElement.setAttributeNode(attr2);
    }

 
    /*
    Cell, cell.owner (Territory)
    Territory (Gameboard.territories tomb)
    Territory.cells tomb <-- cellák referenciái
    Territory.owner (Player)
    GameSettings.players
    Settings.game_numOfPlayers
    
    Cell ---> Territory ---> Player
         <--- array[i]  <--- array[i]             
     * */
    
    private void initFromXML(Document doc) {
    	doc.getDocumentElement().normalize();
        System.out.print("Root element: ");
        Element rootElement = doc.getDocumentElement();
        name = rootElement.getAttribute("name");
        System.out.println(rootElement.getNodeName());
        NodeList nodeList = doc.getElementsByTagName("Player");
        System.out.println("----------------------------");
        initPlayersFromXML(nodeList);
    }
    
    private void initPlayersFromXML(NodeList nodeList) {
    	List<Player> re = new ArrayList<>();
    	Settings.game_numOfPlayers = re.size();
    	for(int i=0;i<nodeList.getLength();++i) {
    		Node playerNode = nodeList.item(i);
    		if(playerNode.getNodeType() != Node.ELEMENT_NODE) System.err.println("Hibas XML file.");
    		Element playerElement = (Element) playerNode;
    		NodeList territoriesNodeList = playerElement.getElementsByTagName("Territory");
    		User u = new User();
    		u.setUsername("nyomi");
    		Player player = new PlayerHuman(u,0);
    		initTerritoriesFromXML(territoriesNodeList,player);
    		re.add(player);
    	}
    	GameSettings.getInstance().players = re;
    }

    private void initTerritoriesFromXML(NodeList nodeList, Player player) {
    	territories = new Territory[nodeList.getLength()];
    	System.out.println("-----------------Territories:"+nodeList.getLength());
    	for(int i=0;i<nodeList.getLength();++i) {
    		Node territoryNode = nodeList.item(i);
    		if(territoryNode.getNodeType() != Node.ELEMENT_NODE) System.err.println("Hibas XML file.");
    		Element territoryElement = (Element) territoryNode;
    		NodeList cellsNodeList = territoryElement.getElementsByTagName("Cell");
    		Territory territory = new Territory();
    		initCellsFromXML(cellsNodeList,territory);
    		territory.setOwner(player);
    		territories[i] = territory;
    	}
    }

	private void initCellsFromXML(NodeList nodeList, Territory territory) {
    	for(int i=0;i<nodeList.getLength();++i) {
    		Node cellNode = nodeList.item(i);
    		if(cellNode.getNodeType() != Node.ELEMENT_NODE) System.err.println("Hibas XML file.");
    		Element cellElement = (Element) cellNode;
    		int x = Integer.parseInt(cellElement.getAttribute("x"));
    		int y = Integer.parseInt(cellElement.getAttribute("y"));
    		Cell cell =  new Cell(x,y);
    		territory.add(cell);
    	}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}

