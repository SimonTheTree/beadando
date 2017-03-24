package Controller;

import Model.User;

public class Controller {
	boolean signIn(User u);
    boolean register(User u);
    boolean modifyUser(User u);
    List<User> getHighScore();
    Statistic getUserStatistics(User u);
    Statistic getAverageStatistics();
    Statistic getStatistics(int ageClass);
}
