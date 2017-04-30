package view.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import resources.Resources;
import view.Settings;

public class GButton extends JButton{
	
	public GButton(){
		this("");
	}
	public GButton(String s){
		super(s);
		setUI(
			new GButtonUI(
				Settings.color_GButton, 
				Settings.color_GButtonHover, 
				Settings.color_GButtonClick, 
				Settings.FONT_GBUTTON_DEFAULT, 
				Settings.color_GButtonFont
			)
		);
//		setUI(new WoodButtonUI(new ImageIcon(Resources.WOOD_BTN_BG), new ImageIcon(Resources.WOOD_BTN_BG), new ImageIcon(Resources.WOOD_BTN_BG)));
	}
	
	public static void main(String[] args){
		
		JFrame frame = new JFrame();
		GButton b = new GButton("Hello");
		
		frame.add(b);
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		
	}
	
}
