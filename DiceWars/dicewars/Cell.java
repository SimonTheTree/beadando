package dicewars;

import gameTools.map.TileHex;
import gameTools.map.Layout;
import gameTools.map.Tester;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *  Hexagon cella melyet ki lehet rajzolni, es "tudja" melyik orszaghoz tartozik
 * @author ganter
 */
public class Cell extends TileHex{
    
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

    public Cell( int q, int r, int s) {
        super(q, r, s);
        highlighted = false;
    }
    
    public Cell( int q, int r) {
        super(q, r, -q-r);
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
        if(!updated && !Settings.dbg){
            return;
        } else {
            updated=false;
        }
        
        if(Settings.dbg){
            g.setColor(getOwner().getOwner().getColor());
        }
        if (highlighted){
            g.setColor(new Color(255, 255, 255, 200));
        }
        g.fill(polygonCorners(layout));
        
            
        
        //draw info - Debug
        if(Settings.dbg){
            g.setColor(Color.WHITE);
            g.setFont(new Font("Courier New", Font.PLAIN, (int) (Settings.getCellHeight())));
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
