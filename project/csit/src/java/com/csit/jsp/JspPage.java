/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.jsp;

/**
 *
 * @author sunil
 */
public interface JspPage {

    /** ALERT */
    String ALERT_MESSAGE = "/jsp/com/alert/message.jsp";
    String ALERT_ERROR = "/jsp/com/alert/error.jsp";
    
    /** USER */
    String USER_LOGIN = "/jsp/com/user/login.jsp";
    String USER_FORM = "/jsp/com/user/form.jsp";
    String USER_REGISTRATION = "/jsp/com/user/registration.jsp";
    String MY_ACCOUNT = "/jsp/com/user/my_account.jsp";

    /** STAFF */    
    String STAFF_ADD_PAGE_1 = "/jsp/com/staff/add_staff_page1.jsp";
    String STAFF_ADD_PAGE_2 = "/jsp/com/staff/add_staff_page2.jsp";
    String STAFF_MODE = "/jsp/com/staff/staff_mode.jsp";
    String STAFF_UPDATE_PAGE_1 = "/jsp/com/staff/update_staff_page1.jsp";
    String STAFF_UPDATE_PAGE_2 = "/jsp/com/staff/update_staff_page2.jsp";    
    String STAFF_SEARCH = "/jsp/com/staff/staff_search.jsp";
    String STAFF_DELETE = "/jsp/com/staff/deleteStaff.jsp";

    /** STUDENT */
    String STUDENT_PAGE = "/jsp/com/user/Student.jsp";
    String STUDENT_ADD_PAGE_1 = "/jsp/com/student/add_student_page1.jsp";
    String STUDENT_ADD_PAGE_2 = "/jsp/com/student/add_student_page2.jsp";
    String STUDENT_DELETE_PAGE = "/jsp/com/student/delete_student_page.jsp";
    String STUDENT_UPDATE_PAGE_1 = "/jsp/com/student/update_student_page1.jsp";
    String STUDENT_UPDATE_PAGE_2 = "/jsp/com/student/update_student_page2.jsp";
    String STUDENT_MODE = "/jsp/com/student/studentMode.jsp";
    String STUDENT_FEE_ADD = "/jsp/com/student/fee/addFee.jsp";
    
    /** ADMIN */
    String ADMIN_ADD_UPDATE = "/jsp/com/admin/addUpdateAdmin.jsp";
    String ADMIN_DELETE = "/jsp/com/admin/deleteAdmin.jsp";
    String ADMIN_MODE = "/jsp/com/admin/adminMode.jsp"; 

    /** COURSE */
    String COURSE_AVAILABLE = "/jsp/com/course/course_available.jsp";
    String COURSE_ADD = "/jsp/com/course/addCourse.jsp";
    String COURSE_UDPATE = "/jsp/com/course/updateCourse.jsp";
    String COURSE_CATEGORY_ADD = "/jsp/com/course/addCourseCategory.jsp";
    String COURSE_MODE = "/jsp/com/course/courseMode.jsp";
    String COURSE_DETAILS = "/jsp/com/course/course_details.jsp";


    String ADD_SUBJECT = "/jsp/com/course/subject/add_subject.jsp";



    /** SEARCH **/

    String SEARCH_RESULT = "/jsp/com/search/searchResult.jsp";   
    String SEARCH_USER = "/jsp/com/search/searchUser.jsp";

    /** COMMENT */
    String COMMENT_ADD = "/jsp/com/comment/addComment.jsp";
    String COMMENT_DISPLAY = "/jsp/com/comment/displayComment.jsp";

    /** UTIL */
    String UPLOAD_FILE = "/jsp/com/util/upload_file.jsp";
    
}
