package view.states;

import gameTools.state.State;
import view.Labels;
import view.MainWindow;
import view.Settings;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import view.components.GLabel;
import java.awt.FlowLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import view.components.GButton;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;

public class GameCreatorState extends DefaultState {
	MainWindow root;
	
	public GameCreatorState(MainWindow r) {
		super(MainWindow.STATE_GAME_SETTINGS, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
		
		JPanel paneMapDisplay = new JPanel();
		JScrollPane scrollPane = new JScrollPane();
		
		GLabel lblTitle = new GLabel(Labels.LBL_TITLE_GAME_CREATOR);
			lblTitle.setFont(Settings.FONT_TITLE);
		GLabel lblTopics = new GLabel(Labels.LBL_TOPICS);
		GLabel lblMap = new GLabel(Labels.LBL_MAP);
		GLabel lblType = new GLabel(Labels.LBL_TYPE);	
		
		GButton btnPrevMap = new GButton("<");
		GButton btnNextMap = new GButton(">");
		GButton btnStart = new GButton(Labels.BTN_START);
			btnStart.addActionListener((e) -> {
				root.setState(MainWindow.STATE_GAME);
			});
		GButton btnCancel = new GButton(Labels.BTN_CANCEL);
			btnCancel.addActionListener((e) -> {
				root.setState(MainWindow.STATE_MAIN);
			});
				
		JComboBox cbSelectMap = new JComboBox();
		
		JRadioButton rdbtn1LastOneStanding = new JRadioButton(Labels.LBL_LAST_ONE_STANDING);
		JRadioButton rdbtn30rounds = new JRadioButton(Labels.LBL_30_ROUNDS);
		JRadioButton rdbtnBlitzkrieg = new JRadioButton(Labels.LBL_BLITZKIREG);
		
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(38)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblTitle)
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 435, GroupLayout.PREFERRED_SIZE)
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
													.addComponent(lblType)
													.addGroup(groupLayout.createSequentialGroup()
														.addComponent(lblMap)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
															.addComponent(rdbtn1LastOneStanding)
															.addComponent(rdbtn30rounds)
															.addComponent(rdbtnBlitzkrieg)
															.addComponent(cbSelectMap, GroupLayout.PREFERRED_SIZE, 288, GroupLayout.PREFERRED_SIZE))))
												.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 335, GroupLayout.PREFERRED_SIZE)))))
								.addComponent(lblTopics))))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(35)
							.addComponent(lblTitle)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(77)
									.addComponent(btnPrevMap, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
											.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
												.addComponent(lblMap)
												.addComponent(cbSelectMap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
											.addGap(18)
											.addComponent(lblType))
										.addComponent(paneMapDisplay, GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)))))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(121)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(rdbtn1LastOneStanding)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(rdbtn30rounds)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(rdbtnBlitzkrieg))
								.addComponent(btnNextMap, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE))))
					.addPreferredGap(ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblTopics)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 227, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)))
					.addGap(32))
		);
		setLayout(groupLayout);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}
}
