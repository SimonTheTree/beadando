package view.states;

import view.Labels;
import view.MainWindow;
import view.Settings;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import view.components.GLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import view.components.GButton;
import javax.swing.JTextField;

public class ProfileState extends DefaultState {
	MainWindow root;
	private JTextField txtfUsername;
	private JTextField txtfRealName;
	public ProfileState(MainWindow r) {
		super(MainWindow.STATE_PROFILE, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
		
		GLabel lblTitle = new GLabel(Labels.LBL_TITLE_PROFILE);
			lblTitle.setFont(Settings.FONT_TITLE);
		GLabel lblUsername = new GLabel(Labels.LBL_UNAME);
		GLabel lblRealName = new GLabel(Labels.LBL_RNAME);
		GLabel lblAge = new GLabel(Labels.LBL_AGE);
		GLabel lblWins = new GLabel(Labels.LBL_N_WINS);
		GLabel lblDefeats = new GLabel(Labels.LBL_N_DEFEATS);
		GLabel lblPoints = new GLabel(Labels.LBL_POINTS);
		GLabel lblRightAns = new GLabel(Labels.LBL_N_RIGHT_ANS);
		GLabel lblWrongAns = new GLabel(Labels.LBL_N_WRONG_ANS);
		GLabel lblRightTips = new GLabel(Labels.LBL_N_RIGHT_TIPS);
		GLabel lblWrongTips = new GLabel(Labels.LBL_N_WRONG_TIPS);
		
		GLabel lblDataAge = new GLabel("?");
		GLabel lblDataPoints = new GLabel("?");
		GLabel lblDataWins = new GLabel("?");
		GLabel lblDataDefeats = new GLabel("?");
		GLabel lblDataRightAns = new GLabel("?");
		GLabel lblDataWrongAns = new GLabel("?");
		GLabel lblDataRightTips = new GLabel("?");
		GLabel lblDataWrongTips = new GLabel("?");
		
		GButton btnDeleteUser = new GButton(Labels.BTN_DELETE);
			btnDeleteUser.addActionListener((e) -> {
				int option = JOptionPane.showConfirmDialog(
						root,
						Labels.MSG_CONFIRM_DELETE_USER,
						Labels.MSG_TITLE_CONFIRM,
				        JOptionPane.OK_CANCEL_OPTION
				        );						
						
				if(option == 0){
					root.controller.deleteUser(root.getLoggedUser().getUsername());
					root.setUser(null);
					root.setState(MainWindow.STATE_LOGIN);
				}
			});
		GButton btnAddQuestions = new GButton(Labels.BTN_ADD_QUESTION);
			btnAddQuestions.addActionListener((e) -> {
				
			});
		GButton btnListMyQuestion = new GButton(Labels.BTN_LIST_MY_QUESTIONS);
			btnListMyQuestion.addActionListener((e) -> {
				
			});
		GButton btnBack = new GButton(Labels.BTN_BACK);
			btnBack.addActionListener((e) -> {
				root.setState(MainWindow.STATE_MAIN);
			});
		
		txtfUsername = new JTextField();
		txtfUsername.setEnabled(false);
		txtfUsername.setEditable(false);
		txtfUsername.setColumns(10);
		
		txtfRealName = new JTextField(root.getLoggedUser().getRealName());
		txtfRealName.setColumns(10);
		
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(64)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblRealName, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblAge, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblPoints, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblWins, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblDefeats, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblRightAns, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblWrongAns, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblRightTips, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblWrongTips, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblUsername, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
								.addComponent(txtfUsername, GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
								.addComponent(txtfRealName)))
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
						.addComponent(txtfUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblRealName)
						.addComponent(txtfRealName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
	protected void onStart() {
		txtfUsername.setText(root.getLoggedUser().getUsername());
		txtfRealName.setText(root.getLoggedUser().getRealName());
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}
}
