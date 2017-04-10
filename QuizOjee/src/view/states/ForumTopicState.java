package view.states;

import gameTools.state.State;
import view.Labels;
import view.MainWindow;
import view.Settings;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ForumTopicState extends State {
	MainWindow root;
	private JTextField txtfRefComment;
	public ForumTopicState(MainWindow r) {
		super(MainWindow.STATE_FORUM_TOPIC, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
		
		JScrollPane scrCommentList = new JScrollPane();
		JTextArea txtpnCommentInput = new JTextArea();
		JScrollPane scrNewComment = new JScrollPane(txtpnCommentInput);
		txtpnCommentInput.setLineWrap(true);
		
		JLabel lblTitle = new JLabel(Labels.LBL_TITLE_FORUM_TOPIC);
			lblTitle.setFont(Settings.FONT_TITLE);
		JLabel lblReferencedComment = new JLabel(Labels.LBL_REFERENCED_COMMENT);
		
		
		txtfRefComment = new JTextField();
			txtfRefComment.setEnabled(false);
			txtfRefComment.setEditable(false);
			txtfRefComment.setColumns(10);
		
		
		JButton btnAddComment = new JButton(Labels.BTN_ADD_COMMENT);
		JButton btnBack = new JButton(Labels.BTN_BACK);
			btnBack.addActionListener((e) -> {
				root.setState(MainWindow.STATE_FORUM);
			});
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(44)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblTitle)
							.addContainerGap())
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(scrCommentList, GroupLayout.DEFAULT_SIZE, 809, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblReferencedComment)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(txtfRefComment, GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE))
								.addComponent(scrNewComment, GroupLayout.DEFAULT_SIZE, 809, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnBack, GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, 445, Short.MAX_VALUE)
									.addComponent(btnAddComment, GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE)))
							.addGap(47))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(26)
					.addComponent(lblTitle)
					.addGap(18)
					.addComponent(scrCommentList, GroupLayout.PREFERRED_SIZE, 297, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblReferencedComment)
						.addComponent(txtfRefComment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrNewComment, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnAddComment, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBack, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE))
					.addGap(41))
		);
		setLayout(groupLayout);
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
	}
}
