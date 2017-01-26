/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */ 
package com.csit.sql.table.model;

import com.avi.sql.table.servlet.model.TableServletModel;
import com.avi.sql.table.annotation.Column;
import com.avi.sql.table.annotation.ForeignKey;
import com.avi.sql.table.annotation.Table;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Admin
 */
@Table(name = "SUBJECT")
public class SubjectModel extends TableServletModel{

    public static final String ID = "subjectId";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String COURSE_ID = "courseId";

    @Column(name="subject_id",isPK=true, autoIncrement=true)
    private int subjectId;

    @Column(name="course_id")
    @ForeignKey(table=CourseModel.class, column=CourseModel.ID, onDelete=ForeignKey.CASCADE, onUpdate=ForeignKey.CASCADE)
    private int courseId;
    
    @Column
    private String name;

    @Column
    private String description;

    public SubjectModel(HttpServletRequest request) throws IllegalArgumentException, Exception {
        super(request);
    }

    public SubjectModel() {
        
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
