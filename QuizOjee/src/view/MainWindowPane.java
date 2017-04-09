package view;

import javax.swing.JPanel;
import javax.swing.JButton;

public class MainWindowPane  extends JPanel{
	public MainWindowPane() {
		
		JButton button = new JButton("Game room");
		add(button);
		
		JButton button_1 = new JButton("Exit");
		add(button_1);
		
		JButton button_2 = new JButton("Start Quiz");
		add(button_2);
	}

}
