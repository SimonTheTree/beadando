package view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

import controller.PasswordCoder;
import model.User;
import resources.Resources;
import view.Labels;
import view.MainWindow;
import view.Settings;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPasswordField;

public class Login extends JDialog {
	private JTextField txtfUserName;
	private JPasswordField txtfPassword;
	private GButton btnLogin;
	private GButton btnRegister;
	private JPanel panelMsg;
	private List<GLabel> msg = new ArrayList<>();

	/**
	 * Create the dialog.
	 */
	public Login() {
		panelMsg = new JPanel();
		panelMsg.setOpaque(false);
		getContentPane().setBackground(new Color(80, 40, 0));
		
		GLabel lblUsername = new GLabel(Labels.USERNAME);
		GLabel lblPassword = new GLabel(Labels.PASSWORD);
		GLabel lblNoAccountYet = new GLabel(Labels.LBL_NO_ACCOUNT_YET);
		
		txtfUserName = new JTextField();
			txtfUserName.setColumns(10);
			txtfUserName.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER){
						btnLogin.doClick();
					}
				}
			});
		
		txtfPassword = new JPasswordField();
			txtfPassword.setColumns(10);
			txtfPassword.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER){
						btnLogin.doClick();
					}
				}
			});
		
		btnLogin = new GButton(Labels.BTN_LOGIN);
			btnLogin.addActionListener((e) -> {
				msg.clear();
				
				String uname = txtfUserName.getText();
				String psw = PasswordCoder.cryptWithMD5(String.valueOf(txtfPassword.getPassword()));     
				boolean ok = true;
				if("".equals(uname)){
					msg.add(new GLabel(Labels.MSG_UNAME_FIELD_EMPTY));
					ok=false;
				}
				if(txtfPassword.getPassword().length == 0){
					msg.add(new GLabel(Labels.MSG_PASSWD_FIELD_EMPTY));
					ok=false;
				}
				printMsg(); //prints the messages
				if(!ok){
					return;
				}
				if (MainWindow.getInstance().controller.signIn(uname, psw)) {
					System.out.println("login!");
					MainWindow.getInstance().setState(MainWindow.STATE_MAIN);
					this.setVisible(false);
					MainWindow.getInstance().setUser(MainWindow.getInstance().controller.getUser(uname));
					MainWindow.getInstance().setVisible(true);
				} else {
					msg.add(new GLabel(Labels.MSG_LOGIN_FAILED));
					ok=false;
				}
				printMsg();
			});
		btnRegister = new GButton(Labels.BTN_REGISTER);
			btnRegister.addActionListener((e) -> {
				MainWindow.getInstance().setState(MainWindow.STATE_REGISTER);
				setVisible(false);
				MainWindow.getInstance().setVisible(true);
			});
		
		
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblUsername)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(txtfUserName, GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblPassword)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(txtfPassword, GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)))
							.addContainerGap())
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnLogin, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
							.addGap(71))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnRegister, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
							.addGap(73))))
				.addComponent(panelMsg, GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNoAccountYet)
					.addContainerGap(94, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUsername)
						.addComponent(txtfUserName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPassword)
						.addComponent(txtfPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnLogin)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNoAccountYet)
					.addGap(12)
					.addComponent(btnRegister)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelMsg, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
		);
		panelMsg.setLayout(null);
		getContentPane().setLayout(groupLayout);
		setSize(300,300);
		setLocationRelativeTo(null);
		setTitle(Labels.LOGIN_WINDOW_TITLE);
	}
	
	private void printMsg(){
		panelMsg.removeAll();
		for(int i = 0; i < msg.size(); i++){
			GLabel label = msg.get(i);
			System.out.println(msg.get(i).getText());
			//tudom h nem szep, de ugy sincs atmeretezes
			label.setBounds(5, i*20+5, 300-10, 20); 
			label.setForeground(Color.RED);
			label.setFont(Settings.FONT_ERROR_MSG);
			panelMsg.add(label);
		}
		panelMsg.repaint();
	}
	
}
