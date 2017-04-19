package controller;

import java.util.List;
import java.util.Random;
import model.Statistics;
import model.DAO;
import model.DAOImp;
import model.Question;
import model.User;
import model.exceptions.UserAlreadyExistsException;
import view.MainWindow;
public class Controller {
	
	
	private DAO db = new DAOImp();
	private MainWindow gui = new MainWindow(this);;
	private int maxDifficulty;
	private int maxTopicId;
	private int actualDiff = -1;
	private int actualTopic = -1;
	private List<Question> questions;
	
	public Controller() {
		maxDifficulty = db.getMax("difficulty");
		maxTopicId = db.getMax("TOPIC_ID");
	}
	
	/**
	 * Bejelentkezik (user-pw check)
	 * @param uname
	 * @param pw
	 * @return true=OK
	 */
	public boolean signIn(String uname, String pw){
		return true;
	}
	/**
	 * Letrehoz egy  {@link User}-t a user tablaban, ha 
	 * lehetseges, ha nem akkor hiba van
	 * @param u
	 * @param pw
	 * @throws UserAlreadyExistsException if the username is taken 
	 * @return
	 */
	public boolean register(User u) throws UserAlreadyExistsException{
    	return false;
    }
    
    /**
     * Felulirja a  {@link User} jelszavat
     * @param uname
     * @param pw
     * @return false ha vmi nem mukodott
     */
	//szerintem ezt is bízzuk a modifyUser-re
//    public boolean setPassword(String uname, String pw){
//    	return false;
//    }
    
    /**
     * A parameterben kapott  {@link User} adatait frissiti az adatbazisban.
     * A user username-jet ki kell tolteni. ami nincs beállítva set[valami]-vel 
     * a {@link User}-ben, azt nem piszkálja az adatbázisban, az marad, ami volt
     * @param u
     * @return
     */
    public boolean modifyUser(User u){
    	return false;
    }
    /**
     * uname azonositoja user  {@link User} objektumaval ter vissza, 
     * mely tartalmazza a user osszes adatat
     * @param uname
     * @return
     */
    public User getUser(String uname){
    	return null;
    }
    /**
     * Há logikus :D
     * @param uname
     * @return
     */
    public boolean deleteUser(String uname){
    	return false;
    }
    
    /**
     * Highscore-t konstrual. A  {@link User} objektumokban 
     * van username, realname, points  
     * @param compare itt adjuk meg a rendezesi logikat
     * @param limit hány fős statisztikát szeretnénk
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
    /**
     * Atlagos {@link Statistics}-t keszit az ageMax es 
     * ageMin evesek kozott (ageMax-t, ageMin-t beszamitva)
     * @param ageMax
     * @param ageMin
     * @return teljesen kitoltott {@link Statistics} objektum.
     */
    public Statistics getAgeStatistics(int ageMax, int ageMin){
    	return null;
    }
    
    /**
     * random kérdés adott topikkal
     * @param topicID
     * @return
     */
    public Question getRandomQuestionOfTopic(int topicID){
    	return null;
    }
    
    /**
     * random kérdés adott nehézségi szinttel
     * @param topicID
     * @return
     */
    public Question getRandomQuestionOfDifficulity(int topicID){
    	return null;
    }
    
    /**
     * Visszaad teljesen kitoltott {@link Question}-t megadott nehezseggel es topic-kal <br>
     * - Ha a parameterek nem voltak megfelelok, akkor null-t.<br>
     * - Ha az adott kategoriaban (diff & topic) nincs kerdes, akkor is null-t.
     * @param diff - nehezsegi fokozat
     * @param topic - az adott topic id-ja.
     * @return {@link Question} or null
     */
    public Question getQuestion(int diff, int topic) {
		if(diff == actualDiff && topic == actualTopic && actualTopic != -1 && actualDiff != -1) {
			System.out.println("load question from memory");
			int random;
			if(questions.size() == 1) {
				random = 0;
				actualDiff = -1;
				actualTopic = -1;
			} else {
				random = new Random().nextInt(questions.size());
			}
			Question re = questions.get(random);
			questions.remove(random);
			return re;
		}
		
		questions = db.getQuestions(diff, topic);
		if(questions == null) {
			actualDiff = -1;
			actualTopic = -1;
			return null;
		} else {
			actualDiff = diff;
			actualTopic = topic;
		}
		
		return getQuestion(diff,topic);
    }

    public Question getQuestion(int topic){
    	return null;
    }
}
