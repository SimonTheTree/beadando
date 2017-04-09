package view.states;

import gameTools.state.State;
import view.Labels;
import view.MainWindow;
import view.Settings;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JComboBox;
import javax.swing.JButton;

public class QuizCreatorState extends State {
	MainWindow root;
	public QuizCreatorState(MainWindow r) {
		super(MainWindow.STATE_QUIZ_SETTINGS, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
		
		JLabel lblTitle = new JLabel(Labels.LBL_TITLE_QUIZ_CREATOR);
			lblTitle.setFont(Settings.FONT_TITLE);
		JLabel lblDiff = new JLabel(Labels.LBL_DIFFICULITY);
		JLabel lblTopics = new JLabel(Labels.LBL_TOPICS);
		JLabel lblQuestionN = new JLabel(Labels.LBL_NUMBER_OF_QUESTIONS);

		JScrollPane scrollPane = new JScrollPane();
		
		JSpinner spinQuestionN = new JSpinner();
		
		String[] diffs = {"Easy", "Medium", "Hard"};
		JComboBox<String> cboxDifficulity = new JComboBox<String>(diffs);
		
		JButton btnStart = new JButton(Labels.BTN_START);
			btnStart.addActionListener((e) -> {
				root.setState(MainWindow.STATE_QUIZ);
			});
		JButton btnCancel = new JButton(Labels.BTN_CANCEL);
			btnCancel.addActionListener((e) -> {
				root.setState(MainWindow.STATE_MAIN);
			});
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(47)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblTitle)
							.addContainerGap(740, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblTopics)
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE))
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(18)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(btnStart, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(btnCancel, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)))
								.addGroup(groupLayout.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(lblDiff)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(cboxDifficulity, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(lblQuestionN)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(spinQuestionN, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)))))
							.addGap(55))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(32)
					.addComponent(lblTitle)
					.addGap(18)
					.addComponent(lblTopics)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblQuestionN)
								.addComponent(spinQuestionN, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblDiff)
								.addComponent(cboxDifficulity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED, 290, Short.MAX_VALUE)
							.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED))
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 443, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(60, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}


}