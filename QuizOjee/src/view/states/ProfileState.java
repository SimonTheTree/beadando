package view.states;

import gameTools.state.State;
import view.Labels;
import view.MainWindow;
import view.Settings;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JTextField;

public class ProfileState extends State {
	MainWindow root;
	private JTextField textField;
	private JTextField textField_1;
	public ProfileState(MainWindow r) {
		super(MainWindow.STATE_PROFILE, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
		
		JLabel lblTitle = new JLabel(Labels.LBL_TITLE_PROFILE);
			lblTitle.setFont(Settings.FONT_TITLE);
		JLabel lblUsername = new JLabel(Labels.LBL_USER_UNAME);
		JLabel lblRealName = new JLabel(Labels.LBL_USER_RNAME);
		JLabel lblAge = new JLabel(Labels.LBL_USER_AGE);
		JLabel lblWins = new JLabel(Labels.LBL_USER_N_WINS);
		JLabel lblDefeats = new JLabel(Labels.LBL_USER_N_DEFEATS);
		JLabel lblPoints = new JLabel(Labels.LBL_USER_POINTS);
		JLabel lblRightAns = new JLabel(Labels.LBL_USER_N_RIGHT_ANS);
		JLabel lblWrongAns = new JLabel(Labels.LBL_USER_N_WRONG_ANS);
		JLabel lblRightTips = new JLabel(Labels.LBL_USER_N_RIGHT_TIPS);
		JLabel lblWrongTips = new JLabel(Labels.LBL_USER_N_WRONG_TIPS);
		
		JLabel lblDataPoints = new JLabel("?");
		JLabel lblDataWins = new JLabel("?");
		JLabel lblDataDefeats = new JLabel("?");
		JLabel lblDataRightAns = new JLabel("?");
		JLabel lblDataWrongAns = new JLabel("?");
		JLabel lblDataRightTips = new JLabel("?");
		JLabel lblDataWrongTips = new JLabel("?");
		
		JButton btnDeleteUser = new JButton(Labels.BTN_DELETE);
		JButton btnAddQuestions = new JButton(Labels.BTN_ADD_QUESTION);
		JButton btnListMyQuestion = new JButton(Labels.BTN_LIST_MY_QUESTIONS);
		JButton btnBack = new JButton(Labels.BTN_BACK);
			btnBack.addActionListener((e) -> {
				root.setState(MainWindow.STATE_MAIN);
			});
		
		textField = new JTextField();
		textField.setEnabled(false);
		textField.setEditable(false);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		
		JLabel lblDataAge = new JLabel("?");
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(64)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblWins)
								.addComponent(lblRightAns)
								.addComponent(lblWrongAns)
								.addComponent(lblRightTips)
								.addComponent(lblWrongTips)
								.addComponent(lblPoints)
								.addComponent(lblUsername)
								.addComponent(lblRealName, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblDefeats)
								.addComponent(lblAge))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblDataAge)
								.addComponent(lblDataPoints)
								.addComponent(lblDataWrongTips)
								.addComponent(lblDataRightTips)
								.addComponent(lblDataWrongAns)
								.addComponent(lblDataRightAns)
								.addComponent(lblDataDefeats)
								.addComponent(lblDataWins)
								.addComponent(textField, GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
								.addComponent(textField_1)))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(36)
									.addComponent(lblTitle))
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(67)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(btnAddQuestions, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE)
										.addComponent(btnListMyQuestion, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE))))
							.addGap(396)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(btnBack, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnDeleteUser, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE))))
					.addGap(41))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(33)
					.addComponent(lblTitle)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUsername)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblRealName)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAge)
						.addComponent(lblDataAge))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPoints)
						.addComponent(lblDataPoints))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblWins)
						.addComponent(lblDataWins))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDataDefeats)
						.addComponent(lblDefeats))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblRightAns)
						.addComponent(lblDataRightAns))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblWrongAns)
						.addComponent(lblDataWrongAns))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblRightTips)
						.addComponent(lblDataRightTips))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblWrongTips)
						.addComponent(lblDataWrongTips))
					.addPreferredGap(ComponentPlacement.RELATED, 138, Short.MAX_VALUE)
					.addComponent(btnListMyQuestion, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnAddQuestions, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
					.addGap(29))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(39)
					.addComponent(btnDeleteUser, GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
					.addGap(455)
					.addComponent(btnBack, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
					.addGap(26))
		);
		setLayout(groupLayout);
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}
}
