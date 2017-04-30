package model;

public class Map {

	private int mapId;
	private String name;
	private long terrain;
	
	public int getMapId() {
		return mapId;
	}
	public void setMapId(int mapId) {
		this.mapId = mapId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getTerrain() {
		return terrain;
	}
	public void setTerrain(long terrain) {
		this.terrain = terrain;
	}
	
	public String toString() {
		return "[(Map"+mapId+")"+name+" "+terrain+"]";
	}
}
