package view.components;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.print.DocFlavor.STRING;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import view.Labels;
import view.MainWindow;
import view.Settings;
import java.awt.BorderLayout;
import javax.swing.JLabel;

public class DialogQuestion extends JDialog {
	public DialogQuestion() {
		this(null, false);
	}

	public DialogQuestion(JFrame root) {
		this(root,false);
	}
	
	public DialogQuestion(JFrame root, boolean modality) {
		super(root, modality);
		setSize(750, 500);
		
		panelNorth = new JPanel();
			lblNorth = new GLabel();
			lblNorth.setFont(Settings.FONT_TITLE);
			lblNorth.setBackground(Color.black);  
			lblNorth.setOpaque(true);             

		panelSouth = new JPanel();
			lblSouth = new GLabel();
			lblSouth.setFont(Settings.FONT_TITLE);
			lblSouth.setBackground(Color.black);  
			lblSouth.setOpaque(true);    

		lblQuestionText = new GLabel();
		lblQuestionText.setFont(Settings.FONT_QUESTION);
		lblQuestionText.setBackground(new Color(0, 0, 0, 230));
		lblQuestionText.setOpaque(true);
	
		MainPanel = new JPanel();
		AnswerPanel = new JPanel();
		AnswerPanel.setBackground(new Color(0, 0, 0, 150));

		btnAnswerA = new GButton();
		btnAnswerB = new GButton();
		btnAnswerC = new GButton();
		btnAnswerD = new GButton();
	
		initLayout();
		setVisible(false);
		if (!Settings.ENV.equals(Settings.ENV_KABINET)){
			setUndecorated(true);
		}
	}

	private void initLayout() {
		getContentPane().setLayout(new BorderLayout(0, 0));

		AnswerPanel.setLayout(new GridLayout(4, 1, 5, 5));

		AnswerPanel.add(btnAnswerA);
		AnswerPanel.add(btnAnswerB);
		AnswerPanel.add(btnAnswerC);
		AnswerPanel.add(btnAnswerD);

		getContentPane().add(MainPanel);
		
		panelNorth.setLayout(new GridLayout(1, 1));
		panelSouth.setLayout(new GridLayout(1, 1));
		panelSouth.add(lblSouth);
		panelNorth.add(lblNorth);
		getContentPane().add(panelNorth, BorderLayout.NORTH);
		getContentPane().add(panelSouth, BorderLayout.SOUTH);
		
		GroupLayout gl_MainPanel = new GroupLayout(MainPanel);
		gl_MainPanel.setHorizontalGroup(
			gl_MainPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_MainPanel.createSequentialGroup()
					.addGap(42)
					.addGroup(gl_MainPanel.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(AnswerPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblQuestionText, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 648, Short.MAX_VALUE))
					.addContainerGap(58, Short.MAX_VALUE))
		);
		gl_MainPanel.setVerticalGroup(
			gl_MainPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_MainPanel.createSequentialGroup()
					.addGap(31)
					.addComponent(lblQuestionText, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(AnswerPanel, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(42, Short.MAX_VALUE))
		);
		MainPanel.setLayout(gl_MainPanel);
		
		
	}
	
	public static void main(String[] args){
		//teszt this window
				JFrame frame = new JFrame();
				frame.setSize(400,400);
				frame.setVisible(false);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				DialogQuestion rqDialog = new DialogQuestion(frame);
				rqDialog.setVisible(true);
				rqDialog.lblNorth.setText("player1 vs player2");
				rqDialog.lblSouth.setText("10s");
				
	}
	
	private JPanel AnswerPanel;
	private JPanel MainPanel;
	private JPanel panelNorth;
	private JPanel panelSouth;

	public GLabel lblNorth;
	public GLabel lblSouth;
	public GLabel lblQuestionText;

	public GButton btnAnswerA;
	public GButton btnAnswerB;
	public GButton btnAnswerC;
	public GButton btnAnswerD;
}
