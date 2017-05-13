package view.states;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import java.awt.BorderLayout;

import model.User;
import view.MainWindow;
import view.Settings;
import view.components.PlayerReport;
public class ReportState extends DefaultState {
	private JTabbedPane tabbedPane;

	public ReportState() {
		super(MainWindow.STATE_REPORT);
		setLayout(new BorderLayout(0, 0));
		
		Object orig = UIManager.get("TabbedPane.contentOpaque"); 
		UIManager.put("TabbedPane.contentOpaque", false); 
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		UIManager.put("TabbedPane.contentOpaque", orig);
		add(tabbedPane, BorderLayout.CENTER);
//		tabbedPane.setOpaque(false);
		
	}
	
	public void setReports(PlayerReport[] reports){
		int i = 0;
		tabbedPane.removeAll();
		for (PlayerReport pr : reports){
			tabbedPane.addTab(pr.getTitle(), null, pr, null);
			tabbedPane.setEnabledAt(i++, true);
		}
	}
	
	public static void main(String[] args){
		JFrame f = new JFrame();
		ReportState rs = new ReportState();
		f.add(rs);
		f.setLocationRelativeTo(null);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		User u = new User();
		u.setUsername("alfred");
		User u2 = new User();
		u2.setUsername("beni");
		rs.start();
		PlayerReport[] p = new PlayerReport[2];
		
		//init difficulitycounter array
		int[][] diffN =  new int[15][2];
		int i = 0;
		for(int[] arr : diffN){
			arr[0] = i++;
			arr[1] = 15-i+1;
		}

		p[0] =  new PlayerReport(u);
		p[0].setQuestNVal(Settings.game_numOfQuestions);
		p[0].setRAnsVal(10);
		p[0].setWAnsVal(20);
		p[0].setTblDiff(diffN);
		
		p[1] =  new PlayerReport(u2);
		p[1].setQuestNVal(240);
		p[1].setRAnsVal(110);
		p[1].setWAnsVal(230);
		rs.setReports(p);
	}
}
