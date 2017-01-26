/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 * Model for the Functions JList 
 * @author sunil
 */
public class CheckBoxListModel implements ListModel {

    Map map;
    List sList;

    public CheckBoxListModel(Map value) {
        this.map = value;
        sList = new ArrayList();
        for (Object obj : map.values()) {
            sList.add(obj);
        }
    }

    @Override
    public int getSize() {
        return sList.size();
    }

    @Override
    public Object getElementAt(int index) {
        return sList.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
