/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csit.sql.table.model;


import com.avi.sql.table.servlet.model.TableServletModel;
import com.avi.sql.table.annotation.Column;
import com.avi.sql.table.annotation.ForeignKey;
import com.avi.sql.table.annotation.Table;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
/**
 *
 * @author Admin
 */
@Table(name = "STUDENT")
public class StudentModel extends TableServletModel{
    public static final String ID = "studentId";
    public static final String ENROLL_NUMBER = "enrollNumber";
    public static final String USER_ID = "userId";
    public static final String COURSE_ID = "courseId";
    public static final String ADMISSION_DATE = "adminssionDate";
    public static final String COURSE_COMPLETION_DATE = "courseCompletionDate";

    @Column(name = "student_id",isPK=true, autoIncrement=true)
    private int studentId;
    @ForeignKey(table=UserModel.class, column=UserModel.ID, onDelete=ForeignKey.CASCADE, onUpdate=ForeignKey.CASCADE)
    @Column(name = "user_id")
    private int userId;
    @Column(name = "course_id")
    @ForeignKey(table=CourseModel.class,column=CourseModel.ID,onUpdate=ForeignKey.CASCADE)
    private int courseId;
    @Column(name = "enroll_number")
    private String enrollNumber;
    @Column(name = "admission_date")
    private Date adminssionDate;
    @Column(name = "course_completion_date")
    private Date courseCompletionDate;

    public StudentModel(HttpServletRequest request) throws IllegalArgumentException, Exception {
        super(request);
    }

    public StudentModel() {
     
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Date getAdminssionDate() {
        return adminssionDate;
    }

    public void setAdminssionDate(Date adminssionDate) {
        this.adminssionDate = adminssionDate;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int brachId) {
        this.courseId = brachId;
    }

    public Date getCourseCompletionDate() {
        return courseCompletionDate;
    }

    public void setCourseCompletionDate(Date courseCompletionDate) {
        this.courseCompletionDate = courseCompletionDate;
    }

    public String getEnrollNumber() {
        return enrollNumber == null ? BLANK : enrollNumber;
    }

    public void setEnrollNumber(String enrollNumber) {
        this.enrollNumber = enrollNumber;
    }
   
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
