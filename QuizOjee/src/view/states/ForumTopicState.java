package view.states;

import gameTools.state.State;
import view.Labels;
import view.MainWindow;
import view.Refreshable;
import view.Settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;

import view.components.GLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextField;
import view.components.GButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.ForumEntry;

public class ForumTopicState extends DefaultState implements Refreshable {
	private MainWindow root;
	private JTextArea refCommentTextArea = new JTextArea();
	private ForumEntry refComment = null;
	private JTextArea txtpnCommentInput = new JTextArea();
	private GLabel lblTitle = new GLabel(Labels.LBL_TITLE_FORUM_TOPIC);
	private List<ForumEntry> forumEntries;
	private int entriesNumber;
	private int maxEntriesOnThePage = 50;
	private int page = 0;
	private Color parityColor = new Color(210,210,210);
	
	public ForumTopicState(MainWindow r) {
		super(MainWindow.STATE_FORUM_TOPIC, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
		
		refCommentTextArea.setForeground(new Color(0,0,0));
		
	}

	@Override
	public void refresh() {
		refComment = null;
		refCommentTextArea.setText("");
		setForumEntries();
		makeOthers();
	}
	
	public void onStart() {
		refresh();
	}
	
	public void update() {
		// TODO Auto-generated method stub
	}
	
	private JPanel forumEntriesPanelMaker() {
		JPanel re = new JPanel();
		LayoutManager panelLayout = new BoxLayout(re,BoxLayout.Y_AXIS);
		//panelLayout.setVgap(3);
		//panelLayout.setHgap(3);
		re.setLayout(panelLayout);
		for(int i=0;i<forumEntries.size();++i) {
			ForumEntry forumEntry = forumEntries.get(i);
			JPanel forumEntryPanel = commentPanelMaker(forumEntry,i%2==0?false:true);
			//forumEntryPanel.setBounds(x, y, maxEntriesOnThePage, entriesNumber);
			re.add(forumEntryPanel);
		}
		return re;
	}
	
	/*private JPanel forumEntryPanelMaker(ForumEntry forumEntry) {
		JPanel rere = new JPanel();
		rere.setLayout(new BorderLayout());
		JPanel re = new JPanel();
		re.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		re.setLayout(new BorderLayout());
		
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BorderLayout());
		JTextArea textArea = jTextAreaDefault(forumEntry.getText());
		System.out.println(forumEntry.getRefComment());
		if(forumEntry.getRefComment() != -1) {
			ForumEntry ref = null;
			for(ForumEntry fe : forumEntries) {
				if(fe.getCommentId() == forumEntry.getRefComment()) {
					ref = fe;
				}
			}
			textPanel.add(forumEntryPanelMaker(ref), BorderLayout.NORTH);
			//textArea.add
		}
		textPanel.add(textArea, BorderLayout.CENTER);

		re.add(textPanel, BorderLayout.CENTER);

		re.add(answerButtonMaker(forumEntry), BorderLayout.EAST);

		rere.add(re,BorderLayout.CENTER);
		rere.add(Box.createRigidArea(new Dimension(15,15)), BorderLayout.WEST);
		return rere;
	}*/
	
	private JPanel spacePanelMaker(Component c, boolean upDown, boolean rightLeft) {
		JPanel re = new JPanel();
		re.setLayout(new BorderLayout());
		re.add(c);
		if(upDown) {
			//re.add(Box.createRigidArea(new Dimension(15,15)), BorderLayout.NORTH);
			re.add(Box.createRigidArea(new Dimension(15,15)), BorderLayout.SOUTH);
		}
		if(rightLeft) {
			re.add(Box.createRigidArea(new Dimension(15,15)), BorderLayout.EAST);
			re.add(Box.createRigidArea(new Dimension(15,15)), BorderLayout.WEST);
		}
		return re;
	}
	
	private JPanel commentPanelMaker(ForumEntry forumEntry, boolean parity) {
		JPanel re = new JPanel();
		re.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		re.setLayout(new BorderLayout());
		re.add(authorPanelMaker(forumEntry.getAuthor(),parity), BorderLayout.NORTH);
		JPanel spacePanel =spacePanelMaker(refAndTextPanelMaker(forumEntry, parity),true,true);
		if(parity) {
			spacePanel.setBackground(parityColor);
			spacePanel.setOpaque(true);
		}
		re.add(spacePanel, BorderLayout.CENTER);
		re.add(datePanelMaker(forumEntry.getDate().toString(),parity), BorderLayout.SOUTH);
		return re;
	}
	
	private JPanel refAndTextPanelMaker(ForumEntry forumEntry, boolean parity) {
		JPanel re = new JPanel();
		re.setLayout(new BorderLayout());
		JPanel refCommentPanel = refCommentPanelMaker(forumEntry, parity);
		if(refCommentPanel != null) {
			re.add(refCommentPanel, BorderLayout.CENTER);
			re.add(textPanelMaker(forumEntry,parity), BorderLayout.SOUTH);
		} else {
			re.add(textPanelMaker(forumEntry,parity), BorderLayout.NORTH);
		}
		return re;
	}
	
	private JPanel textPanelMaker(ForumEntry forumEntry, boolean parity) {
		JPanel re = new JPanel();
		re.setLayout(new BorderLayout());
		re.add(jTextAreaDefault(forumEntry.getText(),true), BorderLayout.CENTER);
		re.add(answerButtonPanelMaker(forumEntry, parity), BorderLayout.EAST);
		return re;
	}
	
	private JPanel refCommentPanelMaker(ForumEntry forumEntry,boolean parity) {
		JPanel re = new JPanel();
		re.setLayout(new BorderLayout());
		if(forumEntry.getRefComment() == -1) {
			return null;
		} else {
			ForumEntry ref = null;
			for(ForumEntry fe : forumEntries) {
				if(fe.getCommentId() == forumEntry.getRefComment()) {
					ref = fe;
				}
			}
			JPanel spacePanel =spacePanelMaker(commentPanelMaker(ref,!parity),true,true);
			if(parity) {
				spacePanel.setBackground(parityColor);
				spacePanel.setOpaque(true);
			}
			re.add(spacePanel, BorderLayout.CENTER);
		}
		return re;
	}
	
	private JPanel answerButtonPanelMaker(ForumEntry forumEntry, boolean parity) {
		JPanel re = new JPanel();
		if(parity) {
			re.setBackground(parityColor);
			re.setOpaque(true);
		}
		re.setLayout(new BorderLayout());
		GButton answerButton = new GButton(Labels.BTN_ANSWER);
		answerButton.addActionListener((e) -> {
			refComment = forumEntry;
			refCommentTextArea.setText(forumEntry.getText());
		});
		re.add(answerButton,BorderLayout.SOUTH);
		return re;
	}
	
	private JPanel authorPanelMaker(String author, boolean parity) {
		JPanel re = new JPanel();
		if(parity) {
			re.setBackground(parityColor);
			re.setOpaque(true);
		}
		re.setLayout(new FlowLayout(FlowLayout.LEFT));
		re.add(jTextAreaDefault(author+": ",false));
		return re;
	}
	
	private JPanel datePanelMaker(String date, boolean parity) {
		JPanel re = new JPanel();
		if(parity) {
			re.setBackground(parityColor);
			re.setOpaque(true);
		}
		re.setLayout(new FlowLayout(FlowLayout.RIGHT));
		re.add(jTextAreaDefault(date,false));
		return re;
	}
	//NE MATASS :P
	
	private void setForumEntries() {
		entriesNumber = root.controller.getForumEntriesCount(Settings.forum_currentTopic);
		maxEntriesOnThePage = entriesNumber;
		forumEntries = root.controller.getSomeForumEntries(Settings.forum_currentTopic, page*maxEntriesOnThePage, (page+1)*maxEntriesOnThePage);
	}
	
	private JPanel refCommentPanelMaker() {
		JPanel re =  new JPanel();
		re.setLayout(new BorderLayout());
		refCommentTextArea.setEditable(false);
		refCommentTextArea.setColumns(10);
		GButton resetButton = new GButton(Labels.BTN_RESET);
			resetButton.addActionListener((e) -> {
				refComment = null;
				refCommentTextArea.setText("");
			});
		JScrollPane scrollText = new JScrollPane(refCommentTextArea);
		scrollText.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		re.add(scrollText,BorderLayout.CENTER);
		re.add(resetButton,BorderLayout.EAST);
		return re;
	}
	
	private void makeOthers() {
		removeAll();
		JScrollPane scrCommentList = new JScrollPane(forumEntriesPanelMaker());
		JScrollPane scrNewComment = new JScrollPane(txtpnCommentInput);
		txtpnCommentInput.setLineWrap(true);
		
		lblTitle.setFont(Settings.FONT_TITLE);
		lblTitle.setText(Settings.forum_currentTopic.getName());
		GLabel lblReferencedComment = new GLabel(Labels.LBL_REFERENCED_COMMENT);
		
		
		JPanel refCommentPanel = refCommentPanelMaker();
		
		GButton btnAddComment = new GButton(Labels.BTN_ADD_COMMENT);
			btnAddComment.addActionListener((e) -> {
				addComment();
			});		
		GButton btnBack = new GButton(Labels.BTN_BACK);
			btnBack.addActionListener((e) -> {
				root.setState(MainWindow.STATE_FORUM);
			});
		GButton resetRefComment = new GButton(Labels.BTN_BACK);
			resetRefComment.addActionListener((e) -> {
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
									.addComponent(refCommentPanel, GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE))
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
						.addComponent(refCommentPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
	
	private void addComment() {
		ForumEntry comment = new ForumEntry();
		comment.setAuthor(root.getLoggedUser().getUsername());
		comment.setText(txtpnCommentInput.getText());
		if(refComment != null) {
			comment.setRefComment(refComment.getCommentId());
		}
		comment.setCurrentTime();
		comment.setTopicId(Settings.forum_currentTopic.getTopicId());
		if(comment.getText().isEmpty()) {
			JOptionPane.showMessageDialog(
					this,
					Labels.MSG_EMPTY_COMMENT,
					Labels.MSG_ERROR,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(!root.controller.addForumEntry(comment)) {
			JOptionPane.showMessageDialog(
					this,
					Labels.MSG_ADDING_FORUM_ENTRY_FAILED,
					Labels.MSG_ERROR,
					JOptionPane.ERROR_MESSAGE);
			return;
		} else {
			txtpnCommentInput.setText("");
			refComment = null;
			refCommentTextArea.setText("");
			refresh();
		}
	}
	
	private JTextArea jTextAreaDefault(String text, boolean lineWrap) {
		JTextArea re = new JTextArea(text);
		re.setLineWrap(lineWrap);
		re.setEditable(false);
		re.setForeground(new Color(0,0,0));
		return re;
	}
	
}
