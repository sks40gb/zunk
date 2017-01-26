/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.sql.table.model;

import com.avi.sql.table.annotation.Column;
import com.avi.sql.table.annotation.ForeignKey;
import com.avi.sql.table.annotation.Table;
import com.avi.sql.table.servlet.model.TableServletModel;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Admin
 */
@Table(name="STAFF")
public class StaffModel extends TableServletModel {

    public static final String ID = "staffId";
    public static final String USER_ID = "userId";
    public static final String TYPE = "type";
    public static final String POST = "post";
    public static final String DEPARTMENT = "department";
    public static final String JOINING_DATE = "joiningDate";
    public static final String RELEAVING_DATE = "releavingDate";
    public static final String QUALIFICATION = "qualification";

    public StaffModel(HttpServletRequest request) throws IllegalArgumentException, Exception {
        super(request);
    }

    public StaffModel(){
        
    }

    @Column(name="staff_id", isPK=true, autoIncrement=true)
    private int staffId;
    @Column(name="user_id")
    @ForeignKey(table=UserModel.class, column=UserModel.ID, onDelete=ForeignKey.CASCADE, onUpdate=ForeignKey.CASCADE)
    private int userId;
    @Column
    private String type;
    @Column
    private String post;
   @Column
    private String department;
    @Column(name="joining_date")
     private Date joiningDate;
    @Column(name="releaving_date")
    private Date releavingDate;
    @Column
    private String qualification;

    public Date getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(Date joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;

    }
         public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public Date getReleavingDate() {
        return releavingDate;
    }

    public void setReleavingDate(Date releavingDate) {
        this.releavingDate = releavingDate;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
