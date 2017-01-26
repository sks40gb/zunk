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
 * @author sunil
 */
@Table(name = "CONTACT_DETAILS")
public class ContactDetailModel extends TableServletModel {

    public static final String ID = "contactDetailsId";
    public static final String MOBILE_NUMBER = "mobileNumber";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String USER_ID = "userId";
    public static final String EMAIL = "emailId";
    @Column(name = "contact_details_id", isPK = true, autoIncrement = true)
    private int contactDetailsId;
    @ForeignKey(table = UserModel.class, column = UserModel.ID, onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
    @Column(name = "user_id")
    private int userId;
    @Column(name = "mobile_number")
    private String mobileNumber;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "email_id")
    private String emailId;

    public ContactDetailModel(HttpServletRequest request) throws IllegalArgumentException, Exception {
        super(request);
    }

    public ContactDetailModel() {
        super();
    }

    public String getEmailId() {
        return emailId == null ? BLANK : emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public int getContactDetailsId() {
        return contactDetailsId;
    }

    public void setContactDetailsId(int contactDetailsId) {
        this.contactDetailsId = contactDetailsId;
    }

    public String getMobileNumber() {
        return mobileNumber == null ? BLANK : mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber == null ? BLANK : phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

