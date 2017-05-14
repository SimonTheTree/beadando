package view.components;

import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import view.MainWindow;

public class AdPopup extends JDialog{

	public static final Color BACKGROUND = new Color(80,40,0);  
	
	public static void displayAd() {
		displayAd(MainWindow.getInstance().controller.getAdvertisement());
	}
	public static void displayAd(String ad) {
		JLabel theAd = new JLabel("<html>" +ad+ "</html>");
		JOptionPane.showMessageDialog(null, theAd, "Reklám", 2, null);
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(400,400);
		frame.setVisible(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		AdPopup.displayAd("<b><i>Hellóó vegyél<br> csavarhúzót!!</i></b>");
	}
	
}
