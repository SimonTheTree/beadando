package model;

import java.util.List;
import java.util.Map;

import model.exceptions.UserAlreadyExistsException;
import model.exceptions.UserNotFoundException;

/**
 * DAO = Data Access Object
 * @author ganter
 *
 */
public interface DAO {
	
    /**
     * Visszaad teljesen kitoltott {@link Question}-kel teli {@link List}-et a megadott nehezseggel es topic-kal <br>
     * - Ha a parameterek nem voltak megfelelok, akkor null-t.<br>
     * - Ha az adott kategoriaban (diff & topic) nincs kerdes, akkor is null-t.
     * @param diff - nehezsegi fokozat
     * @param topic - az adott topic id-ja.
     * @param topicList 
     * @param n kerdesek szama
     * @return {@link List}<{@link Question}> or null
     */
	List<Question> getQuestions(int minDiff,int maxDiff, List<Integer> topicList, int n);
	
	/**
	 * Visszaadja az adott oszlopban tarolt ertekek kozul a legnagyobbat.
	 * @param column
	 * @return max(column)
	 */
	int getMax(String column);

	/**Bejelentkezesnel nezzuk meg hogy stimmel-e a felhasznalonev meg a jelszo. */
	boolean checkUser(String uname, String pw);
	
	/**Ellenorzi, hogy van-e mar ilyen user, ha nincs akkor addol.
	 * @param user : egy {@link User}
	 * @return sikerult-e*/
	boolean addUser(User user) throws UserAlreadyExistsException;
	
	boolean modifyUser(User user) throws UserNotFoundException;
	
	User getUser(String uname);
	
	boolean deleteUser(String uname);

	List<Statistics> getAgeStatistics(int ageMin, int ageMax);

	Map<String, Integer> getQuestionQuantityByCategory();

	List<Statistics> getTopTenPlayersStatistics();

	Map<String, Integer> getUserQuestionQuantity();

	Statistics getUserStatistics(String username);

	boolean updateStatistics(Statistics stat) throws UserNotFoundException;

	List<RaceQuestion> getRaceQuestions(List<Integer> topicList, int n);

	List<Topic> getTopics();

	Map<String, Integer> getTopFiveMaps();
	
	List<String[]> getUserQuestions(String uname);
	
	List<String[]> getGameWinners();
	
	List<String> getWinners(String map);
	
	Map<String,Integer> getFavMaps(String uname);

	List<Topic> getTopicsWithQuestionNumbers();

	List<String[]> getQuestions();

	List<String> getMapNames();

	boolean addQuestion(Question question);

	List<ForumEntry> getForumEntries(ForumTopic forum_currentTopic, int minNum, int maxNum);

	int getForumEntriesCount(ForumTopic forumTopic);

	List<ForumTopic> getForumTopics();

	boolean addForumEntry(ForumEntry forumEntry);

	boolean addForumTopic(ForumTopic forumTopic);

}
