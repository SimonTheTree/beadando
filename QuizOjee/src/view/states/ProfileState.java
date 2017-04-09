package view.states;

import gameTools.state.State;
import view.MainWindow;
import view.Settings;

public class ProfileState extends State {
	MainWindow root;
	public ProfileState(MainWindow r) {
		super(MainWindow.STATE_PROFILE, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
