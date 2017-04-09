package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
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

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Login dialog = new Login();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Login() {
		setResizable(false);
		setTitle(Labels.LOGIN_WINDOW_TITLE);
		setLocationRelativeTo(null);
		setSize(300, 168);
		
		JLabel lblUsername = new JLabel(Labels.USERNAME);
		JLabel lblPassword = new JLabel(Labels.PASSWORD);
		
		txtfUserName = new JTextField();
		txtfUserName.setColumns(10);
		
		txtfPassword = new JTextField();
		txtfPassword.setColumns(10);
		
		btnLogin = new JButton(Labels.BTN_LOGIN);
		btnRegister = new JButton(Labels.BTN_REGISTER);
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblUsername)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(txtfUserName, GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblPassword)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(txtfPassword, GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(56)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(btnRegister, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnLogin, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE))))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
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
					.addComponent(btnRegister)
					.addContainerGap(28, Short.MAX_VALUE))
		);
		getContentPane().setLayout(groupLayout);
	}
}
