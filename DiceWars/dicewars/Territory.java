/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dicewars;

import dicewars.players.Player;
import gameTools.map.Layout;
import gameTools.map.Map;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

/**
 *  Egy ország a térképen, cellák összessége
 * @author ganter
 */
public class Territory {
        private static int numOfTerritories=0;
        private static final int maxStrength = 8;
        public final int id=++numOfTerritories;
        private Player owner;
        public int strength;
        public final ArrayList<Cell> cells; //inside
        private boolean highlighted;
        private boolean updated = true;

        public Territory() {
            strength = 0;
            cells = new ArrayList<>();
        }
        
        void add(Cell c){
            cells.add(c);
            c.setOwner(this);
        }
        
        public void setOwner(Player p) {
            if (owner != null) owner.removeTerritory(this);
            this.owner = p;
            p.addTerritory(this);
        }
        public Player getOwner() {
            return this.owner;
        }
        
        public ArrayList<Territory> getNeighborTerritories(Map map){
            ArrayList<Cell> neighborsOfCell;
            ArrayList<Territory> neighborsOfTerritory = new ArrayList<>(); //unowned neighboring cells of the territory
            
            //collect the territories
            for(Cell cell: cells){
                neighborsOfCell = map.getNeighborTiles(cell.x, cell.y);
                for(Cell c : neighborsOfCell){
                    if( (!neighborsOfTerritory.contains(c.getOwner())) && (!this.equals(c.getOwner())) ) {
                        neighborsOfTerritory.add(c.getOwner());
                    }
                }
            }

            return neighborsOfTerritory;
            
        }
        
        public ArrayList<Cell> getCells(){
            return cells;
        }
        
        public boolean isNeighbor(Territory t, Map map){
            ArrayList<Cell> unownedNeighborsOfCell;
            
            //check each neighbor cell (of any cell in this) if its owner is t
            for(Cell cell: cells){
                unownedNeighborsOfCell = map.getNeighborTiles(cell.x, cell.y);
                for(Cell c : unownedNeighborsOfCell){
                    if( t.equals(c.getOwner()))  {
                        return true;
                    }
                }
            }
            
            return false;
        }
        
        /**
         * adds the amount of strength(dices). The strength cannot get higher than the maximum(def.: 8).
         * @param i the amount of strength to add.
         * @return the amount of strength used up of i.
         */
        public int addDices(int i){
            strength+=i;
            if(strength > 8){
                int ret = 8-(strength-i);
                strength = 8;
                return ret;
            } else {
                return i;
            }
        }
        
        public void touch(){
            this.updated = true;
            for(Cell c : cells){
                c.touch();
            }
        }
        
        public Point getCenter(){
            double sumX=0, sumY=0;
            double cellN = cells.size();
            for(Cell c: cells){
                sumX += c.x / cellN;
                sumY += c.y / cellN; 
            }
            return new Point((int)sumX,(int)sumY);
        }
        
        public int getStrength(){
            return this.strength;
        }
        public void setStrength(int i){
            if(i<=8){
                this.strength = i;
            }else{
                this.strength = 8;
            }
        }

        public void highlight() {
            this.highlighted = true;
            for(Cell c : cells){
                c.highlight();
            }
            touch();
        }
        public void unLight() {
            this.highlighted = false;
            for(Cell c : cells){
                c.unLight();
            }
            touch();
        }
        public boolean isHighlighted() {
            return highlighted;
        }
        
        public void render(Graphics2D g, Layout layout){
            if(!updated){
                return;
            } else {
                updated=false;
            }

            if(owner != null ){
                g.setColor(owner.getColor());
            } else {
                g.setColor(Color.GRAY);
            }
            for(Cell c : cells){
                c.render(g, layout);
            }

            
            //draw strength
            g.setColor(Color.WHITE);
            Cell c = new Cell(getCenter().x, getCenter().y);
            Point p = c.toPixel(layout).toPoint();
            int X = p.x;
            int Y = p.y+15;
            String s = String.format("%d", strength);
            g.setFont(new Font("Courier New", Font.PLAIN, 20));
            g.drawString(s, X, Y);
        }
        
        @Override
        public boolean equals(Object o){
            if (o != null && o instanceof Territory){
                Territory t = (Territory) o;
                return (id == t.id);
            } else {
                return false;
            }
        }
        
    }
