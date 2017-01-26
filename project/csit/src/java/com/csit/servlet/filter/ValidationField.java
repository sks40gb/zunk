/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csit.servlet.filter;

import com.csit.sql.table.model.AddressDetailModel;
import com.csit.sql.table.model.ContactDetailModel;
import com.csit.sql.table.model.CourseCategoryModel;
import com.csit.sql.table.model.CourseModel;
import com.csit.sql.table.model.FeeModel;
import com.csit.sql.table.model.StaffModel;
import com.csit.sql.table.model.StudentModel;
import com.csit.sql.table.model.UserModel;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sunil
 */
public class ValidationField{

    private static Map<String, Rule[]> validateFieldMap;

    static{
        init();
    }

    private static void init(){

        /** user */
        addField(UserModel.USER_NAME);
        addField(UserModel.PASSWORD);
        addField(UserModel.FIRST_NAME);
        addField(UserModel.LAST_NAME);
        addField(UserModel.DATE_OF_BIRTH,Rule.DATE);

        /** Contact Details */
        addField(ContactDetailModel.EMAIL, Rule.EMAIL);
        addField(ContactDetailModel.MOBILE_NUMBER);

        /** Address */
        addField(AddressDetailModel.CITY);
        addField(AddressDetailModel.COUNTRY);
        addField(AddressDetailModel.POST_CODE);

        /** Student */
        addField(StudentModel.ENROLL_NUMBER);
        addField(StudentModel.ADMISSION_DATE, Rule.DATE);
        addField(StudentModel.COURSE_COMPLETION_DATE, Rule.DATE);

        /** Staff */
        addField(StaffModel.JOINING_DATE, Rule.DATE);
        addField(StaffModel.RELEAVING_DATE, Rule.DATE);
        addField(StaffModel.TYPE);
        addField(StaffModel.QUALIFICATION);

        /** Course Category */
        addField(CourseCategoryModel.NAME);

        /** Course */
        addField(CourseModel.DURATION);
        addField(CourseModel.ELIGIBILITY);
        addField(CourseModel.COURSE);
        addField(CourseModel.SEAT);

        /** FEE*/
        addField(FeeModel.AMOUNT, Rule.DOUBLE);
        addField(FeeModel.LATE_FEE, Rule.DOUBLE);
        addField(FeeModel.DUE_DATE, Rule.DATE);
    }

    /** by default it checks for null or empty values */
    public static void addField(String name){
        addField(name, new Rule[]{Rule.NULL});
    }

    public static void addField(String name, Rule ... rules){
        if(validateFieldMap == null){
            validateFieldMap = new HashMap<String, Rule[]>();
        }
        validateFieldMap.put(name, rules);
    }

    public static Map<String, Rule[]> getValidateFieldMap() {
        if(validateFieldMap == null){
            init();
        }
        return validateFieldMap;
    }
  
}
