package view.components;

import java.awt.Color;

import javax.swing.JLabel;

import view.Settings;

public class GLabel extends JLabel {
	public GLabel(String s){
		super(s);
		setFont(Settings.FONT_DEFAULT);
		setForeground(Settings.color_GLabelFont);
	}
	public GLabel(){
		this("");
	}
	
}
