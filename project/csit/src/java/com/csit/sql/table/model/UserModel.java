/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.sql.table.model;

import com.avi.sql.table.servlet.model.TableServletModel;
import com.avi.sql.table.annotation.Column;
import com.avi.sql.table.annotation.Table;
import com.avi.util.Theme;
import java.sql.SQLException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author sunil
 */
@Table(name = "USER")
public class UserModel extends TableServletModel {

    public static final String ID = "userId";
    public static final String USER_NAME = "userName";
    public static final String PASSWORD = "userPassword";
    public static final String FIRST_NAME = "firstName";
    public static final String MIDDLE_NAME = "middleName";
    public static final String LAST_NAME = "lastName";
    public static final String GENDER = "gender";
    public static final String TYPE = "type";
    public static final String THEME = "theme";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    @Column(name = "user_id", isPK = true, autoIncrement = true)
    private int userId;
    @Column(name = "password")
    private String userPassword;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "gender")
    private String gender;
    @Column
    private String theme;
    @Column
    private String type;
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    private String disabled;

    @Override
    public void save() throws SQLException, Exception {
        for(Object user : this.getModelList()){
            if(((UserModel) user).getUserName().equals(userName)){
                throw new Exception("User Already Exists");
            }
        }
        super.save();
    }

    public UserModel(HttpServletRequest request) throws IllegalArgumentException, Exception {
        super(request);
    }

    public UserModel() {
        //super("USER");
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName == null ? BLANK : userName;

    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword == null ? BLANK : userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFirstName() {
        return firstName == null ? BLANK : firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLastName() {
        return lastName == null ? BLANK : lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName == null ? BLANK : middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName == null ? BLANK : middleName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDisabled() {
        return disabled;
    }

    public void setDisabled(boolean isDisabled) {
        if (isDisabled) {
            disabled = "disabled";
        } else {
            disabled = BLANK;
        }
    }

    public String getTheme() {
        return theme == null ? Theme.DEFAULT_THEME : theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    private void validate() throws Exception {
        try {
            //getModelByName(getUserName());
        } catch (Exception e) {
            return;
        }
        throw new Exception("User already exists");
    }
}

