package view;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Random;

import javax.swing.UIManager;


import game.Cell;
import game.players.Player;
import gameTools.map.Tile;
import model.ForumTopic;

public class Settings {

	public static final Random RANDOM = new Random();
	
	// --------------------------------------------------------------//
	// MAIN SETTINGS
	// --------------------------------------------------------------//
	public static final int MAIN_WINDOW_WIDTH = 900;
	public static final int MAIN_WINDOW_HEIGHT = 600;
	
	public static final String ENV_KABINET = "Kabinet GNOME3 BUGOS!!!";
	public static final String ENV_NORMAL = "nem GNOME3";
	// TODO buug! Kabinetben a question dialognál a gombok kicsúsznak a guiból alul.
	// TODO rqdialog is túl kicsi, nem fér el az eredmény, kéne +30 per player min +30
	public static final String ENV = ENV_KABINET;
	

	// --------------------------------------------------------------//
	// FONT SETTINGS
	// --------------------------------------------------------------//
//	public static final String FONT_TYPE_DEFAULT = "Dialog";
	public static final String FONT_NAME_ARCHIVE = "Archive";
	public static final String FONT_NAME_SKETCH = "Sketch College";
	public static final String FONT_NAME_RHESPECT = "Resphekt";
	public static final String FONT_NAME_GRAVITY = "Gravity";
	public static final Font FONT_DEFAULT = new Font(FONT_NAME_GRAVITY, Font.PLAIN, 14);
	public static final Font FONT_GBUTTON_DEFAULT = new Font(FONT_NAME_GRAVITY, Font.PLAIN, 16);
	public static final Font FONT_TITLE = new Font(FONT_NAME_ARCHIVE, Font.BOLD, 20);
	public static final Font FONT_SUB_TITLE = new Font(FONT_NAME_ARCHIVE, Font.BOLD, 15);
	public static final Font FONT_BUTTON_MAIN = new Font(FONT_NAME_ARCHIVE, Font.BOLD, 18);
	public static final Font FONT_QUESTION = new Font(FONT_NAME_GRAVITY, Font.ITALIC, 18);
	public static final Font FONT_ERROR_MSG = new Font(FONT_NAME_GRAVITY, Font.BOLD, 12);

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
	public static Color color_GButtonFont_inGame = Color.WHITE;
	public static Color color_GButton_inGame = new Color(50, 50, 50);
	public static Color color_GButtonHover_inGame =  new Color(70, 70,70);
	public static Color color_GButtonClick_inGame = new Color(60, 60,100);
	public static Color color_lightGray = new Color(190, 190, 190);
	public static Color color_lightGray2 = new Color(210, 210, 210);
	public static Color color_lightGray3 = new Color(230, 230, 230);
	
	
	// --------------------------------------------------------------//
	// QUIZ SETTINGS                                                 //
	// --------------------------------------------------------------//
	public static int game_numOfQuestions = 10;
	public static int game_numOfRaceQuestions = 10;
//	public static List<Topic> quiz_topicList;
	public static int game_difficulity;
	public static String gameServer; //connection string, {GameCreatorState} -> {GameState}

	public static int game_numOfPlayers;
	public static String game_type;
	public static List<Integer> game_topicList;
	public static int game_TPP;
	
	public static final String GAME_TYPE_BLITZKRIEG = "blitzkrieg";
	public static final String GAME_TYPE_10_ROUNDS = "10 rounds";
	public static final String GAME_TYPE_LAST_MAN_STAND = "last man";

	// --------------------------------------------------------------//
	// FORUM SETTINGS                                                //
	// --------------------------------------------------------------//
	public static ForumTopic forum_currentTopic = null;
	
	
	public static void init(){
		setUIFont(new javax.swing.plaf.FontUIResource(FONT_NAME_GRAVITY,Font.PLAIN,12));
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
