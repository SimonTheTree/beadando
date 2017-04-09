package view.states;

import java.awt.event.WindowEvent;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import gameTools.state.State;
import resources.Resources;
import view.Labels;
import view.MainWindow;
import view.Settings;

public class MainState extends State {
	MainWindow root;
	
	public MainState(MainWindow r) {
		super(MainWindow.STATE_MAIN, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
		
		JButton btnStartQuiz = new JButton(Labels.BTN_START_QUIZ);
			btnStartQuiz.addActionListener((e) -> {
				root.setState(MainWindow.STATE_QUIZ_SETTINGS);
			});
		JButton btnCreateGame = new JButton(Labels.BTN_CREATE_GAME);
			btnCreateGame.addActionListener((e) -> {
				root.setState(MainWindow.STATE_GAME_SETTINGS);
			});
		JButton btnQuit = new JButton(Labels.BTN_QUIT);
			btnQuit.addActionListener((e) -> {
				root.dispatchEvent(new WindowEvent(root, WindowEvent.WINDOW_CLOSING));
			});
		JButton btnJoin = new JButton(Labels.BTN_JOIN_GAME);
			btnJoin.addActionListener((e) -> {
				
			});
		JButton btnStats = new JButton(Labels.BTN_STATS);
			btnStats.addActionListener((e) -> {
				root.setState(MainWindow.STATE_STAISTICS);				
			});
		JButton btnForum = new JButton(Labels.BTN_FORUM);
			btnForum.addActionListener((e) -> {
				root.setState(MainWindow.STATE_FORUM);								
			});
		JButton btnProfile = new JButton(Labels.BTN_PROFILE);
			btnProfile.addActionListener((e) -> {
				root.setState(MainWindow.STATE_PROFILE);								
			});
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(127)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnProfile, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnForum, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnStats, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnJoin, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(btnQuit, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnStartQuiz, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
							.addComponent(btnCreateGame, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
					.addContainerGap(548, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(82)
					.addComponent(btnStartQuiz, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCreateGame, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnJoin, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnStats, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnForum, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnProfile, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.addGap(143)
					.addComponent(btnQuit, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(114, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		g.drawImage(Resources.MAIN_WINDOW_BACKGROUND, 0, 0, getWidth(), getHeight(), this);

	}
}
