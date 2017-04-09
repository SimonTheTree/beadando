package view.states;

import gameTools.state.State;
import view.MainWindow;
import view.Settings;

public class GameState extends State {
	MainWindow root;
	
	public GameState(MainWindow r) {
		super(MainWindow.STATE_GAME, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
	}

}
