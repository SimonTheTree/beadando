package view.states;

import gameTools.state.State;
import view.Labels;
import view.MainWindow;
import view.Settings;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.awt.CardLayout;

public class ForumState extends State {
	MainWindow root;
	List<JButton> topics;
	
	public ForumState(MainWindow r) {
		super(MainWindow.STATE_FORUM, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		topics = new ArrayList<>();
		topics.add(new JButton("demoTopic1"));
		topics.add(new JButton("demoTopic2"));
		topics.add(new JButton("demoTopic3"));
		JPanel panel = new JPanel();
		JScrollPane scrollPane = new JScrollPane();
			for(int i = 0; i < topics.size(); i++){
				JButton btn = topics.get(i);
				btn.setBounds(5, i*40, Settings.MAIN_WINDOW_WIDTH-150, 35);
				panel.add(btn);
			}
			scrollPane.add(panel);
		JLabel lblForumTitle = new JLabel(Labels.LBL_TITLE_FORUM);
			lblForumTitle.setFont(Settings.FONT_TITLE);
		JLabel lblTopics = new JLabel(Labels.LBL_TOPICS);
		
		JButton btnNewTopic = new JButton(Labels.BTN_ADD_TOPIC);
		JButton btnBack = new JButton(Labels.BTN_BACK);		
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
