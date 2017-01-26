
<%@page import="com.csit.jsp.JspPage" %>
<%@page import="com.csit.jsp.StaticPage" %>
<%@page import="com.csit.servlet.Attribute"%>

<div id="">
    <div id="side-menu">
        <ul>
            <% if (session.getAttribute(Attribute.CURRENT_USER) == null) {
            %>
            <li><a  href="<%=application.getContextPath() + JspPage.USER_LOGIN%>">Login</a></li>
            <li><a  href="<%=application.getContextPath() + JspPage.USER_REGISTRATION%>">Registration</a></li>
            <% } else {%>
            <li><a  href="<%=application.getContextPath()%>/Logout">Logout</a></li>
            <li><a href="<%=application.getContextPath() + JspPage.MY_ACCOUNT%>">My Account</a></li>
            <%}%>
            <li><a href="<%=application.getContextPath() + JspPage.COURSE_AVAILABLE%>">Courses</a></li>
            <li><a  href="<%=application.getContextPath() + StaticPage.LEFT_NAV_RESEARCH%>">Research</a></li>
            <li><a  href="<%=application.getContextPath() + StaticPage.LEFT_NAV_PHOTO_GALLERY%>">Photo Gallery</a></li>
            <li><a  href="<%=application.getContextPath() + StaticPage.LEFT_NAV_DOWNLOAD%>">Download</a></li>
            <% if (session.getAttribute(Attribute.CURRENT_USER) != null) {%>
             <li><a  href="<%=application.getContextPath() + JspPage.COMMENT_ADD%>">Comment</a></li>
            <%}%>
            <li><a  href="<%=application.getContextPath() + JspPage.COMMENT_DISPLAY%>">Display Comments</a></li>
            <li><a href="mailto:csit@ggu.ac.in">Feedback</a></li>
            <li><a  href="<%=application.getContextPath() + StaticPage.LEFT_NAV_CONTACT%>">Contact Us</a></li>
        </ul>
    </div>
</div>
