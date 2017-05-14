/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.players.Player;
import gameTools.LineHD;
import gameTools.PointHD;
import gameTools.map.Layout;
import gameTools.map.Map;
import gameTools.map.TileHex;
import view.Settings;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.PathIterator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Egy orszag a terkepen, cellak osszessege
 * 
 * @author ganter
 */
public class Territory implements Serializable, Comparable<Territory> {
	private static int numOfTerritories = 0;
	public static final Territory NULL_TERRITORY;
	static{
		//ensure that NULL_TERRITOY has a totally unique id
		numOfTerritories = -1;
		NULL_TERRITORY = new Territory();
		numOfTerritories = 0;
	}
	
	
	public final List<Cell> cells; // inside
	public final int id = ++numOfTerritories;
	private Player owner;
	private boolean highlighted;
	private boolean needsRender = true;
	
	public void markForAttack() {
		for (Cell c : cells) {
			c.setMarkedForAttack(true);
		}
		
		new Thread() {
			@Override
			public void run() {
				Thread.currentThread().setName("Territory.markForAttack");
				try {
					Thread.sleep(5000);
					for (Cell c : cells) {
						c.setMarkedForAttack(false);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}.start();
		
	}
	

 	public Territory() {
		cells = new ArrayList<>();
	}

	void add(Cell c) {
		cells.add(c);
		c.setOwner(this);
	}

	public void setOwner(Player p) {
		if (owner != null) {
			owner.removeTerritory(this);
		}
		this.owner = p;
		p.addTerritory(this);
	}
	
	public Player getOwner() {
		return this.owner;
	}

	public List<Territory> getNeighborTerritories(Map map) {
		List<Cell> neighborsOfCell;
		List<Territory> neighborsOfTerritory = new ArrayList<>(); // unowned
																	// neighboring
																	// cells of
																	// the
																	// territory

		// collect the territories
		for (Cell cell : cells) {
			neighborsOfCell = map.getNeighborTiles(cell.x, cell.y);
			for (Cell c : neighborsOfCell) {
				if ((!neighborsOfTerritory.contains(c.getOwner())) && (!this.equals(c.getOwner()))) {
					neighborsOfTerritory.add(c.getOwner());
				}
			}
		}

		return neighborsOfTerritory;

	}

	public List<Cell> getCells() {
		return cells;
	}

	public boolean isNeighbor(Territory t, Map map) {
		List<Cell> unownedNeighborsOfCell;

		// check each neighbor cell (of any cell in this) if its owner is t
		for (Cell cell : cells) {
			unownedNeighborsOfCell = map.getNeighborTiles(cell.x, cell.y);
			for (Cell c : unownedNeighborsOfCell) {
				if (t.equals(c.getOwner())) {
					return true;
				}
			}
		}

		return false;
	}

	public void touch() {
		this.needsRender = true;
		for (Cell c : cells) {
			c.touch();
		}
	}

	public Point getCenter() {
		double sumX = 0, sumY = 0;
		double cellN = cells.size();
		for (Cell c : cells) {
			sumX += c.x / cellN;
			sumY += c.y / cellN;
		}
		return new Point((int) sumX, (int) sumY);
	}

	public void highlight() {
		this.highlighted = true;
		for (Cell c : cells) {
			c.highlight();
		}
		touch();
	}

	public void unLight() {
		this.highlighted = false;
		for (Cell c : cells) {
			c.unLight();
		}
		touch();
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void render(Graphics2D g, Layout layout) {
		if (!needsRender) {
			return;
		} else {
			needsRender = false;
		}
		if (owner != null) {
			g.setColor(owner.getColor());
		} else {
			g.setColor(Color.GRAY);
		}
		for (Cell c : cells) {
			c.render(g, layout);
		}
		
		g.setColor(Color.BLACK);
//		if(id==1){
			for(LineHD l : getBorderLines(layout)){
	            g.drawLine(l.A.getIntx(), l.A.getInty(), l.B.getIntx(), l.B.getInty());
	        }
//		}

		// draw debuginfo
		if(GameSettings.getInstance().dbg) {
			try {
				g.setColor(Color.WHITE);
				Cell c = new Cell(getCenter().x, getCenter().y);
				Point p = c.toPixel(layout).toPoint();
				int X = p.x;
				int Y = p.y + 15;
				String s = String.format("%d", id);
				g.setFont(new Font("Courier New", Font.PLAIN, 20));
				g.drawString(s, X, Y);			
				Y += 15;
				s = getOwner().getUser().getUsername();
				g.setFont(new Font("Courier New", Font.PLAIN, 20));
				g.drawString(s, X, Y);			
			}catch(NullPointerException e) {
				
			}
		} else {
//			System.out.println("nodebug");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof Territory) {
			Territory t = (Territory) o;
			return (id == t.id);
		} else {
			return false;
		}
	}
	
	@Override
	public int compareTo(Territory t) {
		return id-t.id;
	}
	
	Set<LineHD> getBorderLines(Layout layout){
//		System.out.println();
//		System.out.println();
//		System.out.println("calcBorder");
//		System.out.println();
//		System.out.println();
    	Set<LineHD> border = new TreeSet<>(LineHD.COMPARATOR);
    	for(Cell c : cells){
    		for(LineHD l : c.getBorderLines(layout)){
    			if(border.contains(l)){
    				border.remove(l);
//    				System.out.println("KI  : " + l);
    			} else {
    				border.add(l);
//    				System.out.println("BELE: " + l);
    			}
    		}
    	}
    	return border;
    }

	@Override
	public String toString() {
		return "Territory#"+id+" Owner:"+owner.getUser().getUsername() +"("+ owner.getId() +")";
	}
}
