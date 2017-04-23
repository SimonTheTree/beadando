package view.states;

import gameTools.state.State;
import model.Question;
import view.Labels;
import view.MainWindow;
import view.Settings;
import view.components.GLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import view.components.GButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class QuizState extends DefaultState {
	MainWindow root;
	private List<Question> questions;
	private String[] currentAnswers;
	private int currentQuestionIndex;
	private int rightN, wrongN; 

	public QuizState(MainWindow r) {
		super(MainWindow.STATE_QUIZ, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;

		lblSubTitle = new GLabel();
			lblSubTitle.setFont(Settings.FONT_TITLE);
		lblQuestionText = new GLabel();
			lblQuestionText.setFont(Settings.FONT_QUESTION);
			lblQuestionText.setBackground(new Color(0, 0, 0, 230));
			lblQuestionText.setOpaque(true);

		panel = new JPanel();
		panel.setBackground(new Color(0, 0, 0, 150));
		
		
		lblRightAns = new GLabel(Labels.LBL_USER_N_RIGHT_ANS);
		lblWrongAns = new GLabel(Labels.LBL_USER_N_WRONG_ANS);
		lblWrongN = new GLabel();
		lblRightN = new GLabel();

		btnAnswerA = new GButton();
			btnAnswerA.addActionListener((e) -> {
				checkAnswer((GButton)e.getSource());
			});
		btnAnswerB = new GButton();
			btnAnswerB.addActionListener((e) -> {
				checkAnswer((GButton)e.getSource());
			});
		btnAnswerC = new GButton();
			btnAnswerC.addActionListener((e) -> {
				checkAnswer((GButton)e.getSource());
			});	
		btnAnswerD = new GButton();
			btnAnswerD.addActionListener((e) -> {
				checkAnswer((GButton)e.getSource());
			});
		btnQuit = new GButton(Labels.BTN_QUIT);
			btnQuit.addActionListener((e) -> {
				root.setState(MainWindow.STATE_MAIN);
			});

		initLayout();

	}

	private void nextQuestion(){
		Random rand = new Random();
		String[] q = new String[4];
		Question quest = questions.get(currentQuestionIndex);
		
		//fill the array qith the answers
		q[0] = quest.getRightAnswer();
		q[1] = quest.getAnswer1();
		q[2] = quest.getAnswer2();
		q[3] = quest.getAnswer3();
		
		//shuffle the array (Fisher-Yates shuffle) 
		for(int i = q.length-1; i>0; i--){
			int index = rand.nextInt(i+1);
			//swap
			String s = q[i];
			q[i] = q[index];
			q[index] = s;
		}
		
		//display the answers
		btnAnswerA.setText(q[0]);
		btnAnswerB.setText(q[1]);
		btnAnswerC.setText(q[2]);
		btnAnswerD.setText(q[3]);
		
		//display the question
		lblQuestionText.setText("<html><body style='margin:10px;'>" +quest.getQuestion());
		
		//update question counter
		String questCount = Labels.LBL_QUIZ_QUESTION_INDEX;
		questCount = questCount.replaceFirst("@", String.valueOf(currentQuestionIndex+1));
		questCount = questCount.replaceFirst("@", String.valueOf(Settings.quiz_numOfQuestions));
		lblSubTitle.setText(questCount);

	}
	
	private void checkAnswer(GButton self){
		Question quest = questions.get(currentQuestionIndex);
		//if text on clicked button == right answer text
		if( quest.getRightAnswer().equals(self.getText())){
			rightN++;
			lblRightN.setText(String.valueOf(rightN));
		} else {
			wrongN++;
			lblWrongN.setText(String.valueOf(wrongN));
		}
		
		//load the next answer, if any
		if(++currentQuestionIndex < Settings.quiz_numOfQuestions){
			nextQuestion();
		} else {
			System.out.println("THATS IT!!");
		}
	}
	
	@Override
	protected void onStart(){
		questions = new ArrayList<>();
		int diff = Settings.quiz_difficulity;
		for(int i = 0; i<Settings.quiz_numOfQuestions; i++){
			questions.add(root.controller.getQuestion(diff*5,diff*5+5, null, Settings.quiz_numOfQuestions));
		}

		currentQuestionIndex = 0;
		rightN = wrongN = 0;
		lblRightN.setText(String.valueOf(rightN));
		lblWrongN.setText(String.valueOf(wrongN));
		nextQuestion();
	}
	
	@Override
	public void update() {

	}

	private void initLayout() {
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
				.createSequentialGroup().addGap(52)
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout
								.createSequentialGroup().addGroup(groupLayout
										.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(lblQuestionText, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 776,
												Short.MAX_VALUE)
										.addComponent(lblSubTitle, Alignment.LEADING))
								.addContainerGap(72, Short.MAX_VALUE))
						.addGroup(
								groupLayout.createSequentialGroup()
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
												.addComponent(lblRightAns).addComponent(lblWrongAns))
										.addGap(18)
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
												.addComponent(lblWrongN).addComponent(lblRightN))
										.addPreferredGap(ComponentPlacement.RELATED, 446, Short.MAX_VALUE)
										.addComponent(btnQuit, GroupLayout.PREFERRED_SIZE, 193,
												GroupLayout.PREFERRED_SIZE)
										.addGap(62))
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(panel, GroupLayout.PREFERRED_SIZE, 786, GroupLayout.PREFERRED_SIZE)
								.addContainerGap()))));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addGap(37).addComponent(lblSubTitle).addGap(18)
						.addComponent(lblQuestionText, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
						.addGap(31).addComponent(panel, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED, 204, Short.MAX_VALUE)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(Alignment.TRAILING,
										groupLayout.createSequentialGroup()
												.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
														.addComponent(lblRightAns).addComponent(lblRightN))
												.addGap(18)
												.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
														.addComponent(lblWrongAns).addComponent(lblWrongN))
												.addGap(61))
								.addGroup(Alignment.TRAILING,
										groupLayout
												.createSequentialGroup().addComponent(btnQuit,
														GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
												.addGap(65)))));
		panel.setLayout(new GridLayout(2, 2, 5, 5));

		panel.add(btnAnswerA);
		panel.add(btnAnswerB);
		panel.add(btnAnswerC);
		panel.add(btnAnswerD);
		setLayout(groupLayout);
	}
	
	JPanel panel;

	GLabel lblSubTitle;
	GLabel lblQuestionText;
	GLabel lblRightAns;
	GLabel lblWrongAns;
	GLabel lblWrongN;
	GLabel lblRightN;

	GButton btnAnswerA;
	GButton btnAnswerB;
	GButton btnAnswerC;
	GButton btnAnswerD;
	GButton btnQuit;

}
