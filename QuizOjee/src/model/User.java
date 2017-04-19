package model;

/** 
 * Ez az alap objektum, felhasznalok tarolasara valo.
 * */
public class User {

	private String username; //userID
	private String pw;
	private int age;
	
    public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}

}
