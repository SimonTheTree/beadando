/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameTools.map.generators;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import gameTools.map.TileHex;

/**
 *
 * @author ganter
 */
public class MapGeneratorHexParalelogram<T extends TileHex> extends MapGenerator<T>{

    public MapGeneratorHexParalelogram(String s, T t, int... p) {
        super(s,t,p);
    }

    @Override
    public ArrayList<T> generate() {
        if (p.length != 2) throw new InvalidParameterException("bad Number of arguments!");
        int x = p[0];
        int y = p[1];
        ArrayList<T> a = new ArrayList<>();
        for (int j = 0; j < x; j++) {
            for (int i = 0; i <= y; i++) {
//                int iCorr = i-y/2;
//                int jCorr = j-x/2;
//                a.add((T) t.newTile(jCorr, iCorr));
                a.add((T) t.newTile(j, i));
            }
        }
        return a;
    }
    
}