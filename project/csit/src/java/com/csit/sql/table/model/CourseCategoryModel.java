/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */ 
package com.csit.sql.table.model;

import com.avi.sql.table.servlet.model.TableServletModel;
import com.avi.sql.table.annotation.Column;
import com.avi.sql.table.annotation.Table;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Admin
 */
@Table(name = "COURSE_CATEGORY")
public class CourseCategoryModel extends TableServletModel{

    public static final String ID = "courseCategoryId";
    public static final String NAME = "name";

    @Column(name="course_category_id",isPK=true, autoIncrement=true)
    private int courseCategoryId;
    @Column
    private String name;

    public CourseCategoryModel(HttpServletRequest request) throws IllegalArgumentException, Exception {
        super(request);
    }

    public CourseCategoryModel() {
        
    }

    public int getCourseCategoryId() {
        return courseCategoryId;
    }

    public void setCourseCategoryId(int courseCategoryId) {
        this.courseCategoryId = courseCategoryId;
    }
  

    public String getName() {
        return name == null ? BLANK : name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
