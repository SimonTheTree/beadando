package mapcreator;

import java.awt.Graphics2D;

import gameTools.map.Layout;
import gameTools.map.TileHex;

public class EditorTile  extends TileHex<EditorTile>{

	
	
	public EditorTile(int X, int Y) {
		super(X, Y);
	}

	@Override
	public EditorTile newTile(int... i) {
		return new EditorTile(i[0], i[1]);
	}

	@Override
	public void render(Graphics2D g, Layout l) {
		// TODO Auto-generated method stub
		
	}

}
