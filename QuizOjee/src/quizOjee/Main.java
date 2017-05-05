package quizOjee;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.Map;

import controller.Commands;
import controller.Controller;
import controller.GameClient;
import controller.GameHost;
import controller.GameInputListener;
import controller.GameMessage;
import controller.PasswordCoder;
import controller.exceptions.GameIsStartedException;
import controller.exceptions.HostDoesNotExistException;
import game.Cell;
import game.GameBoard;
import game.Territory;
import gameTools.map.Layout;
import gameTools.map.Orientation;
import gameTools.map.generators.MapGeneratorHexRectangleFlat;
import view.Labels;
import view.components.KDialog;
import model.Statistics;
import model.Topic;
import model.User;


public class Main {
	
	public static void main (String[] args){
		System.out.println("hello");
		
		Controller c = new Controller();
		
	}
	
}
