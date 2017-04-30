package gameTools.map;

import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *  egy hexagon cella kétféleképpen állhat csúccsal fölfelé, vagy oldallal felfelé.
 * Ezen osztály statikus paraméterei ezt tartalmazzák
 * @author ganter
 */
public class Orientation extends Object implements Serializable{
    private static final double GYOK3 = Math.sqrt(3);
    public static final Orientation LAYOUT_POINTY = new Orientation(GYOK3, GYOK3/2.0, 0, 1.5, GYOK3/3.0, -1.0/3.0, 0, 2/3.0, 0.5);
    public static final Orientation LAYOUT_FLAT = new Orientation(1.5, 0, GYOK3/2.0, GYOK3, 2/3.0, 0, -1/3.0, GYOK3/3.0, 0);
    
    public final double F0, F1, F2, F3;
    public final double B0, B1, B2, B3;
    public final double START_ANGLE;

//    /**
//     * constructor for serialization, dont use it. 
//     */
//    public Orientation(){
//    	this(GYOK3, GYOK3/2.0, 0, 1.5, GYOK3/3.0, -1.0/3.0, 0, 2/3.0, 0.5);
//    };
    
    private Orientation(double F0, double F1, double F2, double F3, double B0, double B1, double B2, double B3, double START_ANGLE) {
        this.F0 = F0;
        this.F1 = F1;
        this.F2 = F2;
        this.F3 = F3;
        this.B0 = B0;
        this.B1 = B1;
        this.B2 = B2;
        this.B3 = B3;
        this.START_ANGLE = START_ANGLE;
    }
}
