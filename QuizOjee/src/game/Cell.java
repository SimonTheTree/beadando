package game;

import gameTools.map.TileHex;
import gameTools.map.Layout;
import gameTools.map.Tester;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *  Hexagon cella melyet ki lehet rajzolni, és "tudja" melyik országhoz tartozik
 * @author ganter
 */
public class Cell extends TileHex<Cell> {
    
    public static final Tester<Cell> CELL_SELECTED = new Tester<Cell>(){
        @Override
        public boolean test(Cell c) {
            return c.highlighted;
        }
    };
    public static final Tester<Cell> CELL_NOT_SELECTED = new Tester<Cell>(){@Override
        public boolean test(Cell c) {
            return !c.highlighted;
        }
    };
    public static final Tester<Cell> CELL_NOT_OWNED = new Tester<Cell>(){
        @Override
        public boolean test(Cell c) {
            return (c.owner == null);
        }
    };
    
    private Territory owner;
    private boolean highlighted;
    private boolean updated = true;
    private final int id; 
    private static int CURRENT_ID = 0;
    
    public Cell( int q, int r) {
    	this(q, r, -q-r);
    }
    
    public Cell( int q, int r, int s) {
        super(q, r, s);
        highlighted = false;
        id = CURRENT_ID++;
    }
    
    public void touch(){
        this.updated = true;
    }

    public void highlight() {
        this.highlighted = true;
        touch();
    }
    public void unLight() {
        this.highlighted = false;
        touch();
    }
    public boolean isHighlighted() {
        return highlighted;
    }

    public Territory getOwner() {
        return owner;
    }
    public void setOwner(Territory owner) {
        this.owner = owner;
        touch();
    }

    
    @Override
    public Cell newTile(int... i) {
        return new Cell(i[0], i[1]);
    }
    
    @Override
    public void render(Graphics2D g, Layout layout) {
        if(!updated && !GameSettings.getInstance().dbg){
            return;
        } else {
            updated=false;
        }
        
        if(GameSettings.getInstance().dbg){
            g.setColor(getOwner().getOwner().getColor());
        }
        if (highlighted){
            g.setColor(new Color(255, 255, 255, 200));
        }
        System.out.println("cell rendering");
        g.fill(polygonCorners(layout));
        
            
        
        //draw info - Debug
        if(GameSettings.getInstance().dbg){
            g.setColor(Color.WHITE);
            g.setFont(new Font("Courier New", Font.PLAIN, (int) (GameSettings.getInstance().getCellHeight())));
            Point p = toPixel(layout).toPoint();
            double angle = 2.0 * Math.PI * (3 + layout.orientation.START_ANGLE) / 6.0;
            int X = (int) (layout.size.x * Math.cos(angle)) + p.x +10;
            int Y = p.y+4;
            String s = toString();
    //        if(owner != null){ //territory id
    //            s = String.format("%d", owner.id); 
    //        }
            g.drawString(s, X, Y);
        }
    }
    
}
