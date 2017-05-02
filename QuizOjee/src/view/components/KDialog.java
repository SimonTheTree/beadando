package view.components;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import model.Statistics;

public class KDialog extends JDialog {

	private static final long serialVersionUID = 1L;  
	
	public KDialog(Frame c, boolean modal, List<String[]> datas) {
		super(c,modal);
		init(c,datas);
	}
	
	private void init(Frame c, List<String[]> datas) {
		setLayout(new FlowLayout());
		String[] titles = datas.get(0);
		datas.remove(0);
		AbstractTableModel tableModel = new KTableModel(datas, titles);
		JTable table = new JTable(tableModel);
        JScrollPane container = new JScrollPane(table);
        add(container);
        
		this.pack();
		
		this.setLocationRelativeTo(c);
		this.setVisible(true);
	}
	
}
