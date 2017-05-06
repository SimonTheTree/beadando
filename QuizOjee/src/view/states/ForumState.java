package view.states;

import model.ForumTopic;
import view.Labels;
import view.MainWindow;
import view.Refreshable;
import view.Settings;
import view.components.AddForumTopicDialog;
import view.components.GButton;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import view.components.GLabel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumState extends DefaultState implements Refreshable {
	private MainWindow root;
	private List<GButton> topicButtons;
	private Map<GButton,ForumTopic> forumTopics;
	
	public ForumState(MainWindow r) {
		super(MainWindow.STATE_FORUM, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		
		root = r;
		
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh() {
		System.out.println("Refresh ForumState");
		setTopics();
		makeOthers();
	}
	
	public void onStart() {
		refresh();
	}
	
	private void setTopics() {
		topicButtons = new ArrayList<>();
		forumTopics = new HashMap<>();
		root.controller.refresh();
		List<ForumTopic> forumTopicList = root.controller.getForumTopics();
		if(forumTopics != null) {
			for(ForumTopic forumTopic : forumTopicList) {
				GButton btn = new GButton(forumTopic.getName());
				topicButtons.add(btn);
				forumTopics.put(btn,forumTopic);
			}
		}
	}

	private JPanel forumTopicPanelMaker() {
		JPanel kulso = new JPanel();
		kulso.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		BoxLayout panelLayout = new BoxLayout(panel,BoxLayout.PAGE_AXIS);
		panel.setLayout(panelLayout);
		//int counter = 0;
		panel.add(Box.createRigidArea(new Dimension(5,5)));
		for(GButton btn : topicButtons) {
			//tudom h nem szep, de ugy sincs atmeretezes
			//btn.setBounds(5, counter*40+5, Settings.MAIN_WINDOW_WIDTH-95, 35); 
			JPanel buttonPanel = new JPanel();
			//btn.setMargin(new Insets(5,5,5,5));
			buttonPanel.setLayout(new GridLayout(1,1,5,5));
			buttonPanel.add(btn);
			panel.add(buttonPanel);
			panel.add(Box.createRigidArea(new Dimension(5,5)));
			//set forumtopic states topic
			btn.addActionListener((e) -> {
				Settings.forum_currentTopic = forumTopics.get(btn);
				root.setState(MainWindow.STATE_FORUM_TOPIC);
			});
			//counter++;
		}
		kulso.add(panel,BorderLayout.NORTH);
		kulso.add(Box.createRigidArea(new Dimension(5,5)), BorderLayout.EAST);
		kulso.add(Box.createRigidArea(new Dimension(5,5)), BorderLayout.WEST);
		return kulso;
	}
	
	private void makeOthers() {
		this.removeAll();
		JPanel p = forumTopicPanelMaker();
		JScrollPane forumTopicPanel = new JScrollPane(p);
		
		GLabel lblForumTitle = new GLabel(Labels.LBL_TITLE_FORUM);
			lblForumTitle.setFont(Settings.FONT_TITLE);
		GLabel lblTopics = new GLabel(Labels.LBL_TOPICS);
		
		GButton btnNewTopic = new GButton(Labels.BTN_ADD_TOPIC);
		btnNewTopic.addActionListener((e) -> {
			new AddForumTopicDialog(root,true, this);
		});		
		
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
						.addComponent(forumTopicPanel))
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
					.addComponent(forumTopicPanel, GroupLayout.PREFERRED_SIZE, 367, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnBack, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(28, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
		//System.out.println(p.getSize());
		//System.out.println(btnNewTopic.getSize());
	}
}
