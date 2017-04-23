package resources;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import resources.ResourceLoader;


public class Resources {

	public static final BufferedImage MAIN_WINDOW_BACKGROUND = ResourceLoader.getImage("questionBackground.jpg");
	public static final BufferedImage WOOD_BTN_BG = ResourceLoader.getImage("woodBg2.jpg");
	
	public static final File FONTFILE_ARCHIVE = ResourceLoader.getFile("fonts/Archive.ttf");
	public static final File FONTFILE_SKETCH = ResourceLoader.getFile("fonts/Sketch College.ttf");
	public static final File FONTFILE_RHESPECT = ResourceLoader.getFile("fonts/Resphekt-Regular.ttf");
	public static final File FONTFILE_GRAVITY = ResourceLoader.getFile("fonts/Gravity-UltraLight.ttf");
	
	
	public static void load(){
		try {
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
