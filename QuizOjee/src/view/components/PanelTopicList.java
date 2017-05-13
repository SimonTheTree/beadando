package view.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import controller.Controller;
import model.Topic;
import view.Labels;
import view.MainWindow;
import view.Settings;

public class PanelTopicList extends JPanel{
	
	List<TopicRow> rows = new ArrayList<>();
	GButton btnSelectAll, btnDeselectAll;
	public static void main(String[] args){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		System.out.println("staring");
		Controller controller = new Controller();
		List<Topic> list = controller.getTopics();
		PanelTopicList listPanel = new PanelTopicList(list);
		frame.add(listPanel);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	public PanelTopicList(List<Topic> l) {
		for (Topic t : l) {
			rows.add(new TopicRow(t));
		}
		
		setLayout(new BorderLayout());
		
		btnSelectAll = new GButton(Labels.BTN_SELECT_ALL);
			btnSelectAll.addActionListener((e) -> {
				for(TopicRow t : rows) {
					t.check.setSelected(true);
				}
			});
		btnDeselectAll = new GButton(Labels.BTN_DESELECT_ALL);
			btnDeselectAll.addActionListener((e) -> {
				for(TopicRow t : rows) {
					t.check.setSelected(false);
				}
			});
		
		JPanel panelNorth = new JPanel();
		panelNorth.setLayout(new GridLayout(1, 2,5,5));
		panelNorth.add(btnSelectAll);
		panelNorth.add(btnDeselectAll);
		
		JPanel panelCenter = new JPanel();
		JScrollPane scrollpane = new JScrollPane(panelCenter);
		panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.Y_AXIS));
		for(TopicRow t : rows) {
			panelCenter.add(t);
		}
		
		add(panelNorth, BorderLayout.NORTH);
		add(scrollpane, BorderLayout.CENTER);
		
	}
	
	public List<Integer> getListOfSelectedTopicIDs(){
		List<Integer> ret = new ArrayList<>();
		for(TopicRow t : rows) {
			if(t.check.isSelected()) {
				ret.add(t.topic.getTopicId());
			}
		}
		return ret;
	}
	
	private class TopicRow extends JPanel{
		Topic topic;
		JCheckBox check;
		
		public TopicRow(Topic t) {
			topic = t;
			check = new JCheckBox("",true);
				check.setPreferredSize(new Dimension(10,10));
				check.setMargin(new Insets(2,2,2,2));
			JLabel label = new JLabel(topic.getName()); 
				setAlignmentX(Component.LEFT_ALIGNMENT);
				label.setMinimumSize(new Dimension(200, 10));
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(check);
			add(label);
		}
	}
	
}
