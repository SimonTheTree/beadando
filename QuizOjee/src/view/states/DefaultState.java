package view.states;

import java.awt.Dimension;
import java.awt.Graphics;

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
    	maxTps = 1;
    }
	
	@Override
	public void render() {

	}
	
	@Override
	public void update() {

	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		synchronized (screen) {
			g.drawImage(Resources.MAIN_WINDOW_BACKGROUND, 0, 0, getWidth(), getHeight(), this);			
		}
	}

}
