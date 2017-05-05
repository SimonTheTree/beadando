package view;

import javax.swing.JFrame;
import controller.Controller;

import gameTools.state.State;
import gameTools.state.StateManager;
import model.Statistics;
import model.User;
import model.exceptions.UserNotFoundException;
import resources.Resources;

public class MainWindow extends JFrame{
	//--------------------------------------------------------------//
	// STATE ID-s
	//--------------------------------------------------------------//
	public static final String STATE_MAIN = "0";
	public static final String STATE_LOGIN = "0.1";
	public static final String STATE_REGISTER = "0.2";
	public static final String STATE_QUIZ_SETTINGS = "1";
	public static final String STATE_QUIZ = "1.1";
	public static final String STATE_GAME_SETTINGS = "2";
	public static final String STATE_GAME = "2.1";
	public static final String STATE_STAISTICS = "4";
	public static final String STATE_FORUM = "5";
	public static final String STATE_FORUM_TOPIC = "5.1";
	public static final String STATE_PROFILE = "6";
	public static final String STATE_REPORT = "7";
	
	
    private StateManager sm = new StateManager(this);
    public Controller controller;
    private User user = null;
    private Statistics stat = null;
    
    public State main;
    public State gameCreator;
    public State game;
    public State quizCreator;
    public State quiz;
    public State stats;
    public State forum;
    public State forumTopic;
    public State profile;
    public State login;
    public State registration;
    public State report;
// forum    
    
    private static MainWindow self = null;
    
    public static MainWindow getInstance(Controller c){
    	if (self == null){
    		self = new MainWindow(c);
    	}
    	return self;
    }
    
    public static MainWindow getInstance(){
    	return self;
    }
    
    private MainWindow(Controller c){
    	controller = c;
    	initStates();
    	Thread loader = new Thread(() -> {
    		Resources.load();
    		Settings.init();
    	});
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
        this.setTitle(Labels.MAIN_WINDOW_TITLE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        
        sm.addState(main);
        sm.addState(gameCreator);
        sm.addState(game);
        sm.addState(quizCreator);
        sm.addState(quiz);
        sm.addState(stats);
        sm.addState(forum);
        sm.addState(forumTopic);
        sm.addState(profile);
        sm.addState(login);
        sm.addState(registration);
        sm.addState(report);
        
        
        sm.setCurrentState(STATE_LOGIN);
        sm.startCurrentState();
        
        loader.start();
    }
    
    private void initStates() {
    	main = new view.states.MainState(this);
    	gameCreator = new view.states.GameCreatorState(this);
    	game = new view.states.GameState(this);
    	quizCreator = new view.states.QuizCreatorState(this);
    	quiz = new view.states.QuizState(this);
    	stats = new view.states.StatsState(this);
    	forum = new view.states.ForumState(this);
    	forumTopic = new view.states.ForumTopicState(this);
    	profile = new view.states.ProfileState(this);
    	login = new view.states.LoginState(this);
    	registration = new view.states.RegistrationState(this);
    	report = new view.states.ReportState();
    }
    
    public void setState(String s){
        sm.stopCurrentState();
        sm.setCurrentState(s);
        sm.startCurrentState();
    }

	public User getLoggedUser() {
		if(user != null){
			return user;
		} else {
			User u = new User();
			u.setUsername("nobody logged in!");
			return u;
		}
	}
	
	public Statistics getLoggedUserStats() {
		return stat;
	}
	public void pushLoggedUserStats() {
		try {
			controller.updateStatistics(stat);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			System.err.println("invalid Statistics object: ");
			System.err.println(stat);
		}
	}
	public void pushLoggedUser() {
		try {
			controller.modifyUser(user);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			System.err.println("invalid Statistics object: ");
			System.err.println(stat);
		}
	}
	
	public void setUser(User u) {
		user = u;			
		if (u == null){
			u = getLoggedUser();
		} else {
			stat = controller.getUserStatistics(u);
		}
	}
    
    
}