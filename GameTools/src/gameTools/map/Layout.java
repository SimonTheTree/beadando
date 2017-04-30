package gameTools.map;


import java.awt.Point;
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *  Ez az osztály meghatározza hogy az egyes cellákat egy map hogyan rajzolja ki
 * @author ganter
 */
public class Layout implements Serializable{
    public Orientation orientation;
    public Point size;
    public Point origin;

    public Layout(Orientation orientation, Point size, Point origin) {
        this.orientation = orientation;
        this.size = size;
        this.origin = origin;
    }
    

}
