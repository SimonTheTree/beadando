package gameTools.map;


import gameTools.Graphical;
import gameTools.PointHD;
import gameTools.state.State;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.security.InvalidParameterException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ganter
 * @param <H>
 */
public abstract class TileHex<H extends TileHex> extends Tile<H>{
    public final int z;

    /**
     * Cubic coordinate getter
     * @param i Cell coordinates: array of three ints
     * @return corresponding cell, or null if error
     */
    static int[] axialToCubic(int... i){
        if(i.length != 2) throw new InvalidParameterException("bad Number of arguments!");
        return new int[]{i[0], i[1], -i[0] - i[1]};
    }
    
    /**
     * Cubic coordinate getter
     * @param i Cell coordinates: array of three ints
     * @return corresponding cell, or null if error
     */
    static int[] cubicToAxial(int... i){
        if(i.length != 3) throw new InvalidParameterException("bad Number of arguments!");
        return new int[]{i[0], i[1], -i[0] - i[1]};
    }

    /**
     * Offset coordinate getter
     * @param a Cell coordinates: array of two ints
     * @return corresponding cell, or null if error
     */
    static int[] AxialtoOffset(int... a){
        if(a.length != 2) throw new InvalidParameterException("bad Number of arguments!");
        //convert cube to axial
        //!!!!!!!!!!!!!!!!!!!!!!todo
        
        return new int[]{0,0};
    }
    /**
    OffsetCoord qoffset_from_cube(int offset, Hex h) {
        int col = h.q;
        int row = h.r + int((h.q + offset * (h.q & 1)) / 2);
        return OffsetCoord(col, row);
    }

    Hex qoffset_to_cube(int offset, OffsetCoord h) {
        int q = h.col;
        int r = h.row - int((h.col + offset * (h.col & 1)) / 2);
        int s = -q - r;
        return Hex(q, r, s);
    }

    OffsetCoord roffset_from_cube(int offset, Hex h) {
        int col = h.q + int((h.r + offset * (h.r & 1)) / 2);
        int row = h.r;
        return OffsetCoord(col, row);
    }

    Hex roffset_to_cube(int offset, OffsetCoord h) {
        int q = h.col - int((h.row + offset * (h.row & 1)) / 2);
        int r = h.row;
        int s = -q - r;
        return Hex(q, r, s);
    }
    */
    
    
    static int[][] HEX_DIRECTIONS = new int[][]{
        new int[]{1, 0, -1},
        new int[]{1, -1, 0},
        new int[]{0, -1, 1},
        new int[]{-1, 0, 1},
        new int[]{-1, 1, 0},
        new int[]{0, 1, -1},
        new int[]{0, 0,  0} //nullvektor
    };
    
    public TileHex(int X, int Y){
        super(X, Y);
        z = -X-Y;
    }
    public TileHex(int x, int y, int z){
        this(x,y);
    }
    
    @Override
    public PointHD toPixel(Layout layout){
        final Orientation M = layout.orientation;
        double X = (M.F0 * x + M.F1 * y) * layout.size.x;
        double Y = (M.F2 * x + M.F3 * y) * layout.size.y;
        return new PointHD(X + layout.origin.x, Y + layout.origin.y);
    }
    
    @Override
    public int[] fromPixel(int x, int y, Layout layout){
        final Orientation M = layout.orientation;
        PointHD pt = new PointHD(
                (x - layout.origin.x) / (double)layout.size.x,
                (y - layout.origin.y) / (double)layout.size.y
        );
        double q = M.B0 * pt.x + M.B1 * pt.y;
        double r = M.B2 * pt.x + M.B3 * pt.y;
        
        int rx = (int) Math.round(q);
        int ry = (int) Math.round(r);
        int rz = (int) Math.round(-q-r);

        double x_diff = Math.abs(rx - q);
        double y_diff = Math.abs(ry - r);
        double z_diff = Math.abs(rz - (-q-r));

        //eredmény javítása - normalizálás
        if (Double.compare(x_diff, y_diff) > 0 && Double.compare(x_diff, z_diff) > 0){
            rx = -ry-rz;
        }else if (Double.compare(y_diff, z_diff) > 0){
            ry = -rx-rz;
        }
        
        return new int[]{rx, ry};
    }
    
    @Override
    public String toString(){
        return String.format("[%d,%d,%d]", x,y,z);
    }
    
    PointHD hexCornerOffset(Layout layout, int corner)
    {
        Orientation M = layout.orientation;
        Point size = layout.size;
        double angle = 2.0 * Math.PI/6.0 * (corner + M.START_ANGLE);
        double X = (size.x+1) * Math.cos(angle);
        double Y = (size.y) * Math.sin(angle);
        return new PointHD(X, Y);
    }
    
    @Override
    public Polygon polygonCorners(Layout layout){
        Polygon corners = new Polygon();
        PointHD center = toPixel(layout);
        for (int i = 0; i < 6; i++){
            Point p = center.add(hexCornerOffset(layout, i)).toPoint();
            corners.addPoint(p.x, p.y);
        }
        return corners;
    }
    
    @Override
    public int[] add(int... i){
        return new int[]{x+i[0], y+i[1]};
    }
    @Override
    public int[] subtract(int... i){
        return new int[]{i[0]-x, i[1]-y};
    }
    @Override
    public int[] multiply(int... i){
        return new int[]{i[0]*x, i[1]*y};
    }
    
    int[] HexDirection(int dir){
        if(! (0<=dir && dir<6)) dir=6;
        return HEX_DIRECTIONS[dir];
    }
    
    @Override
    public int[] getNeighbor(int dir){
        return add(HexDirection(dir));
    }
    
    @Override
    public ArrayList<int[]> getNeighbors(){
        ArrayList<int[]> h = new ArrayList<>();
        for(int i=0; i<6; i++) h.add(getNeighbor(i));
        return h;
    }
}
