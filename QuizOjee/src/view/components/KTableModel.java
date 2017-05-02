package view.components;

import java.util.List;
import javax.swing.table.AbstractTableModel;

public class KTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 2390181858155263994L;

    private String[] columnNames;
    private List<String[]> elements;
	
	public KTableModel(List<String[]> elements, String... columnNames) {
		super();
		this.columnNames = columnNames;
		this.elements = elements;
		//prepareDataStructure(elements);
	}
	
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return elements.size();
	}

	public Object getValueAt(int i, int j) {
		return elements.get(i)[j];
	}
	
    public String getColumnName(int col) {
		return columnNames[col];
		
	}

    public boolean isCellEditable(int row, int col) {
        return false;
    }

}
