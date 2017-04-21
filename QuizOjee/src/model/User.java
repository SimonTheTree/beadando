package model;

import controller.PasswordCoder;

/** 
 * <b>User</b><br>
 * Egy felhasznalo tarolasara valo JavaBean.<p>
 * <b>Adattagjai:</b><br>
 * - username<br>
 * - password<br>
 * - realName<br>
 * - age<br>
 */
public class User {

	private String username;
	private String password;
	private String realName;
	private int age;
	private boolean passwordCoded;
	
	public User() {}
	public User(User u) {
		username = u.username;
		password = u.password;
		realName = u.realName;
		age = u.age;
		passwordCoded = u.passwordCoded;
	}

	public void codePassword() {
		if(passwordCoded == true) return; 
		passwordCoded = true;
		password = PasswordCoder.cryptWithMD5(password);
	}
    public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	/** Csak kodolt jelszot lehet lekerni.
	 * @return jelszo vagy null-ha kodolatlan*/
	public String getPassword() {
		if(!passwordCoded) return null;
		return password;
	}
	public void setUncodedPassword(String password) {
		passwordCoded = false;
		this.password = password;
	}
	public void setCodedPassword(String password) {
		passwordCoded = true;
		this.password = password;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}

	public String toString() {
		return "[("+username+") "+password+" "+realName+" "+age+"]";
	}
	
	public boolean equals(Object object) {
		if(!(object instanceof User)) return false;
		User user = (User) object;
		if(user.username.equals(username)) return false;
		if(user.password.equals(password)) return false;
		if(user.realName.equals(realName)) return false;
		if(user.age != age) return false;
		return true;
	}
	
}
