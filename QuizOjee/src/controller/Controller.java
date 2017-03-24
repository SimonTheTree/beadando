package controller;

import java.util.List;
import model.Statistics;
import model.Question;

import model.User;
import model.exceptions.UserAlreadyExistsException;

public class Controller {
	/**
	 * Bejelentkezik (user-pw check)
	 * @param uname
	 * @param pw
	 * @return true=OK
	 */
	boolean signIn(String uname, String pw){
		return false;
	};
	/**
	 * Létrehoz egy usert a user táblában, ha 
	 * lehetséges, ha nem akkor hiba van
	 * @param u
	 * @param pw
	 * @throws UserAlreadyExistsException if the username is taken 
	 * @return
	 */
    boolean register(User u, String pw) throws UserAlreadyExistsException{
    	return false;
    };
    
    /**
     * Felülírja a user jelszavát
     * @param uname
     * @param pw
     * @return false ha vmi nem működött
     */
    boolean setPassword(String uname, String pw){
    	return false;
    };
    
    /**
     * A paraméterben kapott User adatait frissíti az adatbázisban.
     * A user username-jét ki kell tölteni. 
     * @param u
     * @return
     */
    boolean modifyUser(User u){
    	return false;
    };
    /**
     * uname azonosítójú user User objektumával tér vissza, 
     * mely tartalmazza a user összes adatát
     * @param uname
     * @return
     */
    User getUser(String uname){
    	return null;
    }
    /**
     * highscore-t konstruál. A User objektumokban 
     * van username, realname, points  
     * @param compare itt adjuk meg a rendezési logikát
     * @param limit hány fős statisztikát szeretnénk
     * @return
     */
    List<Statistics> getHighScore(Comparable<Statistics> compare, int limit){
    	return null;
    };
    
    Statistics getUserStatistics(String uname){
    	return null;
    };
    
    Statistics getAverageStatistics(){
    	return null;
    };
    /**
     * Átlagos statisztikát készít az ageMax és 
     * ageMin évesek között (ageMax-t, ageMin-t beszámítva)
     * @param ageMax
     * @param ageMin
     * @return
     */
    Statistics getAgeStatistics(int ageMax, int ageMin){
    	return null;
    };
}
