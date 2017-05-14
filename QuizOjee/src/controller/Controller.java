package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.Document;

import game.GameBoard;
import game.GameSettings;
import model.Statistics;
import model.Topic;
import model.DAO;
import model.DAOImp;
import model.ForumEntry;
import model.ForumTopic;
import model.Question;
import model.RaceQuestion;
import model.User;
import model.exceptions.BadUsernameFormatException;
import model.exceptions.UserAlreadyExistsException;
import model.exceptions.UserNotFoundException;
import view.Labels;
import view.MainWindow;
import view.Refreshable;
public class Controller implements Refreshable {
	
	
	private DAO db;
	private MainWindow gui;
	private int maxDifficulty;
	private int maxTopicId;
	private int actualMinDiff = -1;
	private int actualMaxDiff = -1;
	//private Object actualTopicListKey = new Object();
	private List<Integer> actualTopicList = null;
	private Object questionsKey = new Object();
	private List<Question> questions;
	//private Object actualRaceTopicListKey = new Object();
	private List<Integer> actualRaceTopicList = null;
	private Object raceQuestionsKey = new Object();
	private List<RaceQuestion> raceQuestions;
	private Object topicListKey = new Object();
	private List<Topic> topicList = null;
	private Object fullTopicListKey = new Object();
	private List<Topic> fullTopicList = null;
	private Object forumTopicsKey = new Object();
	private List<ForumTopic> forumTopics = null;
	
	//lekerdezesekhez
	private Object questionQuantityByCategoryKey = new Object();
	private Map<String,Integer> questionQuantityByCategory = null;
	private Object topTenPlayersKey = new Object();
	private List<Statistics> topTenPlayers = null;
	private Object userQuestionQuantityKey = new Object();
	private Map<String,Integer> userQuestionQuantity = null;
	private Object topFiveMapsKey = new Object();
	private Map<String, Integer> topFiveMaps = null;
	private Object userQuestionsKey = new Object();
	private String userQuestionsUname = null;
	private List<String[]> userQuestions = null;
	private Object gameWinnersKey= new Object();
	private List<String[]> gameWinners = null;
	private Object winnersKey = new Object();
	private String winnersMap = null;
	private List<String> winners = null;
	private Object favMapsKey = new Object();
	private String favMapsUname = null;
	private Map<String,Integer> favMaps = null;
	
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
		Statistics added = getUserStatistics(stat.getUname());
		if(added == null) throw new UserNotFoundException();
		added.add(stat);
		return db.updateStatistics(added);
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
		synchronized(questionsKey) {
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
    }

    public RaceQuestion getRaceQuestion(List<Integer> topicList, int n) {
		synchronized(raceQuestionsKey) {
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
    }

    public List<Topic> getTopicsWithQuestionNumbers() {
		synchronized(fullTopicListKey) {
	    	if(fullTopicList == null)
	    	fullTopicList = db.getTopicsWithQuestionNumbers();
	    	return fullTopicList;
		}
    }
    
    public List<Topic> getTopics() {
		synchronized(topicListKey) {
	    	if(topicList == null)
	    	topicList = db.getTopics();
	    	return topicList;
		}
    }
    
    public List<String[]> getTopicsTable() {
    	List<Topic> topList = getTopics();
    	List<String[]> re = convertTopic(topList);
    	if(re == null) re = new ArrayList<>();
    	String[] head = topList.get(0).sequence();
    	re.add(0, head);
    	return re;
    }
    
/*
    public List<String[]> getTopicsTable() {
		synchronized(topicListKey) {
	    	if(topicList == null)
	    	topicList = db.getTopics();
	    	return topicList;
		}
    }
*/    
    public int getNumOfQuestions(int minDiff, int maxDiff, List<Integer> topicIdList) {
		synchronized(fullTopicListKey) {
			if(fullTopicList == null) fullTopicList = db.getTopics();
	    	int re = 0;
	    	if(minDiff < 0) minDiff = 0;
	    	if(maxDiff < minDiff) return 0;
	    	if(maxDiff > maxDifficulty) maxDiff = maxDifficulty;
	    	for(int i : topicIdList) {
	    		Topic good = fullTopicList.get(i);
	    		if(good.getTopicId() != i) {
	    			for(int j=0;j<fullTopicList.size();++j) {
	    				if(fullTopicList.get(j).getTopicId() == i) good = fullTopicList.get(j);  
	    			}
	    		}
				re+=good.getNumberOfQuestions(minDiff, maxDiff);
	    	}
	    	return re;
    	}
    }
    
    public int getNumOfRaceQuestions(List<Integer> topicIdList) {
		synchronized(fullTopicListKey) {
	    	if(fullTopicList == null) fullTopicList = db.getTopics();
	    	int re = 0;
	    	for(int i : topicIdList) {
	    		Topic good = fullTopicList.get(i);
	    		if(good.getTopicId() != i) {
	    			for(int j=0;j<fullTopicList.size();++j) {
	    				if(fullTopicList.get(j).getTopicId() == i) good = fullTopicList.get(j);  
	    			}
	    		}
				re+=good.getNumberOfRaceQuestions();
	    	}
	    	return re;
		}
    }
    
    public int getMaxDifficulty() {
    	return maxDifficulty;
    }
    
    public int getMaxTopicId() {
    	return maxTopicId;
    }

    /** 
     * Beallit mindent nullra idonkent.
     */
	private void setThemNummThread() {
		Thread t = new Thread(() ->  {
			while(true) {
				try {
					Thread.sleep(5000*60);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				refresh();
			}
		});
		t.setDaemon(true);
		t.start();
	}
	
	public void refresh() {
		actualTopicList = null;
		actualRaceTopicList = null;
		
		synchronized(questionsKey) {
			questions = null;
		} synchronized(raceQuestionsKey) {
			raceQuestions = null;
		} synchronized(fullTopicListKey) {
			fullTopicList = null;
		} synchronized(topicListKey) {
			topicList = null;
		} synchronized(forumTopicsKey) {
			forumTopics = null;
		}
		
		//lekerdezesekhez
		synchronized(questionQuantityByCategoryKey) {
			questionQuantityByCategory = null;
		} synchronized(topTenPlayersKey) {
			topTenPlayers = null;
		} synchronized(userQuestionQuantityKey) {
			userQuestionQuantity = null;
		} synchronized(topFiveMapsKey) {
			topFiveMaps = null;
		} synchronized(userQuestionsKey) {
			userQuestionsUname = null;
			userQuestions = null;
		} synchronized(gameWinnersKey) {
			gameWinners = null;
		} synchronized(winnersKey) {
			winnersMap = null;
			winners = null;
		} synchronized(favMapsKey) {
			favMapsUname = null;
			favMaps = null;
		}
	}
	
	public List<String[]> getQuestions() {
		return db.getQuestions();
	}
	
	public List<String[]> getQuestionsTable() {
    	List<String[]> re = getQuestions();
    	if(re == null) re = new ArrayList<>();
    	String[] head = {Labels.M_QUESTION,Labels.M_RIGHT_ANSWER,Labels.M_ANSWER1,Labels.M_ANSWER2,Labels.M_ANSWER3,Labels.M_TOPIC_NAME, Labels.M_DIFFICULTY,Labels.M_AUTHOR, };
    	re.add(0, head);
    	return re;
	}
	
	public List<String> getMapNames() {
		return db.getMapNames();
	}
	
	public List<String[]> getMapNamesTable() {
    	List<String[]> re = convert(getMapNames());
    	if(re == null) re = new ArrayList<>();
    	String[] head = {Labels.M_MAP_NAME, };
    	re.add(0, head);
    	return re;
    }
	
	public boolean addQuestion(Question question) {
		return db.addQuestion(question);
	}
	//TODO befejezni
	public List<ForumEntry> getSomeForumEntries(ForumTopic forum_currentTopic, int minNum, int maxNum) {
		return db.getForumEntries(forum_currentTopic,minNum,maxNum);
	}
	
	public int getForumEntriesCount(ForumTopic forumTopic) {
		return db.getForumEntriesCount(forumTopic);
	}
	
	public List<ForumTopic> getForumTopics() {
		synchronized(forumTopicsKey) {
			if(forumTopics == null) {
				System.out.println("Read ForumTopics from DB");
				forumTopics = db.getForumTopics();
			} else {
				System.out.println("Read ForumTopics from Memory");
			}
			return forumTopics;
		}
	}
	
	public boolean addForumEntry(ForumEntry forumEntry) {
		return db.addForumEntry(forumEntry);
	}
	
	public boolean addForumTopic(ForumTopic forumTopic) {
		return db.addForumTopic(forumTopic);
	}
	
	public List<GameBoard> getMaps() {
		List<GameBoard> re = new ArrayList<>();
		List<Document> docs = db.getMapXMLs();
		for(Document doc : docs) {
			GameBoard map = new GameBoard(doc,GameSettings.getInstance().layout);
			re.add(map);
		}
		return re;
	}
	
	public boolean addMap(GameBoard map) {
		return db.addMapXML(map.getName(),map.toXMLString());
	}
	
	//TODO lekerdezesek

    /** 
     * 1. lekerdezes<p>
     * @param category : Milyen kategoriaban.
     * @return Hany kerdes van. (normal+race) vagy null
     */
    public Map<String,Integer> getQuestionQuantityByCategory() {
    	synchronized(questionQuantityByCategoryKey) {
	    	if(questionQuantityByCategory == null) {
	    		questionQuantityByCategory = db.getQuestionQuantityByCategory();
	    	}
	    	return questionQuantityByCategory;
    	}
    }
    /** 
     * 1. lekerdezes<p>
     * @return Hany kerdes van. (normal+race). <br>
     * Elso sor a title. 
     */
    public List<String[]> getQuestionQuantityByCategoryTable() {
    	List<String[]> re = convert(getQuestionQuantityByCategory());
    	if(re == null) re = new ArrayList<>();
    	String[] head = {Labels.M_TOPIC_NAME, Labels.TBL_QUESTIONS_NUMBER, };
    	re.add(0, head);
    	return re;
    }
    
    /** 
     * 2. lekerdezes<p>
     * @return Top 10 jatekos statisztikaja. vagy null
     */
    public List<Statistics> getTopTenPlayersStatistics() {
    	synchronized(topTenPlayersKey) {
	    	if(topTenPlayers == null) {
	    		topTenPlayers = db.getTopTenPlayersStatistics();
	    	}
	    	return topTenPlayers;
    	}
    }
    
    /** 
     * 2. lekerdezes<p>
     * @return Top 10 jatekos statisztikaja.<br>
     * Elso sor a title.
     */
    public List<String[]> getTopTenPlayersStatisticsTable() {
    	List<String[]> re = convertStatistics(getTopTenPlayersStatistics());
    	if(re == null) re = new ArrayList<>();
    	re.add(0, Statistics.getSequence());
    	return re;
    }
    
    
    /** 
     * 3. lekerdezes.<p>
     * @param uname : Melyik jatekos.
     * @return Hany kerdessel jarult hozza a jatekhoz. (normal+race) vagy null
     */
    public Map<String,Integer> getUserQuestionQuantity() {
    	synchronized(userQuestionQuantityKey) {
	    	if(userQuestionQuantity == null) {
	    		userQuestionQuantity = db.getUserQuestionQuantity();
	    	}
	    	return userQuestionQuantity;
    	}
    }
    
    /** 
     * 3. lekerdezes<p>
     * @param uname : Melyik jatekos.
     * @return Hany kerdessel jarult hozza a jatekhoz. (normal+race). <br>
     * Elso sor a title.
     */
    public List<String[]> getUserQuestionQuantityTable() {
    	List<String[]> re = convert(getUserQuestionQuantity());
    	if(re == null) re = new ArrayList<>();
    	String[] head = {Labels.USERNAME, Labels.TBL_QUESTIONS_NUMBER, };
    	re.add(0, head);
    	return re;
    }

    /** 
     *4. lekerdezes.<p>
     *@return Az 5 leggyakrabban hasznalt map nevet. 
     */
	public Map<String, Integer> getTopFiveMaps() {
    	synchronized(topFiveMapsKey) {
			if(topFiveMaps == null) {
				topFiveMaps = db.getTopFiveMaps();
			}
			return topFiveMaps;
    	}
	}
	
    /** 
     * 4. lekerdezes<p>
     *@return Az 5 leggyakrabban hasznalt map nevet. <br>
     * Elso sor a title
     */
    public List<String[]> getTopFiveMapsTable() {
    	List<String[]> re = convert(getTopFiveMaps());
    	if(re == null) re = new ArrayList<>();
    	String[] head = {Labels.M_MAP_NAME, Labels.TBL_POPULARITY, };
    	re.add(0, head);
    	return re;
    }
	
	/** 
	 *5. lekerdezes.<p>
	 *@return A user altal felrakott kerdesek topicnevvel kiirva. vagy null
	 */
	public List<String[]> getUserQuestions(String uname) {
    	synchronized(userQuestionsKey) {
			if(userQuestions == null) {
				userQuestionsUname = uname;
				userQuestions = db.getUserQuestions(uname);
			} else if(userQuestionsUname != null && !userQuestionsUname.equals(uname)) {
				userQuestionsUname = uname;
				userQuestions = db.getUserQuestions(uname);
			}
			return userQuestions;
    	}
	}
	
    /** 
     * 5. lekerdezes<p>
	 *@return A user altal felrakott kerdesek topicnevvel kiirva. vagy null
     * Elso sor a title
     */
    public List<String[]> getUserQuestionsTable(String uname) {
    	List<String[]> re = getUserQuestions(uname);
    	if(re == null) re = new ArrayList<>();
    	String[] head = {Labels.M_QUESTION,Labels.M_RIGHT_ANSWER,Labels.M_TOPIC_NAME,};
    	re.add(0, head);
    	return re;
    }
	
	/** 
	 * 6. lekerdezes.<p>
	 * @return kilistazza a befejezett jatekok nyerteseit, a nevuket, a nyero pontszamot, es a csatateret (terkep) vagy null
	 */
	public List<String[]> getGameWinners() {
    	synchronized(gameWinnersKey) {
			if(gameWinners == null) {
				gameWinners = db.getGameWinners();
			}
			return gameWinners;
    	}
	}
	
    /** 
     * 6. lekerdezes<p>
	 * @return kilistazza a befejezett jatekok nyerteseit, a nevuket, a nyero pontszamot, es a csatateret (terkep)
     * Elso sor a title
     */
    public List<String[]> getGameWinnersTable() {
    	List<String[]> re = getGameWinners();
    	if(re == null) re = new ArrayList<>();
    	String[] head = {Labels.USERNAME, Labels.M_REAL_NAME, Labels.TBL_WINNER_SCORE, Labels.M_MAP_NAME};
    	re.add(0, head);
    	return re;
    }
	
	/** 
	 * 7. lekerdezes.<p>
	 * @return az adott mapon tortent jatekok nyerteseit es pontszamat (mapnev alapjan!) vagy null
	 */
	public List<String> getWinners(String map) {
    	synchronized(winnersKey) {
	    	if(winners == null) {
				winnersMap = map;
				winners = db.getWinners(map);
			} else if(winnersMap != null && !winnersMap.equals(map)) {
				winnersMap = map;
				winners = db.getWinners(map);
			}
			return winners;
    	}
	}
	
	/**
	 *8. lekerdezes.<p>
	 *@return Az adott user kedvenc mapjait, es azt, hogy hanyszor volt rajtuk. vagy null
	 */
	public Map<String, Integer> getFavMaps(String uname) {
    	synchronized(favMapsKey) {
	    	if(favMaps == null) {
				favMapsUname = uname;
				favMaps = db.getFavMaps(uname);
			} else if(favMapsUname != null && !favMapsUname.equals(uname)) {
				favMapsUname = uname;
				favMaps = db.getFavMaps(uname);
			}
			return favMaps;
    	}
	}
	
	public List<String[]> getFavMapsTable(String uname) {
		List<String[]> re = convert(getFavMaps(uname));
    	if(re == null) re = new ArrayList<>();
    	String[] head = {Labels.M_MAP_NAME, Labels.TBL_POPULARITY, };
    	re.add(0, head);
    	return re;
	}
	
	//CONVERT
	private static List<String[]> convert(Map<String,Integer> in) {
		List<String[]> re = new ArrayList<>();
		for(String elem : in.keySet()) {
			String[] str = new String[2];
			str[0] = elem;
			str[1] = in.get(elem)+"";
			re.add(str);
		}
		return re;
	}
	
	private static List<String[]> convert(List<String> in) {
		List<String[]> re = new ArrayList<>();
		for(String elem : in) {
			String[] str = new String[1];
			str[0] = elem;
			re.add(str);
		}
		return re;
	}
	
	private static List<String[]> convertStatistics(List<Statistics> in) {
		List<String[]> re = new ArrayList<>();
		for(Statistics st : in) {
			re.add(st.convertToStringArray());
		}
		return re;
	}
	
	private static List<String[]> convertTopic(List<Topic> in) {
		List<String[]> re = new ArrayList<>();
		for(Topic tp : in) {
			re.add(tp.convertToStringArray());
		}
		return re;
	}
	
	
}
