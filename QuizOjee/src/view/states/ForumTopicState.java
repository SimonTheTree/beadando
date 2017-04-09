package view.states;

import gameTools.state.State;
import view.MainWindow;
import view.Settings;

public class ForumTopicState extends State {
	MainWindow root;
	public ForumTopicState(MainWindow r) {
		super(MainWindow.STATE_FORUM_TOPIC, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
