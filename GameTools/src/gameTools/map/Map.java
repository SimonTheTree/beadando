package gameTools.map;

import gameTools.map.generators.MapGenerator;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *  grid of cells
 * @author ganter
 * @param <T>
 */
public class Map<T extends Tile>
        extends HashMap<List<Integer>, T>
        implements gameTools.Graphical{
    
    public Layout layout;
    protected T tile;
    
    public Map(MapGenerator<T> g, Layout l){
        super();
        List<T> tiles = g.generate();
        int i;
        for(i = 0; i<tiles.size(); i++){
            addTile(tiles.get(i));
        }
        tile = tiles.get(i-1);
        layout = l;
    }
    
    public Map(Layout l, T tile){
        super();
        /*List<T> tiles = new ArrayList<>();
        int i;
        for(i = 0; i<tiles.size(); i++){
            addTile(tiles.get(i));
        }
        tile = tiles.get(i-1);*/
        layout = l;
    }
    
    public final int addTile(T c){
        List<Integer> a = new ArrayList<>();
        a.add(c.x);
        a.add(c.y);
        this.put(a, c);
        return 0;
    }
    
    public final T getTile(int... i){
        if (i.length != 2) return null; //throw new NullPointerException("The map does not contain the requested Tile");
        List<Integer> a = new ArrayList<>();
        a.add(i[0]);
        a.add(i[1]);
        return this.get(a);
    }
    
    public Dimension getDimensionInTiles(){
        int xMin=Integer.MAX_VALUE, xMax=Integer.MIN_VALUE, yMin=Integer.MAX_VALUE, yMax=Integer.MIN_VALUE;
        
        for(T tt : values()){
            Point t = tt.toPixel(layout).toPoint();
            if(xMin > t.x) xMin = t.x;
            if(xMax < t.x) xMax = t.x;
            if(yMin > t.y) yMin = t.y;
            if(yMax < t.y) yMax = t.y;
//            System.out.printf("%s,\txmin=%d, ymin=%d, xmax=%d, ymax=%d%n",t, xMin, yMin, xMax, yMax);
        }
//        Tile p = tile.newTile(tile.fromPixel((xMax-xMin+1),(int)(0.5*(xMax-xMin+1)+(yMax-yMin+1)),layout));
        int x = (xMax-xMin);
        int y = (yMax-yMin);
        int[] p = tile.fromPixel(x, y, layout);
//        return new Dimension(x, Math.abs(2*y+x)/2);
        return new Dimension(p[0],Math.abs(2*p[1]+p[0])/2);
    }
    
    public Dimension getDimensions(){
        int xMin=Integer.MAX_VALUE, xMax=Integer.MIN_VALUE, yMin=Integer.MAX_VALUE, yMax=Integer.MIN_VALUE;
        for(T t : values()){
            Point p = t.toPixel(layout).toPoint();
            if(xMin > p.x) xMin = p.x;
            if(xMax < p.x) xMax = p.x;
            if(yMin > p.y) yMin = p.y;
            if(yMax < p.y) yMax = p.y;
        }
        
        return new Dimension(xMax-xMin+1, yMax-yMin+1);
    }
    
    public Point getCenteredLayoutCenter(){
        Dimension area = getDimensions();
        Dimension offset = getZeroPointOffset();
        return new Point(area.width-offset.width,area.height-offset.height);
    }
    
    public Dimension getZeroPointOffset(){
        int xOffset=0, yOffset=0;
        Point p0 = getTile(0,0).toPixel(layout).toPoint();
        for(T t : values()){
            Point p = t.toPixel(layout).toPoint();
            if(p.x < p0.x && (p0.x-p.x) > xOffset) xOffset = p0.x-p.x;
            if(p.y < p0.y && (p0.y-p.y) > yOffset) yOffset = p0.y-p.y;
        }
//        xOffset = layout.size.x;
//        yOffset = layout.size.y;
        
        return new Dimension(xOffset, yOffset);
    }
    
    public final List<T> getNeighborTiles(int... i){
        List<int[]> coordinates;
        try{
            coordinates = getTile(i).getNeighbors();
        }catch(NullPointerException e){
            System.err.println(e.getMessage());
            return new ArrayList<>();
        }
        
        List<T> neighbors = new ArrayList();
        for(int[] coordinate : coordinates){
            T t = getTile(coordinate);
            if(t != null) neighbors.add(t);
        }
        return neighbors;
    }
    
    public final List<T> getSpecNeighborTiles(Tester<T> cc, int... i){
        List<T> validNeighbors = getNeighborTiles(i);
        for (Iterator<T> iterator = validNeighbors.iterator(); iterator.hasNext();) {
            T t = iterator.next();
            if(!cc.test(t)) iterator.remove();
        }
        return validNeighbors;
    }
    
    public boolean tileExists(int... i){
        return (getTile(i) != null);
        
    }
    
    public List<T> getTileArray(){
        return new ArrayList<>(this.values());
    }
    
    public List<T> getSpecTiles(Tester<T> cc){
        List<T> tiles = getTileArray();
        for (Iterator<T> iterator = tiles.iterator(); iterator.hasNext();) {
            T t = iterator.next();
            if(!cc.test(t)) iterator.remove();
        }
        return tiles;
    }
    
    public T fromPixel(int x, int y){
        return getTile(tile.fromPixel(x, y, layout));
    }
    
    @Override
    public void render(Graphics2D g) {
        for(T i : values()){
            i.render(g, layout);
        }
    }
}
