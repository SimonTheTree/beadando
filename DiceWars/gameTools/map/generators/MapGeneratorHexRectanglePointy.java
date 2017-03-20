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
 * @param <T>
 */
public class MapGeneratorHexRectanglePointy<T extends TileHex> extends MapGenerator<T>{

    public MapGeneratorHexRectanglePointy(String s, T t, int... p) {
        super(s,t,p);
    }

    @Override
    public ArrayList<T> generate() {
        if (p.length != 2) throw new InvalidParameterException("bad Number of arguments!");
        int x = p[0];
        int y = p[1];
        ArrayList<T> a = new ArrayList<>();
        for (int r = 0; r < y; r++) {
            int r_offset = r>>1; // or r>>1
            for (int q = -r_offset; q < x - r_offset; q++) {
                a.add((T) t.newTile(q, r, -q-r));
            }
        }
        return a;
    }
    
}
