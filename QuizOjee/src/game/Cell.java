package game;

import gameTools.map.TileHex;
import gameTools.PointHD;
import gameTools.LineHD;
import gameTools.map.Layout;
import gameTools.map.Tester;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.border.StrokeBorder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *  Hexagon cella melyet ki lehet rajzolni, es "tudja" melyik orszaghoz tartozik
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

    public List<LineHD> getBorderLines(Layout layout){
        List<LineHD> lines = new ArrayList<>();
        PointHD center = toPixel(layout);
        PointHD[] p = new PointHD[2];
        p[0] = center.add(hexCornerOffset(layout, 0));
        for (int i = 1; i < 6; i++){
            p[1] = center.add(hexCornerOffset(layout, i));
            
            lines.add(new LineHD(p[0], p[1]));
            p[0] = p[1];
        }
        lines.add(new LineHD(p[0], center.add(hexCornerOffset(layout, 0))));
        return lines;
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
        }
        if (highlighted){
        	g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(255, 255, 255, 50));
        } else {        	
        	g.setColor(getOwner().getOwner().getColor());
        	g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        	g.draw(polygonCorners(layout));
        }
        
        g.fillPolygon(polygonCorners(layout));
        
        
//        for(LineHD l : getBorderLines(layout)){
//            g.drawLine(l.A.getIntx(), l.A.getInty(), l.B.getIntx(), l.B.getInty());
//        }
            
        
        //draw info - Debug
//        if(GameSettings.getInstance().dbg){
//            g.setColor(Color.WHITE);
//            g.setFont(new Font("Courier New", Font.PLAIN, (int) (GameSettings.getInstance().getCellHeight())));
//            Point p = toPixel(layout).toPoint();
//            double angle = 2.0 * Math.PI * (3 + layout.orientation.START_ANGLE) / 6.0;
//            int X = (int) (layout.size.x * Math.cos(angle)) + p.x +10;
//            int Y = p.y+4;
//            String s = toString();
//    //        if(owner != null){ //territory id
//    //            s = String.format("%d", owner.id); 
//    //        }
//            g.drawString(s, X, Y);
//        }
    }
    
}
