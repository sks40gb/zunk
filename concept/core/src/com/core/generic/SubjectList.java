package com.core.generic;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sunil
 * @param <C>
 */
public class SubjectList<C extends Subject> {

    List<C> subjectList;

    public SubjectList() {
        subjectList = new ArrayList<>();
    }

    public void print() {
        subjectList.stream().forEach((c)->{
            StringBuilder buffer = new StringBuilder();
            buffer.append("Name :");
            buffer.append(c.getName());
            buffer.append("\n");
            buffer.append("duration");
            buffer.append(c.getDuration());
            System.out.println("INFO : \n " + buffer.toString());
        });
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
