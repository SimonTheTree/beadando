package view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import model.Question;
import model.Topic;
import view.Labels;
import view.MainWindow;

public class AddQuestionDialog extends JDialog implements ActionListener {
	
	private MainWindow root;
	private static final long serialVersionUID = 1L;
	private JTextField questionTextField = new JTextField();
	private JTextField rightAnswerTextField = new JTextField();
	private JTextField answer1TextField = new JTextField();
	private JTextField answer2TextField = new JTextField();
	private JTextField answer3TextField = new JTextField();
    private JComboBox<String> topicNameCombo;
	private JSpinner difficultySpinner = new JSpinner();
	private List<JTextField> textFields = new ArrayList<>();
	private GButton okButton = new GButton(Labels.BTN_OK);
	private GButton cancelButton = new GButton(Labels.BTN_CANCEL);
	public static final Color BACKGROUND = Login.BACKGROUND;  
	
	public AddQuestionDialog(MainWindow root, boolean modal, String userName) {
		super(root,modal);
		init(root,userName);
	}
	
	private void init(MainWindow root, String userName) {
		this.root = root;
		List<Topic> topics = root.controller.getTopics();
		String[] boxItems = new String[topics.size()];
		for(int i=0;i<boxItems.length;++i) {
			boxItems[i] = topics.get(i).getName();
		}
		topicNameCombo = new JComboBox<String>(boxItems);
		
		this.setLayout(new BorderLayout());
		
		JPanel thingsPanel = thingsPanelMaker();
		JPanel buttonPanel = buttonPanelMaker();
		JPanel mainPanel = mainPanelMaker(thingsPanel, buttonPanel);
		
		this.setTitle(Labels.ADD_QUESTION_DIALOG_TITLE);
		this.add(mainPanel);
		this.pack();
		this.setLocationRelativeTo(root);
		this.setVisible(true);
	}

	private JPanel thingsPanelMaker() {
		JPanel re = new JPanel();
		GridLayout layout = new GridLayout(7,2);
		layout.setHgap(10);
		layout.setVgap(2);
		re.setBackground(BACKGROUND);
		re.setLayout(layout);
		re.add(new GLabel(Labels.M_QUESTION));
		re.add(questionTextField);
		textFields.add(questionTextField);
		re.add(new GLabel(Labels.M_RIGHT_ANSWER));
		re.add(rightAnswerTextField);
		textFields.add(rightAnswerTextField);
		re.add(new GLabel(Labels.M_ANSWER1));
		re.add(answer1TextField);
		textFields.add(answer1TextField);
		re.add(new GLabel(Labels.M_ANSWER2));
		re.add(answer2TextField);
		textFields.add(answer2TextField);
		re.add(new GLabel(Labels.M_ANSWER3));
		re.add(answer3TextField);
		textFields.add(answer3TextField);
		re.add(new GLabel(Labels.M_TOPIC_NAME));
		re.add(topicNameCombo);
		re.add(new GLabel(Labels.M_DIFFICULTY));
		re.add(difficultySpinner);
		return re;
	}

	private JPanel buttonPanelMaker() {
		JPanel re = new JPanel();
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		re.add(okButton);
		re.add(cancelButton);
		re.setBackground(BACKGROUND);
		return re;
	}

	private JPanel mainPanelMaker(JPanel thingsPanel, JPanel buttonPanel) {
		JPanel re = new JPanel();
		re.setLayout(new BorderLayout());
		re.add(thingsPanel, BorderLayout.CENTER);
		re.add(buttonPanel, BorderLayout.SOUTH);
		JPanel[] bounds = new JPanel[3];
		for(int i=0;i<bounds.length;++i) {
			bounds[i] = new JPanel();
			bounds[i].setSize(new Dimension(buttonPanel.getSize().height,buttonPanel.getSize().height));
			bounds[i].setBackground(BACKGROUND);
		}
		int index = 0;
		re.add(bounds[index++], BorderLayout.NORTH);
		re.add(bounds[index++], BorderLayout.WEST);
		re.add(bounds[index++], BorderLayout.EAST);
		re.setBackground(BACKGROUND);
		return re;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(okButton)) {
			boolean wasThereEmpty = false;
			for(JTextField textField : textFields) {
				if(textField.getText().isEmpty()) {
					wasThereEmpty = true;
					break;
				}
			}
			if(wasThereEmpty) {
				JOptionPane.showMessageDialog(
					this,
					Labels.MSG_PLEASE_SET_EVERYTHING,
					Labels.MSG_ERROR,
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(((Integer)difficultySpinner.getValue()) < 0) {
				JOptionPane.showMessageDialog(
						this,
						Labels.MSG_NEGATIVE_VALUE_GIVEN,
						Labels.MSG_ERROR,
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			Question question = new Question();
			question.setQuestion(questionTextField.getText());
			question.setRightAnswer(rightAnswerTextField.getText());
			question.setAnswer1(answer1TextField.getText());
			question.setAnswer2(answer2TextField.getText());
			question.setAnswer3(answer3TextField.getText());
			question.setTopicName((String)topicNameCombo.getSelectedItem());
			question.setDifficulty((Integer)difficultySpinner.getValue());
			question.setAuthor(root.getLoggedUser().getUsername());
			if(!root.controller.addQuestion(question)) {
				JOptionPane.showMessageDialog(
						this,
						Labels.MSG_ADDING_QUESTION_FAILED,
						Labels.MSG_ERROR,
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		} else if(e.getSource().equals(cancelButton)) {
			setVisible(false);
		}
	}
	
}
