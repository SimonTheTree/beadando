package view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import model.RaceQuestion;
import view.Labels;
import view.Settings;
import javax.swing.JTextField;
import javax.swing.JButton;

public class DialogRaceQuestion extends JDialog {
	public DialogRaceQuestion() {
		this(null, false);
	}

	public DialogRaceQuestion(JFrame root) {
		this(root, false);
	}

	public DialogRaceQuestion(JFrame root, boolean modality) {
		super(root, modality);
		
		initGui();
		initLayout();
		setVisible(false);
		
	}
	
	private void initGui() {
		setSize(750, 300);
		
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

		txtfAnswer = new JTextField();
			txtfAnswer.setColumns(10);
			txtfAnswer.setBackground(new Color(0x000088));
			txtfAnswer.setCaretColor(Color.WHITE);
			txtfAnswer.setFont(Settings.FONT_BUTTON_MAIN);
			txtfAnswer.setBorder(null);
			txtfAnswer.setForeground(Color.WHITE);			
			txtfAnswer.setOpaque(true);		
			//biztositjuk, hogy csak szamjegyeket, es egy tizedespontot/vesszot lehessen beirni
			((AbstractDocument) txtfAnswer.getDocument()).setDocumentFilter(new DocumentFilter() {
				// Useful for every kind of input validation !
			    // this is the insert pattern
			    // The pattern must contain all subpatterns so we can enter characters into a text component !
			    private Pattern pattern = Pattern.compile("\\d*[,\\.]?\\d*");
			    
			    @Override
			    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
			            throws BadLocationException {

			        String newStr = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
			        Matcher m = pattern.matcher(newStr);
			        if (m.matches()) {
			            super.insertString(fb, offset, string, attr);
			        } else {
			        }
			    }
			    
			    @Override
			    public void replace(FilterBypass fb, int offset,
			                        int length, String string, AttributeSet attr) throws
			            BadLocationException {

			        if (length > 0) fb.remove(offset, length);
			        insertString(fb, offset, string, attr);
			    }
			});
			//enternek hatasara nyomodjon le a gomb
			txtfAnswer.addActionListener((e) -> {
				btnGo.doClick();
			});
		btnGo = new GButton(Labels.LBL_MEHET);
			btnGo.setUI(new GButtonUI(
				new Color(0x111111).brighter(), 
				new Color(0x111111).brighter().brighter(), 
				new Color(0x111111), 
				Settings.FONT_SUB_TITLE, 
				Color.WHITE
			));
			
		MainPanel = new JPanel();
		answersPanel = new JPanel();
			answersPanel.setBackground(Settings.color_lightGray2);
			
		setUndecorated(true);
	}

	private void initLayout() {
		getContentPane().setLayout(new BorderLayout(0, 0));

		answersPanel.setLayout(new GridLayout(4, 1, 5, 5));

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
					.addGroup(gl_MainPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(answersPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 648, Short.MAX_VALUE)
						.addComponent(lblQuestionText, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 648, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_MainPanel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtfAnswer, GroupLayout.PREFERRED_SIZE, 506, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnGo, GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)))
					.addContainerGap(60, Short.MAX_VALUE))
		);
		gl_MainPanel.setVerticalGroup(
			gl_MainPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_MainPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblQuestionText, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_MainPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtfAnswer, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnGo, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(answersPanel, GroupLayout.DEFAULT_SIZE, 18, Short.MAX_VALUE)
					.addContainerGap())
		);
		MainPanel.setLayout(gl_MainPanel);

	}
	
	/**
	 * Extracts whatever is written into the answer textfield, replacing any ',' with '.'
	 * @return String
	 */
	public String getAnswer() {
		return txtfAnswer.getText().replace(',', '.');
	}
	/**
	 * Clears whatever is written into the answer textfield.
	 */
	public void clearAnswer() {
		txtfAnswer.setText("");
	}
	/**
	 * disables/enables the input textfield and the button
	 * @param bool true -> enabled false -> ha erted... 
	 */
	public void setInputEnabled(boolean bool) {
		txtfAnswer.setEditable(bool);
		btnGo.setEnabled(bool);
	}

	public void focus() {
		txtfAnswer.requestFocus();
	}
	public JPanel answersPanel;
	private JPanel MainPanel;
	private JPanel panelNorth;
	private JPanel panelSouth;

	public GLabel lblNorth;
	public GLabel lblSouth;
	public GLabel lblQuestionText;
	private JTextField txtfAnswer;
	public GButton btnGo;
	
	public static void main(String[] args) {
		//teszt this window
		JFrame frame = new JFrame();
		frame.setSize(400,400);
		frame.setVisible(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DialogRaceQuestion rqDialog = new DialogRaceQuestion(frame);
		rqDialog.setVisible(true);
		rqDialog.lblNorth.setText("player1 vs player2");
		rqDialog.lblSouth.setText("10s");
		do {
			try {
				rqDialog.txtfAnswer.setBackground(new Color(0x000044));
//				rqDialog.AnswersPanel.setOpaque(false);
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}while (true);
		
	}
}
