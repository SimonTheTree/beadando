package model;

import java.util.List;

/**
 * <b>Statistics</b><br> 
 * Egy {@link User} vagy egy csoport atlag statisztikajanak tarolasara valo objektum.<p>
 * 
 * <b>Adattagjai:</b><br>
 * - uname<br>
 * - points<br>
 * - wins<br>
 * - defeats<br>
 * - rightAnswers<br>
 * - wrongAnswers<br>
 * - rightTips<br>
 * - wrongTips<br>
 */
public class Statistics {
	private String uname;
	private int points;
    private int wins;
    private int defeats;
    private int rightAnswers;
    private int wrongAnswers;
    private int rightTips;
    private int wrongTips;
    
    /**Az atlagos {@link Statistics} megkulonbozteto egyedi {@link #uname}-je */
    public static final String avg = "@avg@";
    
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
    
    public static Statistics createAvgStatistics(List<Statistics> stats) {
    	if(stats == null) return null;
    	Statistics re = new Statistics();
    	re.setUname(avg);
    	for(int i=0;i<stats.size();++i) {
	    	Statistics stat = stats.get(i);
			re.points += stat.points;
	    	re.wins += stat.wins;
	    	re.defeats += stat.defeats;
	    	re.rightAnswers += stat.rightAnswers;
	    	re.wrongAnswers += stat.wrongAnswers;
	    	re.rightTips += stat.rightTips;
	    	re.wrongTips += stat.wrongTips;
    	}
		re.points /= stats.size();
    	re.wins /= stats.size();
    	re.defeats /= stats.size();
    	re.rightAnswers /= stats.size();
    	re.wrongAnswers /= stats.size();
    	re.rightTips /= stats.size();
    	re.wrongTips /= stats.size();
    	return re;
    }
    
    public String toString() {
    	return "[("+uname+") "+points+" "+wins+" "+defeats+" "+rightAnswers+" "+wrongAnswers+" "+rightTips+" "+wrongTips+"]";
    }
}
