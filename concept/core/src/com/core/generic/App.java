package com.core.generic;

/**
 * Type safety and Type Casting.
 * Generic Class are template Class.
 * Bounded types - <T extends Number>
 * 
 * @author sunil
 */
public class MainClass {

    public static void main(String[] args) {
        Math math = new Math();
        math.setName("Math");
        math.setDuration(1000);

        SubjectList<Math> subjectList = new SubjectList<Math>();
        subjectList.addSubject(math);
       // subjectList.addSubject(physics);
        addDifferentClass(subjectList);
        subjectList.print();       
        
        Math m2 = subjectList.getSubjectByIndex(0);
        //System.out.println(m2.getDuration());

    }

    public static void addDifferentClass(SubjectList subjectList){
        Physics physics = new Physics();
        physics.setDuration(1500);
        physics.setName("Light");
        subjectList.addSubject(physics);
    }

}
