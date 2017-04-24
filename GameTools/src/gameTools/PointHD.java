/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameTools;

import java.awt.Point;

/**
 *  egyszerű double pont osztály a pontosabb aritmetika megvalósítása gyanánt. általános függvénxeket tartalmaz
 * @author ganter
 */
public class PointHD{
        public double x,y;
        
        public PointHD(double x, double y){
            this.x = x;
            this.y = y;
        }
        
        public int getIntx(){
            return (int) Math.round(x);
        }
        public int getInty(){
            return (int) Math.round(y);
        }
        
        public PointHD add(PointHD b){
            return new PointHD(x + b.x, y + b.y);
        }
        public PointHD subtract(PointHD b){
            return new PointHD(x - b.x, y - b.y);
        }
        public PointHD multiply(PointHD b){
            return new PointHD(x - b.x, y - b.y);
        }
        
        public Point toPoint(){
            return new Point(getIntx(),getInty());
        }
        public boolean equals(PointHD p){
            return (Double.compare(p.x, x) == 0 && Double.compare(p.y, y) == 0);
        }
        
        public boolean isInPolygon(PointHD... p){
            double ret=0;
            int i;
            for(i = 0; i< p.length-1; i++){
                ret += getArea(this, p[i], p[i+1]);
            }
            ret += getArea(this, p[i], p[0]);
            return Double.compare(getArea(p) , ret) == 0;
        };
        
        public static double getArea(PointHD... points){
            double ret=0;
            int i;
            for(i = 0; i< points.length-1; i++){
                ret += points[i].x*points[i+1].y - points[i].y * points[i+1].x;
            }
            ret += points[i].x*points[0].y - points[i].y * points[0].x;
            return Math.abs(ret/2.0);
        }
        
        public double distance(PointHD b){
            return Math.sqrt( Math.abs(this.x-b.x)*Math.abs(this.x-b.x) + Math.abs(this.y-b.y)*Math.abs(this.y-b.y) );
        }
    }
