package model;

/** 
 * Egy {@link User} vagy egy csoport atlag statisztikajanak tarolasara valo objektum.
 * */
public class Statistics {
	private String uname;
	private int points;
    private int wins;
    private int defeats;
    private int rightAnswers;
    private int wrongAnswers;
    private int rightTips;
    private int wrongTips;
    
    public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDefeats() {
        return defeats;
    }

    public void setDefeats(int defeats) {
        this.defeats = defeats;
    }

    public int getRightAnswers() {
        return rightAnswers;
    }

    public void setRightAnswers(int rightAnswers) {
        this.rightAnswers = rightAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public int getRightTips() {
        return rightTips;
    }

    public void setRightTips(int rightTips) {
        this.rightTips = rightTips;
    }

    public int getWrongTips() {
        return wrongTips;
    }

    public void setWrongTips(int wrongTips) {
        this.wrongTips = wrongTips;
    }
    

}
