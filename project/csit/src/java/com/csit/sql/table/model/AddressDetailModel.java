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
@Table(name = "ADDRESS")
public class AddressDetailModel extends TableServletModel{

    public static final String ID = "addressDetailId";
    public static final String CONTACT_DETAILS_ID = "contactDetailsId";
    public static final String BLOCK = "block";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String COUNTRY = "country";
    public static final String POST_CODE = "postCode";
 
    @Column(name = "address_id",isPK=true, autoIncrement=true)
    private int addressDetailId;
    @Column
    private String block;
    @Column
    private String city;
    @Column 
    private String state;
    @Column
    private String country;
    @Column(name = "post_code")
    private String postCode;
    @Column(name = "contact_details_id")
    @ForeignKey(table=ContactDetailModel.class, column=ContactDetailModel.ID, onDelete=ForeignKey.CASCADE, onUpdate=ForeignKey.CASCADE)
    private int contactDetailsId;

    public AddressDetailModel(HttpServletRequest request) throws IllegalArgumentException, Exception {
        super(request);
    }

    public AddressDetailModel() {
    }

    public int getAddressDetailId() {
        return addressDetailId;
    }

    public void setAddressDetailId(int addressDetailId) {
        this.addressDetailId = addressDetailId;
    }

    public int getContactDetailsId() {
        return contactDetailsId;
    }

    public void setContactDetailsId(int contactDetailsId) {
        this.contactDetailsId = contactDetailsId;
    }

    public String getBlock() {
        return block == null ? BLANK : block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getCity() {
        return city == null ? BLANK : city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state == null ? BLANK : state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostCode() {
        return postCode == null ? BLANK : postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

}

