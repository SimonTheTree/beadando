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
public class MapGeneratorHexRectangleFlat<T extends TileHex> extends MapGenerator<T>{

    public MapGeneratorHexRectangleFlat(String s, T t, int... p) {
        super(s,t,p);
    }

    @Override
    public ArrayList<T> generate() {
        if (p.length != 2) throw new InvalidParameterException("bad Number of arguments!");
        int x = p[0];
        int y = p[1];
        ArrayList<T> a = new ArrayList<>();
        for (int q = 0; q < x; q++) {
            int q_offset = q>>1;
            for (int r = -q_offset; r < y - q_offset; r++) {
//                int xCorr = q + (y/4);
//                int zCorr = r + (y/4);
//                a.add((T) t.newTile(xCorr, zCorr, -xCorr-zCorr));
                a.add((T) t.newTile(q, r, -q-r));
            }
        }
        return a;
    }
    
}