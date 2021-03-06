package view.states;


import gameTools.state.State;
import model.Statistics;
import view.Labels;
import view.MainWindow;
import view.Settings;

import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import view.components.GLabel;
import view.components.KDialog;
import view.components.GButton;
import view.components.AddQuestionDialog;
import javax.swing.LayoutStyle.ComponentPlacement;

public class StatsState extends DefaultState {
	MainWindow root;
	public StatsState(MainWindow r) {
		super(MainWindow.STATE_STAISTICS, Settings.MAIN_WINDOW_WIDTH, Settings.MAIN_WINDOW_HEIGHT);
		root = r;

		GLabel lblStatistics = new GLabel(Labels.LBL_TITLE_STATS);
			lblStatistics.setFont(Settings.FONT_TITLE);
		
		GButton btnListQuestions = new GButton(Labels.BTN_LIST_QUESTIONS);
			btnListQuestions.addActionListener((e) -> {
				new KDialog(root,true,root.controller.getQuestionsTable());
			});	
		GButton btnListMyQuestions = new GButton(Labels.BTN_LIST_MY_QUESTIONS);
			btnListMyQuestions.addActionListener((e) -> {
				new KDialog(root,true,root.controller.getUserQuestionsTable(root.getLoggedUser().getUsername()));
			});
		GButton btnListMaps = new GButton(Labels.BTN_LIST_MAPS);
			btnListMaps.addActionListener((e) -> {
				new KDialog(root,true,root.controller.getMapNamesTable());
			});
		GButton btnListGlobalFavoriteMaps = new GButton(Labels.BTN_LIST_GLOB_FAV_MAPS);
			btnListGlobalFavoriteMaps.addActionListener((e) -> {
				new KDialog(root,true,root.controller.getTopFiveMapsTable());
			});
		GButton btnListMyFavoriteMaps = new GButton(Labels.BTN_LIST_MY_FAV_MAPS);
			btnListMyFavoriteMaps.addActionListener((e) -> {
				new KDialog(root,true,root.controller.getFavMapsTable(root.getLoggedUser().getUsername()));
			});
		GButton btnListMapWinners = new GButton(Labels.BTN_LIST_MAP_WINNERS);
			btnListMapWinners.addActionListener((e) -> {
				new KDialog(root,true,root.controller.getGameWinnersTable());				
			});
		GButton btnListTopics = new GButton(Labels.BTN_LIST_TOPICS);
			btnListTopics.addActionListener((e) -> {
				new KDialog(root,true,root.controller.getTopicsTable());				
			});
		GButton btnTopTen = new GButton(Labels.BTN_LIST_TOP_TEN);
			btnTopTen.addActionListener((e) -> {
				new KDialog(root,true,root.controller.getTopTenPlayersStatisticsTable());
			});
		GButton btnAddQuestion = new GButton(Labels.BTN_ADD_QUESTION);
			btnAddQuestion.addActionListener((e) -> {
				new AddQuestionDialog(root, true, root.getLoggedUser().getUsername());
			});
		GButton btnAddMap = new GButton(Labels.BTN_ADD_MAP);
			btnAddMap.addActionListener((e) -> {
				
			});
		GButton btnBack = new GButton(Labels.BTN_BACK);
			btnBack.addActionListener((e) -> {
				root.setState(MainWindow.STATE_MAIN);
			});
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(45)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnBack, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnAddMap, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnAddQuestion, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnTopTen, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnListTopics, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnListMapWinners, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnListMyFavoriteMaps, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnListGlobalFavoriteMaps, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnListMaps, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnListMyQuestions, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnListQuestions, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblStatistics))
					.addContainerGap(581, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(38)
					.addComponent(lblStatistics)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnListQuestions, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnListMyQuestions, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnListMaps, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnListGlobalFavoriteMaps, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnListMyFavoriteMaps, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnListMapWinners, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnListTopics, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnTopTen, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnAddQuestion, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnAddMap, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnBack, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(34, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
		
	}

}
