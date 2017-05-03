package view.states;

import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import javax.swing.GroupLayout;
import view.components.GButton;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import gameTools.state.State;
import resources.Resources;
import view.Labels;
import view.MainWindow;
import view.Settings;

public class MainState extends DefaultState {
	MainWindow root;
	
	private GButton btnStartQuiz;
	private GButton btnCreateGame;
	private GButton btnJoin;
	private GButton btnStats;
	private GButton btnForum;
	private GButton btnProfile;
	private GButton btnLogOut;
	private GButton btnQuit;
	
	public MainState(MainWindow r) {
		super(MainWindow.STATE_MAIN, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
		
		inputManager.addKeyMapping("qiuz", KeyEvent.VK_Q);
		inputManager.addKeyMapping("game", KeyEvent.VK_G);
		inputManager.addKeyMapping("join", KeyEvent.VK_J);
		inputManager.addKeyMapping("stats", KeyEvent.VK_S);
		inputManager.addKeyMapping("forum", KeyEvent.VK_F);
		inputManager.addKeyMapping("profile", KeyEvent.VK_P);
		inputManager.addKeyMapping("logout", KeyEvent.VK_L);
		inputManager.addKeyMapping("quit", KeyEvent.VK_ESCAPE);
		
		btnStartQuiz = new GButton(Labels.BTN_START_QUIZ);
			btnStartQuiz.setFont(Settings.FONT_BUTTON_MAIN);
			btnStartQuiz.addActionListener((e) -> {
				root.setState(MainWindow.STATE_QUIZ_SETTINGS);
			});
		btnCreateGame = new GButton(Labels.BTN_CREATE_GAME);
			btnCreateGame.setFont(Settings.FONT_BUTTON_MAIN);
			btnCreateGame.addActionListener((e) -> {
				root.setState(MainWindow.STATE_GAME_SETTINGS);
			});
		btnQuit = new GButton(Labels.BTN_QUIT);
			btnQuit.setFont(Settings.FONT_BUTTON_MAIN);
			btnQuit.addActionListener((e) -> {
				root.dispatchEvent(new WindowEvent(root, WindowEvent.WINDOW_CLOSING));
			});
		btnJoin = new GButton(Labels.BTN_JOIN_GAME);
			btnJoin.setFont(Settings.FONT_BUTTON_MAIN);
			btnJoin.addActionListener((e) -> {
				Settings.gameServer = JOptionPane.showInputDialog(root, Labels.MSG_ENTER_IP_ADDRESS);
				root.setState(MainWindow.STATE_GAME);
			});
		btnStats = new GButton(Labels.BTN_STATS);
			btnStats.setFont(Settings.FONT_BUTTON_MAIN);
			btnStats.addActionListener((e) -> {
				root.setState(MainWindow.STATE_STAISTICS);				
			});
		btnForum = new GButton(Labels.BTN_FORUM);
			btnForum.setFont(Settings.FONT_BUTTON_MAIN);
			btnForum.addActionListener((e) -> {
				root.setState(MainWindow.STATE_FORUM);								
			});
		btnProfile = new GButton(Labels.BTN_PROFILE);
			btnProfile.setFont(Settings.FONT_BUTTON_MAIN);
			btnProfile.addActionListener((e) -> {
				root.setState(MainWindow.STATE_PROFILE);								
			});
		btnLogOut = new GButton(Labels.BTN_LOGOUT);
			btnLogOut.setFont(Settings.FONT_BUTTON_MAIN);
			btnLogOut.addActionListener((e) -> {
				root.setUser(null);
				root.setState(MainWindow.STATE_LOGIN);								
			});
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(127)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnLogOut, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
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
					.addGap(98)
					.addComponent(btnLogOut, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnQuit, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(114, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
	
	@Override
	public void update(){
		if (inputManager.isKeyTyped("qiuz"   )) { btnStartQuiz.doClick();}
		if (inputManager.isKeyTyped("game"   )) { btnCreateGame.doClick();}
		if (inputManager.isKeyTyped("join"   )) { btnJoin.doClick();}
		if (inputManager.isKeyTyped("stats"  )) { btnStats.doClick();}
		if (inputManager.isKeyTyped("forum"  )) { btnForum.doClick();}
		if (inputManager.isKeyTyped("profile")) { btnProfile.doClick();}
		if (inputManager.isKeyTyped("logout" )) { btnLogOut.doClick();}
		if (inputManager.isKeyTyped("quit"   )) { btnQuit.doClick();}
		
	}

}
