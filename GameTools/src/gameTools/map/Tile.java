package gameTools.map;


import gameTools.PointHD;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *  abstract Tile osztály. Ezen osztály leszármazottaiból készíthető térkép
 * a játékhoz 
 * @author ganter
 * @param <T>
 */
public abstract class Tile<T extends Tile>{
    public final int x,y;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public int hashCode(){
        int A = (x >= 0) ? 2 * x : -2 * x - 1;
        int B = (y >= 0) ? 2 * y : -2 * y - 1;
        return (A >= B) ? A * A + A + B : B * B + A;
    }
    
    public abstract T newTile(int... i);
    
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Tile){
            Tile t = (Tile) o;
            return (x==t.x && y==t.y);
        } else {
            return false;
        }
    }
    
    /**
     *
     * @param i
     * @param h
     * @return
     */
    public abstract int[] add(int... i);

    /**
     *
     * @param i
     * @param h
     * @return
     */
    public abstract int[] subtract(int... i);

    /**
     *
     * @param i
     * @param h
     * @return
     */
    public abstract int[] multiply(int... i);

    /**
     *
     * @param dir
     * @return
     */
    public abstract int[] getNeighbor(int dir);

    /**
     *
     * @return
     */
    public abstract ArrayList<int[]> getNeighbors();

    /**
     * Gets the center Point of the cell on screen
     * @param layout
     * @param h
     * @return 
     */
    public abstract PointHD toPixel(Layout layout);
    
    public abstract int[] fromPixel(int x, int y, Layout layout);
    
    /**
     * Gets the Polygon to draw on screen
     * @param layout
     * @param h
     * @return 
     */
    public abstract Polygon polygonCorners(Layout layout);

    public abstract void render(Graphics2D g, Layout l);

}
