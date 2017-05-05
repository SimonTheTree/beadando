package view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import model.ForumTopic;
import model.Question;
import model.Topic;
import view.Labels;
import view.MainWindow;
import view.Refreshable;

public class AddForumTopicDialog extends JDialog implements ActionListener {

	private MainWindow root;
	private static final long serialVersionUID = 1L;
	private JTextField nameTextField = new JTextField();
	private GButton okButton = new GButton(Labels.BTN_OK);
	private GButton cancelButton = new GButton(Labels.BTN_CANCEL);
	private Refreshable ref;
	public static final Color BACKGROUND = Login.BACKGROUND;  
	
	
	public AddForumTopicDialog(MainWindow root, boolean modal, Refreshable ref) {
		super(root,modal);
		init(root,ref);
	}
	
	private void init(MainWindow root, Refreshable ref) {
		this.root = root;
		this.ref = ref;
		
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
		GridLayout layout = new GridLayout(1,2);
		layout.setHgap(10);
		layout.setVgap(2);
		re.setBackground(BACKGROUND);
		re.setLayout(layout);
		re.add(new GLabel(Labels.M_TOPIC_NAME));
		re.add(nameTextField);
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
			if(nameTextField.getText().isEmpty()) {
				JOptionPane.showMessageDialog(
					this,
					Labels.MSG_PLEASE_SET_EVERYTHING,
					Labels.MSG_ERROR,
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			ForumTopic topic = new ForumTopic();
			topic.setName(nameTextField.getText());
			if(!root.controller.addForumTopic(topic)) {
				JOptionPane.showMessageDialog(
						this,
						Labels.MSG_ADDING_QUESTION_FAILED,
						Labels.MSG_ERROR,
						JOptionPane.ERROR_MESSAGE);
				return;
			} else {
				ref.refresh();
				setVisible(false);
			}
		} else if(e.getSource().equals(cancelButton)) {
			setVisible(false);
		}
	}
	
}
