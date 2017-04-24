/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameTools.map.generators;

import gameTools.map.TileHex;
import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 *
 * @author ganter
 */
public class MapGeneratorHexLineFlat<T extends TileHex> extends MapGenerator<T>{

    public MapGeneratorHexLineFlat(String s, T t, int... p) {
        super(s,t,p);
    }

    @Override
    public ArrayList<T> generate() {
        if (p.length < 1) throw new InvalidParameterException("bad Number of arguments!");
        int x = p[0];
        ArrayList<T> a = new ArrayList<>();
        for (int j = 0; j < x; j++) {
            a.add((T) t.newTile(0, j));
        }
        return a;
    }
    
}