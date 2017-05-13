package model;

import java.io.Serializable;
import java.util.List;

import view.Labels;

/**
 * <b>Statistics</b><br> 
 * Egy {@link User} vagy egy csoport atlag statisztikajanak tarolasara valo objektum.<p>
 * 
 * <b>Adattagjai:</b><br>
 * - uname<br>
 * - age<br>
 * - points<br>
 * - wins<br>
 * - defeats<br>
 * - rightAnswers<br>
 * - wrongAnswers<br>
 * - rightTips<br>
 * - wrongTips<p>
 * 
 * <b>Ha resetelni akarsz, mindent allits be 0-ra!</b>
 */
public class Statistics  implements Serializable{
	private String uname;
    private int age = 0;
    private int points = -1;
    private int wins = -1;
    private int defeats = -1;
    private int rightAnswers = -1;
    private int wrongAnswers = -1;
    private int rightTips = -1;
    private int wrongTips = -1;
    
    /**Az atlagos {@link Statistics} megkulonbozteto egyedi {@link #uname}-je */
    public static final String avg = "@avg@";
    
    
    public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
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
	    	re.age += stat.age;
			re.points += stat.points;
	    	re.wins += stat.wins;
	    	re.defeats += stat.defeats;
	    	re.rightAnswers += stat.rightAnswers;
	    	re.wrongAnswers += stat.wrongAnswers;
	    	re.rightTips += stat.rightTips;
	    	re.wrongTips += stat.wrongTips;
    	}
		re.points /= stats.size();
		re.age /= stats.size();
    	re.wins /= stats.size();
    	re.defeats /= stats.size();
    	re.rightAnswers /= stats.size();
    	re.wrongAnswers /= stats.size();
    	re.rightTips /= stats.size();
    	re.wrongTips /= stats.size();
    	return re;
    }
    
    public String toString() {
    	return "[("+uname+") "+age+" "+points+" "+wins+" "+defeats+" "+rightAnswers+" "+wrongAnswers+" "+rightTips+" "+wrongTips+"]";
    }
    
    public String[] convertToStringArray() {
    	String[] re = new String[9];
    	int index = 0;
    	re[index++] = uname;
    	re[index++] = age + "";
    	re[index++] = points + "";
    	re[index++] = wins + "";
    	re[index++] = defeats + "";
    	re[index++] = rightAnswers + "";
    	re[index++] = wrongAnswers + "";
    	re[index++] = rightTips + "";
    	re[index++] = wrongTips + "";
    	return re;
    }
    
    public static String[] getSequence() {
    	return new String[] {
    		Labels.USERNAME,
    		Labels.M_AGE,
    		Labels.M_POINTS,
    		Labels.M_WINS,
    		Labels.M_DEFEATS,
    		Labels.M_RIGHT_ANS,
    		Labels.M_WRONG_ANS,
    		Labels.M_RIGHT_TIPS,
    		Labels.M_WRONG_TIPS,
    	};
    }
    
    public Statistics add(Statistics stat) {
		points += stat.points;
    	wins += stat.wins;
    	defeats += stat.defeats;
    	rightAnswers += stat.rightAnswers;
    	wrongAnswers += stat.wrongAnswers;
    	rightTips += stat.rightTips;
    	wrongTips += stat.wrongTips;
    	return this;
    }
    
}
