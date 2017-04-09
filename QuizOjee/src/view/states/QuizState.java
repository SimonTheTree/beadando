package view.states;

import gameTools.state.State;
import view.Labels;
import view.MainWindow;
import view.Settings;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPanel;
import java.awt.GridLayout;

public class QuizState extends State {
	MainWindow root;
	
	public QuizState(MainWindow r) {
		super(MainWindow.STATE_QUIZ, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
		
		JLabel lblSubTitle = new JLabel("Question 1 of 20");
			lblSubTitle.setFont(Settings.FONT_SUB_TITLE);
		JLabel lblQuestionText = new JLabel("Hány csillag van az égen?");
		
		JPanel panel = new JPanel();
		
		JLabel lblRightAns = new JLabel("Right answers: ");
		
		JLabel lblWrongAns = new JLabel("Wrong answers: ");
		
		
		JLabel lblWrongN = new JLabel("0");
		
		JLabel lblRightN = new JLabel("0");
		
		JButton btnAnswerA = new JButton("A Válasz");
		JButton btnAnswerB = new JButton("B Válasz");
		JButton btnAnswerC = new JButton("C Válasz");
		JButton btnAnswerD = new JButton("D Válasz");
		JButton btnNext = new JButton(Labels.BTN_NEXT);
		JButton btnQuit = new JButton(Labels.BTN_QUIT);
			btnQuit.addActionListener((e) -> {
				root.setState(MainWindow.STATE_MAIN);
			});
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(52)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(lblQuestionText, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
								.addComponent(lblSubTitle, Alignment.LEADING))
							.addContainerGap(72, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblRightAns)
								.addComponent(lblWrongAns))
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblWrongN)
								.addComponent(lblRightN))
							.addPreferredGap(ComponentPlacement.RELATED, 220, Short.MAX_VALUE)
							.addComponent(btnQuit, GroupLayout.PREFERRED_SIZE, 193, GroupLayout.PREFERRED_SIZE)
							.addGap(13)
							.addComponent(btnNext, GroupLayout.PREFERRED_SIZE, 193, GroupLayout.PREFERRED_SIZE)
							.addGap(66))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 786, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(37)
					.addComponent(lblSubTitle)
					.addGap(18)
					.addComponent(lblQuestionText, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
					.addGap(31)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 211, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, groupLayout.createParallelGroup(Alignment.TRAILING, false)
							.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
									.addComponent(lblRightAns)
									.addComponent(lblRightN))
								.addGap(18)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
									.addComponent(lblWrongAns)
									.addComponent(lblWrongN)))
							.addComponent(btnNext, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(btnQuit, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE))
					.addGap(61))
		);
		panel.setLayout(new GridLayout(2, 2, 5, 5));
		
		panel.add(btnAnswerA);
		panel.add(btnAnswerB);
		panel.add(btnAnswerC);
		panel.add(btnAnswerD);
				
		setLayout(groupLayout);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
