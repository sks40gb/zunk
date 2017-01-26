<%@page import="com.csit.jsp.JspPage" %>
<%@page import="com.csit.jsp.StaticPage" %>
<%@page import="com.csit.servlet.Attribute"%>
<%@page import="com.csit.sql.table.model.*" %>
<script>
    // Javascript originally by Patrick Griffiths and Dan Webb.
    // http://htmldog.com/articles/suckerfish/dropdowns/
    sfHover = function() {
        var sfEls = document.getElementById("navbar").getElementsByTagName("li");
        for (var i=0; i<sfEls.length; i++) {
            sfEls[i].onmouseover=function() {
                this.className+=" hover";
            }
            sfEls[i].onmouseout=function() {
                this.className=this.className.replace(new RegExp(" hover\\b"), "");
            }
        }
    }
    if (window.attachEvent) window.attachEvent("onload", sfHover);
</script>


<%
        String contextpath = application.getContextPath();
        String imagePath = contextpath + "/images/gallery/";
        Object u = session.getAttribute(Attribute.CURRENT_USER);
        UserModel currentUser = u == null ? null : (UserModel) u;
%>
<script src="<%=contextpath%>/js/slideshow.js" type="text/javascript"></script>
<!--<div class="slideshow" ss-time="1">
    <img src="<%=imagePath%>bacj.jpg" alt="Image 2">
</div>
-->
<div id="header">
    <div class="top">
        <% if (currentUser == null) {
        %>
        <a  href="<%=contextpath + JspPage.USER_LOGIN%>">Login</a>
        <a  href="<%=contextpath + JspPage.USER_REGISTRATION%>">Registration</a>
        <% } else {%>
        <b>Welcome : <%=currentUser.getUserName()%></b>
        <a  href="<%=contextpath%>/Logout">Logout</a>
        <%}%>
    </div>
    <div class="middle">
        <img src="<%=contextpath%>/images/archieve/lines_pp_029.gif" width="100%" height="5px">
        <marquee scrolldelay="0" scrollamount="2">Welcome</marquee>
        <img src="<%=contextpath%>/images/archieve/lines_pp_029.gif" width="100%" height="5px">
    </div>
    <div class="bottom">
       
    </div>
</div>
<div id="header-menu">
    <ul id="navbar">

        <% if (currentUser != null) {
            if ("ADMIN".equals(currentUser.getType())) {
        %>
        <!--- First Menu -------------------------------------------------->
        <li><a href="#">Administrator</a>
            <ul>
                <li><a href="<%=contextpath + JspPage.ADMIN_ADD_UPDATE%>?mode=ADD">Add Admin</a></li>
                <li><a href="<%=contextpath + JspPage.ADMIN_MODE%>?mode=UPDATE">Update Admin</a></li>
                <li><a href="<%=contextpath + JspPage.ADMIN_DELETE%>">Delete Admin</a></li>
                <li><a href="<%=contextpath%>/AdminSearch">Display All</a></li>
            </ul>
        </li>

        <!-- Second Menu -------------------------------------------------->
        <li><a href="#">Staff</a>
            <ul>
                <li><a href="<%=contextpath + JspPage.STAFF_ADD_PAGE_1%>">Add Faculty</a></li>
                <li><a href="<%=contextpath + JspPage.STAFF_MODE%>">Update Faculty</a></li>
                <li><a href="<%=contextpath + JspPage.STAFF_DELETE%>">Delete Faculty</a></li>
                <li><a href="<%=contextpath + JspPage.STAFF_SEARCH%>">Display All</a></li>
            </ul>
        </li>

        <%}%>

        <%
        StaffModel staff = new StaffModel();
        staff.addCriteria(StaffModel.USER_ID,currentUser.getUserId());
        staff.getModel();
     if ("ADMIN".equals(currentUser.getType()) || "HOD".equals(staff.getType())) {
        %>

        <!-- Third menu -------------------------------------------------->
        <li><a href="#">Student</a>
            <ul>
                <li><a href="<%=contextpath + JspPage.STUDENT_ADD_PAGE_1%>">Add Student</a></li>
                <li><a href="<%=contextpath + JspPage.STUDENT_DELETE_PAGE%>">Delete Student</a></li>
                <li><a href="<%=contextpath + JspPage.STUDENT_MODE%>">Update Student</a></li>
                <li><a href="<%=contextpath + JspPage.STUDENT_FEE_ADD%>">Fee</a></li>
                <li><a href="<%=contextpath%>/StudentSearch">Display All</a></li>
            </ul>
        </li>

        <!-- Fourth menu -------------------------------------------------->
        <li><a href="#">Courses</a>
            <ul>
                <li><a href="<%=contextpath + JspPage.COURSE_ADD%>">Add Course</a></li>
                <li><a href="<%=contextpath + JspPage.COURSE_CATEGORY_ADD%>">Add Course Category</a></li>
                <li><a href="<%=contextpath + JspPage.COURSE_MODE%>?mode=UPDATE">Update Course</a></li>
                <li><a href="<%=contextpath + JspPage.ADD_SUBJECT%>?mode=ADD">Add Description</a></li>
                <li><a href="<%=contextpath + JspPage.COURSE_AVAILABLE%>">Available</a></li>
            </ul>
        </li>

        <!-- Fifth Menu -------------------------------------------------->
        <li>
            <a href="<%=contextpath + JspPage.SEARCH_USER%>">Search</a>
        </li>
        <%
             }
        }
        %>
       

        <%
        if(currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getType())) {
        boolean display = true;
        if(currentUser != null && currentUser.getType().equals("STAFF")){
            StaffModel staff = new StaffModel();
            staff.addCriteria(StaffModel.USER_ID,currentUser.getUserId());
            staff.getModel();
            if(staff.getType().equals("HOD")){
                display = false;
            }

        }
        if(display){
        %>
        <!---------------------------------------------------------------->
        <li>
            <a href="#">University</a>
            <ul>
                <li><a href="<%=contextpath + StaticPage.UNIVERSITY_CAMPUS%>">Campus</a></li>
                <li><a href="<%=contextpath + StaticPage.UNIVERSITY_LOCATION%>">Location</a></li>
                <li><a href="<%=contextpath + StaticPage.UNIVERSITY_ADMISSION%>">Admission </a></li>
                <li><a href="<%=contextpath + StaticPage.UNIVERSITY_SURGUJA_CAMPUS%>">Surguja Campus </a></li>
            </ul>
        </li>

        <li>
            <a href="#">Desk Of Manager</a>
            <ul>
                <li><a href="<%=contextpath + StaticPage.DESK_VICE%>">Vice Chancellor</a></li>
                <li><a href="<%=contextpath + StaticPage.DESK_REGISTAR%>">Registrar</a></li>
            </ul>
        </li>
        <li>
            <a href="#">Facilities</a>
            <ul>
                <li><a href="<%=contextpath + StaticPage.FACILITY_CENTRAL_LIBRARY%>">Central Library</a></li>
                <li><a href="<%=contextpath + StaticPage.FACILITY_PROCTORIAL_BOARD%>">Proctorial Board</a></li>
                <li><a href="<%=contextpath + StaticPage.FACILITY_NATIONAL_SERVICE%>">National Service</a></li>
                <li><a href="<%=contextpath + StaticPage.FACILITY_HEALTH_CENTER%>">Health Centre</a></li>
                <li><a href="<%=contextpath + StaticPage.FACILITY_HOSTEL%>">Hostel</a></li>
                <li><a href="<%=contextpath + StaticPage.FACILITY_POST_OFFICE%>">Post Office</a></li>
            </ul>
        </li>
        <li>
            <a href="#">Distance Education</a>
            <ul>
                <li><a href="<%=contextpath + StaticPage.DIST_ED_SCHOOL_OF_STUDIES%>">Academic-Programs</a></li>
                <li><a href="<%=contextpath + StaticPage.DIST_ED_APPLICATION_FORM%>">Application Form</a></li>
                <li><a href="<%=contextpath + StaticPage.DIST_ED_PROGRAMS%>">Objectives</a></li>
            </ul>
        </li>

        <%
        }
        }
        %>

  <!---------------------------------------------------------------->
        <% if (session.getAttribute(Attribute.CURRENT_USER) != null) {%>
        <li>
            <a href="#">Theme</a>
            <ul>
                <li><a href="<%=contextpath%>/RotateTheme?theme=classical">Classical</a></li>
                <li><a href="<%=contextpath%>/RotateTheme?theme=green">Green</a></li>
                <li><a href="<%=contextpath%>/RotateTheme?theme=indigo">Indigo</a></li>
                <li><a href="<%=contextpath%>/RotateTheme?theme=purple">Purple</a></li>
                <li><a href="<%=contextpath%>/RotateTheme?theme=water">Water</a></li>
            </ul>
        </li>
        <%}%>
        <!---------------------------------------------------------------->

    </ul>
</div>

