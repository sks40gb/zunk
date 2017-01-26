/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import valueobjects.FieldFormatData;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
     *
 * @author sunil
 */
public class OutputFormatModel extends AbstractTableModel {

    private List<FieldFormatData> FieldList;
    private String header[] = {"No.", "Name" , "Separator"};
    
    boolean[] canEdit = new boolean [] {
		false, false, true
	};

    public OutputFormatModel(List<FieldFormatData> FieldList) {
        this.FieldList = FieldList;
    }
        
    public OutputFormatModel(List<FieldFormatData> FieldList, String header[]) {
        this.FieldList = FieldList;
        this.header = header;
    }

    @Override
    public int getRowCount() {
        return FieldList.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {        
        super.setValueAt(aValue, rowIndex, columnIndex);
        if(columnIndex == 0){
            aValue = aValue.toString().replaceAll("\\D", "");
            FieldList.get(rowIndex).setSequence(Integer.parseInt(aValue.toString()));
        }else if(columnIndex == 1){            
            FieldList.get(rowIndex).setName(aValue.toString());
        }else if(columnIndex == 2){
            FieldList.get(rowIndex).setSeparator(aValue.toString());
        }else{
            assert (columnIndex < 3);            
        }
        
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex == 0){            
            return FieldList.get(rowIndex).getSequence() + " " + 
                    ((FieldList.get(rowIndex).getFormat() == null 
                    || FieldList.get(rowIndex).getFormat().equals("")) ? "" : "F");
            
        }else if(columnIndex == 1){
            return FieldList.get(rowIndex).getName();
        }else if(columnIndex == 2){
            return FieldList.get(rowIndex).getSeparator();
        }else{
            assert (columnIndex < 3);
            return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return header[column];
    }   
    
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
       return canEdit [columnIndex];
    }
    
}
