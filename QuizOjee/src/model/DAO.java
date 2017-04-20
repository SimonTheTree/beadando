package model;

import java.util.List;

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
     * @return {@link List}<{@link Question}> or null
     */
	List<Question> getQuestions(int minDiff,int maxDiff, List<Integer> topicList);
	
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
}
