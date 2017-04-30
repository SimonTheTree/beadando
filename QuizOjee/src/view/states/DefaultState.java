package view.states;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JPanel;

import gameTools.state.State;
import resources.Resources;
import view.Settings;

public abstract class DefaultState extends State{
	
	public DefaultState(){
    	this("");
    }
    
    public DefaultState(String s){
        this(s, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
    }
    
    public DefaultState(String s, int width, int height){
    	super(s, width, height);
    	maxFps = 70;
    	maxTps = 10;
    }
	
	@Override
	public void render() {
		g.drawImage(Resources.MAIN_WINDOW_BACKGROUND, 0, 0, getWidth(), getHeight(), this);
	}
	
	@Override
	public void update() {
	}
	

}
