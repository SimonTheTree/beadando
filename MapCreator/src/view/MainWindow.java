package view;

import javax.swing.JFrame;

import gameTools.state.State;
import gameTools.state.StateManager;

public class MainWindow extends JFrame{
	//--------------------------------------------------------------//
	// STATE ID-s
	//--------------------------------------------------------------//
	public static final String STATE_CREATOR = "creator";
	
    private StateManager sm = new StateManager(this);
    
    private State creator = new view.states.CreatorState();

// forum    
    
    public MainWindow(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
        this.setTitle(Labels.MAIN_WINDOW_TITLE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        
        sm.addState(creator);

        
        sm.setCurrentState(STATE_CREATOR);
        sm.startCurrentState();
        
        this.setVisible(true);
    }
    
    public void setState(String s){
        sm.stopCurrentState();
        sm.setCurrentState(s);
        sm.startCurrentState();
    }
    
    
}