/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import valueobjects.Feedback;
//import com.mysql.jdbc.ResultSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author sunil
 */
public class CheckBoxTableModel extends AbstractTableModel {

    private ResultSet resultSet = null;
    private String[] columnNames = null;
    private Object[][] data;

    public CheckBoxTableModel(ResultSet results, String[] headings) throws SQLException {
        this.resultSet = results;
        this.columnNames = headings;
        int count = 0;
        while (results.next()) {
            count++;
        }
        data = new Object[count][4];
        getData(results, count);
    }

    public CheckBoxTableModel(Feedback feedback, String[] headings) {
        try {
            String image_path = feedback.getImagePath();
            List imageList = feedback.getImageList();
            List<String> selectImageList = feedback.getSelectedImageList();

            int i = 0;
            this.columnNames = headings;
            data = new Object[imageList.size()][2];
            for (Object object : imageList) {
                if (isMatched(selectImageList, object, image_path)) {
                    data[i][0] = new Boolean("true");
                } else {
                    data[i][0] = new Boolean("false");
                }
                data[i][1] = image_path + "/" + object.toString();
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isMatched(List<String> selectImageList, Object pattern, String imagepath) {
        if (selectImageList == null) {
            return false;
        }
        for (String match : selectImageList) {            
            if (match.equals(imagepath + "/" + pattern.toString())) {
                return true;
            }
        }

        return false;
    }

    private Object[][] getData(ResultSet resultSet, int count) {
        try {
            for (int i = 0; resultSet.previous(); i++) {
                data[i][0] = new Boolean(resultSet.getString(1));
                if (this.columnNames.length < 4) {
                    data[i][1] = i + 1 + "";
                    data[i][2] = resultSet.getString(3);
                } else {
                    data[i][1] = resultSet.getString(2);
                    data[i][2] = resultSet.getString(3);
                    data[i][3] = resultSet.getString(4);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        if (data == null) {
            return 0;
        }
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col > 0) {
            return false; 
        }
        return true;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
}