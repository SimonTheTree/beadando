package view.states;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

import view.Labels;
import view.MainWindow;
import view.Settings;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

public class Login extends JDialog {
	private JTextField txtfUserName;
	private JTextField txtfPassword;
	private JButton btnLogin;
	private JButton btnRegister;
	private JPanel panelMsg;
	private List<JLabel> msg = new ArrayList<>();
	private MainWindow root;

	/**
	 * Create the dialog.
	 */
	public Login(MainWindow r) {
		root = r;

		panelMsg = new JPanel();
		
		JLabel lblUsername = new JLabel(Labels.USERNAME);
		JLabel lblPassword = new JLabel(Labels.PASSWORD);
		JLabel lblNoAccountYet = new JLabel(Labels.LBL_NO_ACCOUNT_YET);
		
		txtfUserName = new JTextField();
			txtfUserName.setColumns(10);
		
		txtfPassword = new JTextField();
			txtfPassword.setColumns(10);
		
		btnLogin = new JButton(Labels.BTN_LOGIN);
			btnLogin.addActionListener((e) -> {
				msg.clear();
				
				String uname = txtfUserName.getText();
				String psw = txtfPassword.getText();
				boolean ok = true;
				if("".equals(uname)){
					msg.add(new JLabel(Labels.MSG_UNAME_FIELD_EMPTY));
					ok=false;
				}
				if("".equals(psw)){
					msg.add(new JLabel(Labels.MSG_PASSWD_FIELD_EMPTY));
					ok=false;
				}
				printMsg(); //prints the messages
				if(!ok){
					return;
				}
				if (root.controller.signIn(uname, psw)) {
					System.out.println("login!");
					root.setState(MainWindow.STATE_MAIN);
					this.setVisible(false);
					root.setVisible(true);
				} else {
					msg.add(new JLabel(Labels.MSG_LOGIN_FAILED));
					ok=false;
				}
			});
		btnRegister = new JButton(Labels.BTN_REGISTER);
			btnRegister.addActionListener((e) -> {
				root.setState(MainWindow.STATE_REGISTER);
				setVisible(false);
				root.setVisible(true);
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
			JLabel btn = msg.get(i);
			System.out.println(msg.get(i).getText());
			//tudom h nem szép, de úgy sincs átméretezés
			btn.setBounds(5, i*20+5, 300-10, 20); 
			panelMsg.add(btn);
		}
		panelMsg.repaint();
	}
}
