package view;

import javax.swing.JFrame;
import controller.Controller;

import gameTools.state.State;
import gameTools.state.StateManager;

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
	
	
    private StateManager sm = new StateManager(this);
    private Controller controller;
    
    private State main = new view.states.MainState(this);
    private State gameCreator = new view.states.GameCreatorState(this);
    private State game = new view.states.GameState(this);
    private State quizCreator = new view.states.QuizCreatorState(this);
    private State quiz = new view.states.QuizState(this);
    private State stats = new view.states.StatsState(this);
    private State forum = new view.states.ForumState(this);
    private State forumTopic = new view.states.ForumTopicState(this);
    private State profile = new view.states.ProfileState(this);
    private State login = new view.states.LoginState(this);
    private State registration = new view.states.RegistrationState(this);
// forum    
    
    public MainWindow(Controller c){
    	controller = c;
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
        
        sm.setCurrentState(STATE_MAIN);
        sm.startCurrentState();
        this.setVisible(true);
        
    }
    
    public void setState(String s){
        sm.stopCurrentState();
        sm.setCurrentState(s);
        sm.startCurrentState();
    }
    
    
}