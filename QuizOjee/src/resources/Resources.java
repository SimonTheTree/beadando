package resources;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import game.GameSettings;
import gameTools.DyeImage;
import resources.ResourceLoader;


public class Resources {

	public static final BufferedImage MAIN_WINDOW_BACKGROUND = ResourceLoader.getImage("questionBackground.jpg");
	public static final BufferedImage GAME_WAIT_FOR_JOIN_BG = ResourceLoader.getImage("waitJoinBG.jpg");
	public static final BufferedImage WOOD_BTN_BG = ResourceLoader.getImage("woodBg2.jpg");
	
	public static BufferedImage[] ARROW_RIGHT;
	
	public static final File FONTFILE_ARCHIVE = ResourceLoader.getFile("fonts/Archive.ttf");
	public static final File FONTFILE_SKETCH = ResourceLoader.getFile("fonts/Sketch College.ttf");
	public static final File FONTFILE_RHESPECT = ResourceLoader.getFile("fonts/Resphekt-Regular.ttf");
	public static final File FONTFILE_GRAVITY = ResourceLoader.getFile("fonts/Gravity-UltraLight.ttf");
	
	public static void loadGameIcons(GameSettings settings) {
		//load and paint icons
		ARROW_RIGHT = new BufferedImage[GameSettings.getInstance().COLORS.length+1];
		for(int i = 0; i < settings.COLORS.length; i++){
			ARROW_RIGHT[i] = DyeImage.dye(
				ResourceLoader.getImage("arrow_right.png"), 
				DyeImage.addTransparency(settings.COLORS[i], 125)
			);
        }
	}
	
	public static BufferedImage scaleImage(BufferedImage srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
	
	public static void load(){
		try {
			//load fonts
		     GraphicsEnvironment ge = 
		         GraphicsEnvironment.getLocalGraphicsEnvironment();
		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, FONTFILE_ARCHIVE));
		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, FONTFILE_SKETCH));
		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, FONTFILE_RHESPECT));
		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, FONTFILE_GRAVITY));
//		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, FONTFILE_ANTIPASTO2));
		     
		} catch (IOException|FontFormatException e) {
		     //Handle exception
		}
	}
	
	public static void main(String[] args){
		System.out.println("Tested all resources!");
	}
}
