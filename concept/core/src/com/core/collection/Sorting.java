package com.core.collection;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sunil
 */
public class Sorting {

    public static void main(String[] args) {
        List list = new ArrayList();
        list.add(new ResultRow(3));
        list.add(new ResultRow(2));
        list.add(new ResultRow(5));
        java.util.Collections.sort(list, new java.util.Comparator() {
            public int compare(Object arg0, Object arg1) {
                if (arg0 instanceof ResultRow && arg0 instanceof ResultRow) {
                    return (int)(((ScoreSearchEntry)((ResultRow) arg0).getEntries().get(((ResultRow) arg0).getEntries().size())).getScore() - ((ScoreSearchEntry)((ResultRow) arg1).getEntries().get(((ResultRow) arg1).getEntries().size())).getScore());
                } else {

                    float first = ((ScoreSearchEntry)((ResultRow) arg0).getEntries().get(((ResultRow) arg0).getEntries().size()-1)).getScore();
                    float sec = ((ScoreSearchEntry)((ResultRow) arg1).getEntries().get(((ResultRow) arg1).getEntries().size()-1)).getScore();

                    if(first > sec){
                        return -1;
                    }else if(first < sec){
                        return 1;
                    }else{
                        return 0;
                    }
                 
                }
            }
        });
    }
}

class ResultRow {

    int r;

    public ResultRow(int r) {
        this.r = r;
    }

   
    
    public List getEntries(){
        return  entries_;
    }

    List entries_;
}

class ScoreSearchEntry{
     public float getScore() {
        return (float) 1.1;
    }
}
