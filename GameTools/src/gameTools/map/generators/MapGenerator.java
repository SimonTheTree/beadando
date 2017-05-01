/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameTools.map.generators;

import java.io.Serializable;
import java.util.ArrayList;
import gameTools.map.Tile;

/**
 *  mapgenerator interfesz: ide van belekodolva ( az alosztalyokban) hogy milxen fuggveny alapjan hozza letre a terkepet
 * @author ganter
 * @param <T>
 */
public abstract class MapGenerator<T extends Tile> implements Serializable{

    public final String name;
    protected T t;
    protected int[] p;

    public void setTile(T t) {
        this.t = t;
    }

    public void setParameters(int... p) {
        this.p = p;
    }
    
    public MapGenerator(String s, T t, int... p){
        this.name = s;
        this.t = t;
        this.p = p; 
    }
    
    public abstract ArrayList<T> generate();
    
}
