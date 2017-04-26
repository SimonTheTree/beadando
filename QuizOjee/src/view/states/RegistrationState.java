package view.states;

import gameTools.state.State;
import model.User;
import controller.PasswordCoder;
import model.exceptions.BadUsernameFormatException;
import view.Labels;
import view.MainWindow;
import view.Settings;
import view.components.GLabel;

import java.awt.Color;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import view.components.GButton;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JPasswordField;

public class RegistrationState extends DefaultState {
	MainWindow root;
	private JTextField txtUname;
	private JPasswordField txtPw;
	private JSpinner txtAge;
	private JPasswordField txtPwAgain;
	private List<GLabel> msg = new ArrayList<>();
	private JPanel panelMsg;
	public RegistrationState(MainWindow r) {
		super(MainWindow.STATE_REGISTER, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;
		
		panelMsg = new JPanel();
		
		GLabel lblRegisterTitle = new GLabel(Labels.LBL_TITLE_REGISTER);
			lblRegisterTitle.setFont(Settings.FONT_TITLE);
		GLabel lblUsername = new GLabel(Labels.USERNAME);
		GLabel lblPassword = new GLabel(Labels.PASSWORD);
		GLabel lblAge = new GLabel(Labels.LBL_AGE);
		GLabel lblPasswordAgain = new GLabel(Labels.PASSWORD_AGAIN);
		
		txtUname = new JTextField();
			txtUname.setColumns(10);
		
		txtPw = new JPasswordField();
			txtPw.setColumns(10);
		
		txtAge = new JSpinner();
		
		txtPwAgain = new JPasswordField();
			txtPwAgain.setColumns(10);
			
		GButton btnRegister = new GButton(Labels.BTN_OK);
			btnRegister.addActionListener((e) -> {
				msg.clear();
				String uname = txtUname.getText();                         
				String password = PasswordCoder.cryptWithMD5(String.valueOf(txtPw.getPassword()));     
				String password2 = PasswordCoder.cryptWithMD5(String.valueOf(txtPwAgain.getPassword()));     	           
				int age = (Integer) txtAge.getValue();
				
				boolean ok = true;
				if("".equals(uname)){
					msg.add(new GLabel(Labels.MSG_UNAME_FIELD_EMPTY));
					ok=false;
				} else if(root.controller.getUser(uname) != null) {
					msg.add(new GLabel(Labels.MSG_USER_EXISTS));
					ok=false;
				}
				if(txtPw.getPassword().length == 0){
					msg.add(new GLabel(Labels.MSG_PASSWD_FIELD_EMPTY));
					ok=false;
				}
				if(txtPwAgain.getPassword().length == 0){
					msg.add(new GLabel(Labels.MSG_PASSWD2_FIELD_EMPTY));
					ok=false;
				}
				if(age <= 12){
					msg.add(new GLabel(Labels.MSG_YOURE_TOO_YOUNG));
					ok=false;
				}
				//finally check passwords match
				if( (txtPw.getPassword().length > 0) && (txtPwAgain.getPassword().length > 0) ){
					if(!password.equals(password2)){						
						msg.add(new GLabel(Labels.MSG_PASSWORDS_DONT_MATCH));
						ok = false;
					}
				}
				if(!ok){
					msg.add(new GLabel(Labels.MSG_REGISTRATION_FAILED));
				}
				if(ok){
					try{
						User u = new User();
						u.setUsername(uname);
						u.setCodedPassword(password);
						u.setAge(age);
						ok = root.controller.register(u);
						System.out.println(ok);
					}catch(BadUsernameFormatException ex){
						msg.add(new GLabel(Labels.MSG_BAD_USERNAME_FORMAT));
						ok = false;
					}
				}
				if(!ok){
					msg.add(new GLabel(Labels.MSG_SERVER_ERROR));
					msg.add(new GLabel(Labels.MSG_REGISTRATION_FAILED));
				}
				printMsg();
				if(ok){
					root.setState(MainWindow.STATE_LOGIN);
				}
			});
		
		GButton btnCancel = new GButton(Labels.BTN_CANCEL);
			btnCancel.addActionListener((e) -> {
				root.setState(MainWindow.STATE_LOGIN);
			});
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(139)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblRegisterTitle, GroupLayout.PREFERRED_SIZE, 156, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(17)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(btnRegister, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(lblPassword, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(lblUsername, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
										.addComponent(lblPasswordAgain)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(lblAge)
											.addGap(101)))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(txtAge, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE)
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
											.addComponent(txtPwAgain)
											.addComponent(txtPw)
											.addComponent(txtUname, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE))))
								.addComponent(btnCancel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addGap(18)
							.addComponent(panelMsg, GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(94)
					.addComponent(lblRegisterTitle, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addGap(37)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(panelMsg, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblUsername)
									.addGap(18)
									.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblPassword)
										.addComponent(txtPw, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addGap(18)
									.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblPasswordAgain)
										.addComponent(txtPwAgain, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addGap(18)
									.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblAge)
										.addComponent(txtAge, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
								.addComponent(txtUname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(20)
							.addComponent(btnRegister, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(198, Short.MAX_VALUE))
		);
		panelMsg.setLayout(null);
		setLayout(groupLayout);
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}
	
	private void printMsg(){
		panelMsg.removeAll();
		for(int i = 0; i < msg.size(); i++){
			GLabel btn = msg.get(i);
			System.out.println(msg.get(i).getText());
			//tudom h nem szép, de úgy sincs átméretezés
			btn.setBounds(5, i*20+5, 300-10, 20); 
			btn.setForeground(Color.RED);
			btn.setFont(Settings.FONT_ERROR_MSG);
			panelMsg.add(btn);
		}
		panelMsg.repaint();
	}

}
