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
public class MapGeneratorHexHexagonPointy<T extends TileHex> extends MapGenerator<T>{

    public MapGeneratorHexHexagonPointy(String s, T t, int... p) {
        super(s,t,p);
    }

    @Override
    public ArrayList<T> generate() {
        if (p.length < 1) throw new InvalidParameterException("bad Number of arguments!");
        int map_radius = p[0];
        ArrayList<T> a = new ArrayList<>();
        for (int q = -map_radius; q <= map_radius; q++) {
            int r1 = (-map_radius >= -q - map_radius)? -map_radius : -q - map_radius;
            int r2 = (map_radius <= -q + map_radius)? map_radius : -q + map_radius;
            for (int r = r1; r <= r2; r++) {

                a.add((T) t.newTile(q, r, -q-r));
            }
        }
        return a;
    }
    
}
