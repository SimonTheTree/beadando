package model;

import java.util.List;

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
     * @return {@link List}<{@link Question}> or null
     */
	List<Question> getQuestions(int diff,int topic);
	
	/**
	 * Visszaadja az adott oszlopban tarolt ertekek kozul a legnagyobbat.
	 * @param column
	 * @return max(column)
	 */
	int getMax(String column);
	
}
