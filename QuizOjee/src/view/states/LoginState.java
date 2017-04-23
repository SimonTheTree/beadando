package view.states;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import gameTools.state.State;
import view.MainWindow;
import view.Settings;

public class LoginState extends State {
	MainWindow root;
	Login loginDialog;
	
	public LoginState(MainWindow r) {
		super(MainWindow.STATE_LOGIN, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
		loginDialog = new Login(root);
		loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		loginDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                root.dispatchEvent(new WindowEvent(root, WindowEvent.WINDOW_CLOSING));
            }
        });
	}
	
	@Override
	protected void onStart() {
		root.setVisible(false);			
		loginDialog.setVisible(true);
	}
	
	@Override
	public void update() {
	}

}
