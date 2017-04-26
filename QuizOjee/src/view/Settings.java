package view;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.UIManager;

import com.jcraft.jsch.jce.Random;

import game.Cell;
import game.players.Player;
import gameTools.map.Tile;

public class Settings {

	// --------------------------------------------------------------//
	// MAIN SETTINGS
	// --------------------------------------------------------------//
	public static final int MAIN_WINDOW_WIDTH = 900;
	public static final int MAIN_WINDOW_HEIGHT = 600;

	// --------------------------------------------------------------//
	// FONT SETTINGS
	// --------------------------------------------------------------//
//	public static final String FONT_TYPE_DEFAULT = "Dialog";
	public static final String FONT_ARCHIVE = "Archive";
	public static final String FONT_SKETCH = "Sketch College";
	public static final String FONT_RHESPECT = "Resphekt";
	public static final String FONT_GRAVITY = "Gravity";
	public static final Font FONT_DEFAULT = new Font(FONT_GRAVITY, Font.PLAIN, 14);
	public static final Font FONT_TITLE = new Font(FONT_ARCHIVE, Font.BOLD, 20);
	public static final Font FONT_SUB_TITLE = new Font(FONT_ARCHIVE, Font.BOLD, 15);
	public static final Font FONT_GBUTTON_DEFAULT = new Font(FONT_GRAVITY, Font.PLAIN, 16);
	public static final Font FONT_BUTTON_MAIN = new Font(FONT_ARCHIVE, Font.BOLD, 18);
	public static final Font FONT_QUESTION = new Font(FONT_GRAVITY, Font.ITALIC, 18);
	public static final Font FONT_ERROR_MSG = new Font(FONT_GRAVITY, Font.BOLD, 12);

	// --------------------------------------------------------------//
	// COLOR SETTINGS
	// --------------------------------------------------------------//
	public static Color color_GButtonFont = Color.WHITE;
	public static Color color_GButton = new Color(103, 45, 0, 200);
	public static Color color_GButtonHover =  new Color(255, 143, 0, 200);
	public static Color color_GButtonClick = Color.RED;
	public static Color color_GLabelFont = Color.WHITE;
	public static Color color_error = Color.RED;
	public static Color color_success = new Color(0, 132, 0);
	
	// --------------------------------------------------------------//
	// QUIZ SETTINGS
	// --------------------------------------------------------------//
	public static int quiz_numOfQuestions;
//	public static List<Topic> quiz_topicList;
	public static int quiz_difficulity;

	public static String gameServer; //connection string, {GameCreatorState} -> {GameState}
	
	public static void init(){
		setUIFont(new javax.swing.plaf.FontUIResource(FONT_ARCHIVE,Font.PLAIN,12));
	}
	
	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value != null && value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}
}
