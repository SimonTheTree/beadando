package view.components;

import java.util.Map;

import javax.swing.GroupLayout;
import view.components.GButton;
import view.components.GLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;

import model.User;
import view.Labels;
import view.MainWindow;
import view.Settings;

public class PlayerReport extends JPanel{
	private JTable tblDiff;
	private JTable tblTopic;
	private GLabel lblRAnsVal;
	private GLabel lblWAnsVal;
	private GLabel lblRTipsVal;
	private GLabel lblWTipsVal;
	private GLabel lblTerrWonVal;
	private GLabel lblTerrLostVal;
	private GLabel lblPointsVal;
	private GLabel lblQuestNVal;
	private User user;
	private static final String NOTHING = "-";
	
	public PlayerReport(User u){
		user = u;
		setOpaque(false);
		tblTopic = new JTable();
		tblDiff = new JTable();
		tblDiff.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblDiff.setRowSelectionAllowed(false);
		tblDiff.setFillsViewportHeight(true);
		
		JScrollPane scrollPane = new JScrollPane();
			scrollPane.setViewportView(tblTopic);
		
		GLabel lblReportTitle = new GLabel(Labels.LBL_TITLE_REPORT.replaceFirst("@", user.getUsername()));
			lblReportTitle.setFont(Settings.FONT_TITLE);
		GLabel lblPoints = new GLabel(Labels.LBL_EARNED_POINTS);
		GLabel lblQuestNum = new GLabel(Labels.LBL_NUMBER_OF_QUESTIONS);
		GLabel lblQuestDiffs = new GLabel(Labels.LBL_QUESTION_DIFFICULTIES);
			lblQuestDiffs.setFont(Settings.FONT_SUB_TITLE);
		GLabel lblQuestTopics = new GLabel(Labels.LBL_TOPICS);
			lblQuestTopics.setFont(Settings.FONT_SUB_TITLE);
		GLabel lblRightAns = new GLabel(Labels.LBL_N_RIGHT_ANS);
		GLabel lblWrongAns = new GLabel(Labels.LBL_N_WRONG_ANS);
		GLabel lblRightTips = new GLabel(Labels.LBL_N_RIGHT_TIPS);
		GLabel lblWrongTips = new GLabel(Labels.LBL_N_WRONG_TIPS);
		GLabel lblTerritoriesWon = new GLabel(Labels.LBL_TERRITORIES_WON);
		GLabel lblTerritoriesLost = new GLabel(Labels.LBL_TERRITORIES_LOST);
		lblRAnsVal = new GLabel(NOTHING);
		lblWAnsVal = new GLabel(NOTHING);
		lblRTipsVal = new GLabel(NOTHING);
		lblWTipsVal = new GLabel(NOTHING);
		lblTerrWonVal = new GLabel(NOTHING);
		lblTerrLostVal = new GLabel(NOTHING);
		lblPointsVal = new GLabel(NOTHING);
		lblQuestNVal = new GLabel(NOTHING);
		
		GButton btnQuit = new GButton(Labels.BTN_QUIT);
			btnQuit.addActionListener((e) -> {
				MainWindow.getInstance().setState(MainWindow.STATE_MAIN);
			});
				
		GroupLayout gl = new GroupLayout(this);
		gl.setHorizontalGroup(
			gl.createParallelGroup(Alignment.LEADING)
				.addGroup(gl.createSequentialGroup()
					.addGroup(gl.createParallelGroup(Alignment.LEADING)
						.addGroup(gl.createSequentialGroup()
							.addGap(63)
							.addComponent(lblReportTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl.createSequentialGroup()
							.addGap(87)
							.addGroup(gl.createParallelGroup(Alignment.LEADING)
								.addComponent(lblQuestTopics, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl.createParallelGroup(Alignment.TRAILING, false)
									.addGroup(gl.createSequentialGroup()
										.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 317, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.UNRELATED)
										.addGroup(gl.createParallelGroup(Alignment.LEADING, false)
											.addComponent(lblRightTips, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(lblWrongAns, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(lblRightAns, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
											.addComponent(lblWrongTips, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(lblTerritoriesWon, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(lblTerritoriesLost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl.createParallelGroup(Alignment.LEADING)
											.addGroup(gl.createSequentialGroup()
												.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(btnQuit, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE))
											.addGroup(gl.createSequentialGroup()
												.addPreferredGap(ComponentPlacement.RELATED)
												.addGroup(gl.createParallelGroup(Alignment.LEADING)
													.addComponent(lblWAnsVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(lblRAnsVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(lblRTipsVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(lblWTipsVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(lblTerrWonVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(lblTerrLostVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))))
									.addComponent(lblQuestDiffs, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(tblDiff, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 742, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl.createSequentialGroup()
									.addGroup(gl.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(lblQuestNum, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(lblPoints, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl.createParallelGroup(Alignment.LEADING)
										.addComponent(lblQuestNVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblPointsVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))))
					.addContainerGap(72, Short.MAX_VALUE))
		);
		gl.setVerticalGroup(
			gl.createParallelGroup(Alignment.LEADING)
				.addGroup(gl.createSequentialGroup()
					.addGap(36)
					.addComponent(lblReportTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(46)
					.addGroup(gl.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPoints, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPointsVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblQuestNum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblQuestNVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblQuestDiffs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tblDiff, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
					.addGroup(gl.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnQuit, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl.createSequentialGroup()
							.addComponent(lblQuestTopics, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl.createParallelGroup(Alignment.LEADING)
								.addGroup(gl.createSequentialGroup()
									.addGroup(gl.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblRightAns, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblRAnsVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblWrongAns, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblWAnsVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblRightTips, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblRTipsVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblWrongTips, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblWTipsVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblTerritoriesWon, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblTerrWonVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblTerritoriesLost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblTerrLostVal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 225, GroupLayout.PREFERRED_SIZE))))
					.addGap(68))
		);
		
		setLayout(gl);
	}
	public void setTblDiff(int[][] diffN) {
		DefaultTableModel tblmodel = new DefaultTableModel();
		String[][] data = new String[2][diffN.length+1];
		String[] row = new String[diffN.length+1];
		tblmodel.setColumnCount(row.length);
		
		row[0] = Labels.LBL_DIFFICULITY;
		for(int i = 0; i < diffN.length; i++){
			row[i+1] = String.valueOf(diffN[i][0]);
		}
		tblmodel.addRow(row);
		row[0] = Labels.LBL_NUMBER_OF_QUESTIONS;
		for(int i = 0; i < diffN.length; i++){
			row[i+1] = String.valueOf(diffN[i][1]);
		}
		tblmodel.addRow(row);
		
//		data[0][0] = Labels.LBL_DIFFICULITY;
//		data[1][0] = Labels.LBL_NUMBER_OF_QUESTIONS;
//		
//		for(int i = 0; i < diffN.length; i++){
//			for(int j = 0; j < diffN[i].length; j++){
//				data[j][i+1] = String.valueOf(diffN[i][j]);
//			}
//		}
//		tblmodel = new DefaultTableModel(data, new String[diffN.length+2]);
		tblDiff.setModel(tblmodel);
		tblDiff.getColumnModel().getColumn(0).setMinWidth(150);
//		tblDiff.getColumnModel().getColumn(1).setMinWidth(15);			
		
	}
	public void setTblTopic(Map<Object, Integer> tblTopic) {
	}
	public void setRAnsVal(int val) {
		lblRAnsVal.setText(String.valueOf(val));
	}
	public void setWAnsVal(int val) {
		lblWAnsVal.setText(String.valueOf(val));
	}
	public void setRTipsVal(int val) {
		lblRTipsVal.setText(String.valueOf(val));
	}
	public void setWTipsVal(int val) {
		lblWTipsVal.setText(String.valueOf(val));
	}
	public void setTerrWonVal(int val) {
		lblTerrWonVal.setText(String.valueOf(val));
	}
	public void setTerrLostVal(int val) {
		lblTerrLostVal.setText(String.valueOf(val));
	}
	public void setPointsVal(int val) {
		lblPointsVal.setText(String.valueOf(val));
	}
	public void setQuestNVal(int val) {
		lblQuestNVal.setText(String.valueOf(val));
	}
	public String getTitle(){
		return user.getUsername();
	}
}