package view.components;

import javax.swing.JDialog;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import view.Labels;
import view.MainWindow;
import view.Settings;

public class DialogQuestion extends JDialog {	
	public DialogQuestion() {
		
		setSize(750, 500);
		

		lblQuestionText = new GLabel();
			lblQuestionText.setFont(Settings.FONT_QUESTION);
			lblQuestionText.setBackground(new Color(0, 0, 0, 230));
			lblQuestionText.setOpaque(true);
	
		panel = new JPanel();
		panel.setBackground(new Color(0, 0, 0, 150));
	
		btnAnswerA = new GButton();
		btnAnswerB = new GButton();
		btnAnswerC = new GButton();
		btnAnswerD = new GButton();
	
		initLayout();
		setVisible(false);
	}

	private void initLayout() {

		panel.setLayout(new GridLayout(4, 1, 5, 5));

		panel.add(btnAnswerA);
		panel.add(btnAnswerB);
		panel.add(btnAnswerC);
		panel.add(btnAnswerD);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(28)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblQuestionText, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE))
					.addContainerGap(35, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(30)
					.addComponent(lblQuestionText, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 286, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(35, Short.MAX_VALUE))
		);
		getContentPane().setLayout(groupLayout);
		
	}
	
	private JPanel panel;

	public GLabel lblQuestionText;

	public GButton btnAnswerA;
	public GButton btnAnswerB;
	public GButton btnAnswerC;
	public GButton btnAnswerD;
}
