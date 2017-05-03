package view.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
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
		setLayout(new BorderLayout());
		String[] titles = datas.get(0);
		datas.remove(0);
		KTableModel tableModel = new KTableModel(datas, titles);
		JTable table = new JTable(tableModel);
        JScrollPane container = new JScrollPane(table);
        
        add(container,BorderLayout.CENTER);
        
        //TODO
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenSize.width -= 14;
        screenSize.height -= 38;
        container.setPreferredSize(new Dimension(Integer.min((tableModel.getColumnCount()*screenSize.width/8),screenSize.width),Integer.min((1+tableModel.getRowCount())*(table.getFont().getSize()+6),screenSize.height-40)));

        this.pack();
		
		this.setLocationRelativeTo(c);
		this.setVisible(true);
	}
	
}
