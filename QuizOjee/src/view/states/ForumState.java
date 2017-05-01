package view.states;

import gameTools.state.State;
import view.Labels;
import view.MainWindow;
import view.Settings;
import view.components.GButton;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import view.components.GLabel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.awt.CardLayout;

public class ForumState extends DefaultState {
	MainWindow root;
	List<GButton> topics;
	
	public ForumState(MainWindow r) {
		super(MainWindow.STATE_FORUM, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		
		JPanel panel = new JPanel();
		JScrollPane scrollPane;
		
		topics = new ArrayList<>();
		topics.add(new GButton("demoTopic1"));
		topics.add(new GButton("demoTopic2"));
		topics.add(new GButton("demoTopic3"));
		
		panel.setLayout(null);
			for(int i = 0; i < topics.size(); i++){
				GButton btn = topics.get(i);
				//tudom h nem szep, de ugy sincs atmeretezes
				btn.setBounds(5, i*40+5, Settings.MAIN_WINDOW_WIDTH-95, 35); 
				panel.add(btn);
				//set forumtopic states topic
				btn.addActionListener((e) -> {
					root.setState(MainWindow.STATE_FORUM_TOPIC);
				});
			}
		scrollPane = new JScrollPane(panel);
		
		GLabel lblForumTitle = new GLabel(Labels.LBL_TITLE_FORUM);
			lblForumTitle.setFont(Settings.FONT_TITLE);
		GLabel lblTopics = new GLabel(Labels.LBL_TOPICS);
		
		GButton btnNewTopic = new GButton(Labels.BTN_ADD_TOPIC);
		GButton btnBack = new GButton(Labels.BTN_BACK);		
			btnBack.addActionListener((e) -> {
				root.setState(MainWindow.STATE_MAIN);
			});
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(39)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblTopics)
						.addComponent(btnBack, GroupLayout.DEFAULT_SIZE, 817, Short.MAX_VALUE)
						.addComponent(btnNewTopic, GroupLayout.DEFAULT_SIZE, 817, Short.MAX_VALUE)
						.addComponent(lblForumTitle)
						.addComponent(scrollPane))
					.addContainerGap(44, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(33)
					.addComponent(lblForumTitle)
					.addGap(18)
					.addComponent(btnNewTopic, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblTopics)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 367, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnBack, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(28, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
		root = r;
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
