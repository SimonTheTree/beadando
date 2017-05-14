package view.states;

import gameTools.state.State;
import view.Labels;
import view.MainWindow;
import view.Settings;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import view.components.GLabel;
import view.components.PanelTopicList;

import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import game.GameBoard;
import game.GameServer;
import game.GameSettings;
import view.components.GButton;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;

public class GameCreatorState extends DefaultState {
	MainWindow root;
	
	private GButton btnPrevMap;
	private GButton btnNextMap;
	private GButton btnStart;
	private GButton btnCancel;
	
	private JSpinner spinner;
	
	private JSpinner spinTpp;

	private ButtonGroup btnGroup;
	private JRadioButton rdbtn1LastOneStanding;
	private JRadioButton rdbtn30rounds;
	private JRadioButton rdbtnBlitzkrieg;
	private JComboBox cbSelectMap;
	public GameCreatorState(MainWindow r) {
		super(MainWindow.STATE_GAME_SETTINGS, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
//		maps = r.controller.getMaps();
		inputManager.addKeyMapping("ESC", KeyEvent.VK_ESCAPE);
		inputManager.addKeyMapping("s", KeyEvent.VK_S);
		
		JPanel paneMapDisplay = new JPanel();
		GameSettings.getInstance().GAME_WIDTH = paneMapDisplay.getWidth();
		GameSettings.getInstance().SCREEN_WIDTH = paneMapDisplay.getWidth();
		GameSettings.getInstance().GAME_HEIGHT = paneMapDisplay.getHeight();
		GameSettings.getInstance().SCREEN_HEIGHT = paneMapDisplay.getHeight();
		GameSettings.getInstance().GAME_INFOLABEL_HEIGHT = 0;
		
		PanelTopicList topicsPanel = new PanelTopicList(root.controller.getTopicsWithQuestionNumbers());
		
		GLabel lblTitle = new GLabel(Labels.LBL_TITLE_GAME_CREATOR);
			lblTitle.setFont(Settings.FONT_TITLE);
		GLabel lblTopics = new GLabel(Labels.LBL_TOPICS);
		GLabel lblMap = new GLabel(Labels.LBL_MAP);
		GLabel lblType = new GLabel(Labels.LBL_TYPE);	
		
		btnPrevMap = new GButton("<");
		btnNextMap = new GButton(">");
		btnStart = new GButton(Labels.BTN_START);
			btnStart.addActionListener((e) -> {
				Settings.game_numOfPlayers = (Integer) spinner.getValue();
				Settings.game_TPP = (Integer) spinTpp.getValue();
				root.gameServer = new GameServer();
				root.gameServer.createGame();
				System.out.println("game created switching to gamestate");
				int counter = 0;
				while(!root.gameServer.isLyukendzsoint()){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					//System.out.print("!");
					//if(counter++%100 == 0) System.out.println();
				};
				
				Settings.gameServer = "localhost";
				Settings.game_topicList = topicsPanel.getListOfSelectedTopicIDs();
				if (Settings.game_numOfQuestions > root.controller.getNumOfQuestions(0, root.controller.getMaxDifficulty(), Settings.game_topicList)) {
					root.displayError(Labels.MSG_NOT_ENOUGH_QUESTIONS_FOR_TOPICS);
				} else {
					root.setState(MainWindow.STATE_GAME);
				}
			});
		btnCancel = new GButton(Labels.BTN_CANCEL);
			btnCancel.addActionListener((e) -> {
				root.setState(MainWindow.STATE_MAIN);
			});
				
		cbSelectMap = new JComboBox();
		btnGroup = new ButtonGroup();
		rdbtn1LastOneStanding = new JRadioButton(Labels.LBL_LAST_ONE_STANDING);
			rdbtn1LastOneStanding.addActionListener((e) -> {
				Settings.game_type = Settings.GAME_TYPE_LAST_MAN_STAND;
			});	
		rdbtn30rounds = new JRadioButton(Labels.LBL_30_ROUNDS);
			rdbtn30rounds.addActionListener((e) -> {
				Settings.game_type = Settings.GAME_TYPE_10_ROUNDS;
			});	
		rdbtnBlitzkrieg = new JRadioButton(Labels.LBL_BLITZKIREG);
			rdbtnBlitzkrieg.addActionListener((e) -> {
				Settings.game_type = Settings.GAME_TYPE_BLITZKRIEG;
			});	
		btnGroup.add(rdbtn1LastOneStanding);
		btnGroup.add(rdbtn30rounds);
		btnGroup.add(rdbtnBlitzkrieg);
		
		GLabel lblPlayerNum = new GLabel(Labels.LBL_NUMBER_OF_PLAYERS);
		
		spinner = new JSpinner();
			spinner.setValue(2);
		
		GLabel lblTPP = new GLabel(Labels.LBL_TERRITORIES_LOST_PER_PLAYER);
		
		spinTpp = new JSpinner();
			spinTpp.setValue(5);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(38)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(topicsPanel, GroupLayout.PREFERRED_SIZE, 435, GroupLayout.PREFERRED_SIZE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnPrevMap, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(paneMapDisplay, GroupLayout.PREFERRED_SIZE, 245, GroupLayout.PREFERRED_SIZE)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
											.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(btnNextMap, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
											.addGap(483))
										.addGroup(groupLayout.createSequentialGroup()
											.addGap(142)
											.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
												.addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 335, GroupLayout.PREFERRED_SIZE)
												.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
													.addComponent(lblType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addGroup(groupLayout.createSequentialGroup()
														.addComponent(lblMap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
															.addComponent(cbSelectMap, GroupLayout.PREFERRED_SIZE, 288, GroupLayout.PREFERRED_SIZE)
															.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
																.addComponent(rdbtnBlitzkrieg, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																.addComponent(rdbtn30rounds, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																.addComponent(rdbtn1LastOneStanding, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
													.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
														.addGroup(groupLayout.createSequentialGroup()
															.addComponent(lblPlayerNum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
															.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
															.addComponent(spinner, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE))
														.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
															.addComponent(lblTPP, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE)
															.addPreferredGap(ComponentPlacement.RELATED)
															.addComponent(spinTpp, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE))))
												.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 335, GroupLayout.PREFERRED_SIZE)))))
								.addComponent(lblTopics, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(35)
							.addComponent(lblTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(77)
									.addComponent(btnPrevMap, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
											.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
												.addComponent(lblMap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(cbSelectMap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
											.addGap(18)
											.addComponent(lblType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addGap(84)
											.addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
												.addComponent(lblTPP, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
												.addComponent(spinTpp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
										.addComponent(paneMapDisplay, GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)))))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(121)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(rdbtn1LastOneStanding)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(rdbtn30rounds)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(rdbtnBlitzkrieg)
									.addGap(18)
									.addComponent(lblPlayerNum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addComponent(btnNextMap, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE))))
					.addPreferredGap(ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblTopics, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(topicsPanel, GroupLayout.PREFERRED_SIZE, 227, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)))
					.addGap(32))
		);
		setLayout(groupLayout);
		
		// TODO Auto-generated constructor stub
	}

	public void onStart(){
		if(root.gameServer != null && root.gameServer.host != null) {
			root.gameServer.host.abort();
		}
	}
	
	@Override
	public void update() {
		if(inputManager.isKeyTyped("s")){btnStart.doClick();}
		if(inputManager.isKeyTyped("next")){btnNextMap.doClick();}
		if(inputManager.isKeyTyped("prev")){btnPrevMap.doClick();}
		if(inputManager.isKeyTyped("ESC")){btnCancel.doClick();}
		
//		currentGameboard.render(paneMapDisplay);
	}
}
