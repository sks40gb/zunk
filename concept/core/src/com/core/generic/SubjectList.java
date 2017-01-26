package com.core.generic;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sunil
 */
public class SubjectList<C extends Subject> {

    List<C> subjectList;

    public SubjectList() {
        subjectList = new ArrayList<C>();
    }

    public void print() {
        for (Subject c : subjectList) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Name :");
            buffer.append(c.getName());
            buffer.append("\n");
            buffer.append("duration");
            buffer.append(c.getDuration());
            System.out.println("INFO : \n " + buffer.toString());
        }
    }

    public void addSubject(C c) {
        subjectList.add(c);
    }

    public C getSubjectByIndex(int index) {
        return subjectList.get(index);
    }

    public <T> T getMathInFormatOf(int index) {
        return (T) subjectList.get(index);
    }
}
