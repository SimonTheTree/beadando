package view.states;

import javax.swing.JTextField;

import gameTools.state.State;
import view.MainWindow;
import view.Settings;

public class LoginState extends State {
	MainWindow root;
	public LoginState(MainWindow r) {
		super(MainWindow.STATE_LOGIN, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
