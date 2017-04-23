package view.states;

import java.awt.Dimension;
import java.awt.Graphics;

import gameTools.state.State;
import resources.Resources;

public abstract class DefaultState extends State{
	
	public DefaultState(){
    	super();
    }
    
    public DefaultState(String s){
        super(s);
    }
    
    public DefaultState(String s, int width, int height){
    	super(s, width, height);
    }
	
	@Override
	public void render() {
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(Resources.MAIN_WINDOW_BACKGROUND, 0, 0, getWidth(), getHeight(), this);
	}

}
