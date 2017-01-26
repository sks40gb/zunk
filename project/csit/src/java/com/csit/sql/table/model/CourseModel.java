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

@Table(name = "COURSE")
public class CourseModel extends TableServletModel{
    
    public static final String ID = "courseId";
    public static final String COURSE = "name";
    public static final String DURATION = "duration";
    public static final String SEAT = "seat";
    public static final String ELIGIBILITY = "eligibility";
    public static final String COURSEFEE = "courseFee";
    public static final String COURSE_CATEGORY_ID = "courseCategoryId";
   
    @Column(name="course_id",isPK=true, autoIncrement=true)
    private int courseId;
    @Column(name="course_category_id")
    @ForeignKey(table=CourseCategoryModel.class, column=CourseCategoryModel.ID, onDelete=ForeignKey.CASCADE, onUpdate=ForeignKey.CASCADE)
    private int courseCategoryId;
    @Column
    private String name;
    @Column  
    private String duration;
    @Column
    private String seat;
    @Column
    private String eligibility;
     @Column(name="course_fee")
    private int courseFee;

    public CourseModel(HttpServletRequest request) throws IllegalArgumentException, Exception {
        super(request);
    }
    
    public CourseModel() {        
    }

    public int getCourseCategoryId() {
        return courseCategoryId;
    }

    public void setCourseCategoryId(int courseCategoryId) {
        this.courseCategoryId = courseCategoryId;
    }

    public String getDuration() {
        return duration == null ? BLANK : duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEligibility() {
        return eligibility == null ? BLANK : eligibility;
    }

    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name == null ? BLANK : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeat() {
        return seat == null ? BLANK : seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public int getCourseFee() {
        return courseFee;
    }

    public void setCourseFee(int courseFee) {
        this.courseFee = courseFee;
    }
}
