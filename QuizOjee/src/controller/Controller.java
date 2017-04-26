package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import model.Statistics;
import model.DAO;
import model.DAOImp;
import model.Question;
import model.RaceQuestion;
import model.User;
import model.exceptions.BadUsernameFormatException;
import model.exceptions.UserAlreadyExistsException;
import model.exceptions.UserNotFoundException;
import view.MainWindow;
public class Controller {
	
	
	private DAO db;
	private MainWindow gui;
	private int maxDifficulty;
	private int maxTopicId;
	private int actualMinDiff = -1;
	private int actualMaxDiff = -1;
	private List<Integer> actualTopicList = null;
	private List<Question> questions;
	private List<Integer> actualRaceTopicList = null;
	private List<RaceQuestion> raceQuestions;
	private Map<String,Integer> questionQuantityByCategory = null;
	private List<Statistics> topTenPlayers = null;
	private Map<String,Integer> userQuestionQuantity = null;
	
	public Controller() {
		db = new DAOImp();
		gui = MainWindow.getInstance(this);
		maxDifficulty = db.getMax("difficulty");
		System.out.println(maxDifficulty);
		maxTopicId = db.getMax("TOPIC_ID");
		System.out.println("maxDifficulty "+maxDifficulty);
		setThemNummThread();
		
	}
	
	/**
	 * Bejelentkezik (user-pw check).<br>
	 * <b>FONTOS:</b><br>
	 * Kodolja a jelszot! tehat kodolatlanul add meg!
	 * @param uname : felhasznalonev
	 * @param uncodedPassword : kodolatlan jelszo
	 * @return true=OK
	 */
	public boolean signIn(String uname, String codedPassword) {
		return db.checkUser(uname,codedPassword);
	}
	/**
	 * Letrehoz egy  {@link User}-t a user tablaban.<br>
	 * Kodolja a jelszot! tehat nem kotelezo megtenned.
	 * @param u : A letrehozando {@link User}.
	 * @throws UserAlreadyExistsException ha mar letezik.
	 * @throws BadUsernameFormatException ha nem jo a felhasznalonev formatuma. 
	 * @return sikerult-e.
	 */
	public boolean register(User u) throws UserAlreadyExistsException, BadUsernameFormatException {
		if(u.getUsername().contains(GameMessage.SPLIT) || u.getUsername().contains("@")) throw new BadUsernameFormatException();
		User codedUser = new User(u);
		codedUser.codePassword();
    	return db.addUser(codedUser);
    }
    
//    /**
//     * Felulirja a  {@link User} jelszavat
//     * @param uname
//    * @param pw
//     * @return false ha vmi nem mukodott
//     */
//szerintem ezt is bizzuk a modifyUser-re
//    public boolean setPassword(String uname, String pw){
//    	return false;
//    }
    
    /**
     * A parameterben kapott  {@link User} adatait frissiti az adatbazisban.
     * A user username-jet ki kell tolteni. ami nincs beallitva set[valami]-vel 
     * a {@link User}-ben, azt nem piszkalja az adatbazisban, az marad, ami volt.<br>
     * A jelszo-t kodolja! Tehat nem kell megtenned.
     * @param u
     * @return sikerult-e.
     * @throws UserNotFoundException
     */
    public boolean modifyUser(User u) throws UserNotFoundException {
    	User codedUser = new User(u);
    	return db.modifyUser(codedUser);
    }
    /**
     * uname azonositoja user  {@link User} objektumaval ter vissza, 
     * mely tartalmazza a user osszes adatat.<br>
     * <b>FONTOS:</b><br>
     * kodolt jelszoval ter vissza.
     * @param uname
     * @return A user-t vagy null.
     */
    public User getUser(String uname) {
    	User re = db.getUser(uname); 
    	return re;
    }
    /**
     * Ha logikus :D
     * @param uname.
     * @return sikerult-e.
     */
    public boolean deleteUser(String uname) {
    	return db.deleteUser(uname);
    }
    
    /**
     * Highscore-t konstrual. A  {@link User} objektumokban 
     * van username, realname, points
     * @param compare itt adjuk meg a rendezesi logikat
     * @param limit hany fos(xd) statisztikat szeretnenk
     * @return
     */
//    List<Statistics> getHighScore(Comparable<Statistics> compare, int limit){
//    	return null;
//    }
//    
//    public Statistics getUserStatistics(String uname){
//    	return null;
//    }
//    
//    public Statistics getAverageStatistics(){
//    	return null;
//    }
    
    public Statistics getUserStatistics(User u) {
    	return getUserStatistics(u.getUsername());
    }
    
    public Statistics getUserStatistics(String username) {
    	return db.getUserStatistics(username);
    }
    
	public boolean updateStatistics(Statistics stat) throws UserNotFoundException {
		return db.updateStatistics(stat);
	}
    
    /**
     * Atlagos {@link Statistics}-t keszit az ageMax es 
     * ageMin evesek kozott (ageMax-t, ageMin-t beszamitva)
     * @param ageMin
     * @param ageMax
     * @return teljesen kitoltott {@link Statistics} objektum {@link Statistics#avg avg} nevvel vagy null.
     */
    public Statistics getAgeStatistics(int ageMin, int ageMax) {
    	return Statistics.createAvgStatistics(db.getAgeStatistics(ageMin,ageMax));
    }
    
    /**
     * Visszaad teljesen kitoltott {@link Question}-t megadott nehezseggel es topic-kal <br>
     * - Ha a parameterek nem voltak megfelelok, akkor null-t.<br>
     * - Ha az adott kategoriaban (diff & topic) nincs kerdes, akkor is null-t.
     * @param minDiff a legkisebb nehezsegi szint, beleertve. (0-nal kisebb nem szamit)
     * @param maxDiff a legnagyobb nehezsegi szint, beleerve. ({@link #getMaxDifficulty()}-nal nagyobb nem szamit.)
     * @param topicList - a topic id-k listaja. Ha ures vagy null, akkor nincs megkotes.
     * @param n mennyi kerdest toltson be. 
     * @return {@link Question} or null
     */
    public Question getQuestion(int minDiff, int maxDiff, List<Integer> topicList, int n) {
    	if(minDiff >  maxDiff) return null;
    	if(maxDiff < 0) return null;
    	if(minDiff < 0) minDiff = 0;
    	if(maxDiff > maxDifficulty) maxDiff = maxDifficulty;
    	//---------Ha min es max is megegyezik------------------ES-----------(--------Ha nem nullok akkor a topicListek megegyeznek--------------VAGY--------------mindketto null---------)-------ES--------A jelenlegi nehezsegek nem -1 ek---
		if(minDiff == actualMinDiff && maxDiff == actualMaxDiff && ((topicList != null && actualTopicList != null && topicList.equals(actualTopicList)) || (topicList == null && actualTopicList == null)) && actualMinDiff != -1 && actualMaxDiff != -1) {
			System.out.println("load question from memory");
			int random;
			if(questions.size() == 1) {
				random = 0;
				actualMinDiff = -1;
				actualMaxDiff = -1;
				actualTopicList = null;
			} else {
				random = new Random().nextInt(questions.size());
			}
			Question re = questions.get(random);
			questions.remove(random);
			return re;
		}
		
		questions = db.getQuestions(minDiff, maxDiff, topicList, n);
		if(questions == null) {
			actualMinDiff = -1;
			actualMaxDiff = -1;
			actualTopicList = null;
			return null;
		} else {
			actualMinDiff = minDiff;
			actualMaxDiff = maxDiff;
			actualTopicList = topicList==null?null:new ArrayList<Integer>(topicList);
		}
		
		return getQuestion(minDiff, maxDiff, actualTopicList, n);
    }

    public RaceQuestion getRaceQuestion(List<Integer> topicList, int n) {
    	//---------Ha min es max is megegyezik------------------ES-----------(--------Ha nem nullok akkor a topicListek megegyeznek--------------VAGY--------------mindketto null---------)-------ES--------A jelenlegi nehezsegek nem -1 ek---
		if(raceQuestions != null && ((topicList != null && actualRaceTopicList != null && topicList.equals(actualTopicList)) || (topicList == null && actualTopicList == null))) {
			System.out.println("load raceQuestion from memory");
			int random;
			if(raceQuestions.size() == 1) {
				random = 0;
				actualRaceTopicList = null;
			} else {
				random = new Random().nextInt(raceQuestions.size());
			}
			RaceQuestion re = raceQuestions.get(random);
			raceQuestions.remove(random);
			return re;
		}
		
		raceQuestions = db.getRaceQuestions(topicList, n);
		if(raceQuestions == null) {
			actualRaceTopicList = null;
			return null;
		} else {
			actualRaceTopicList = topicList==null?null:new ArrayList<Integer>(topicList);
		}
		
		return getRaceQuestion(actualTopicList, n);
    }

    
    public int getMaxDifficulty() {
    	return maxDifficulty;
    }
    
    public int getMaxTopicId() {
    	return maxTopicId;
    }

	private void setThemNummThread() {
		Thread t = new Thread(() -> {
			while(true) {
				try {
					Thread.sleep(5000*60);
				} catch (Exception e) {
					e.printStackTrace();
				}
				questionQuantityByCategory = null;
				topTenPlayers = null;
				userQuestionQuantity = null;
			}
		});
		t.setDaemon(true);
		t.start();
	}
	
    /** 
     * 1. lekerdezes<p>
     * @param category : Milyen kategoriaban.
     * @return Hany kerdes van. (normal+race)
     */
    public int getQuestionQuantityByCategory(String category) {
    	if(questionQuantityByCategory == null) {
    		questionQuantityByCategory = db.getQuestionQuantityByCategory();
    	}
    	return questionQuantityByCategory.get(category)==null?0:questionQuantityByCategory.get(category);
    }
    
    /** 
     * 2. lekerdezes<p>
     * @return Top 10 jatekos statisztikaja.
     */
    public List<Statistics> getTopTenPlayersStatistics() {
    	if(topTenPlayers == null) {
    		topTenPlayers = db.getTopTenPlayersStatistics();
    	}
    	return topTenPlayers;
    }
    
    /** 
     * 3. lekerdezes.<p>
     * @param uname : Melyik jatekos.
     * @return Hany kerdessel jarult hozza a jatekhoz. (normal+race)
     */
    public int getUserQuestionQuantity(String uname) {
    	if(userQuestionQuantity == null) {
    		userQuestionQuantity = db.getUserQuestionQuantity();
    	}
    	return userQuestionQuantity.get(uname)==null?0:userQuestionQuantity.get(uname);    	
    }

}
