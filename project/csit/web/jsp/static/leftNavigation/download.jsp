<html>
    <head>
        <title>Download</title>
        <%@include file="/jsp/com/init.jsp" %>
        <%String path = application.getContextPath();%>
        <script type="text/javascript" src="<%=path%>/js/rounded-box.js"></script>
    </head>

    <body>
        <center>

            <table class="container">
                <tr>
                    <td id="column1" colspan="2">
                        <%@include file="/jsp/com/header.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td id="column2" class="column-twenty">
                        <%@include file="/jsp/com/left.jsp" %>
                    </td>
                    <style>
                        #content a{
                         text-decoration:none;
                         color:navy;
                        }
                    </style>
                    <td id="column3" class="column-eighty">
                        <div id="content">
                            <div class="title">DOWNLOAD FORMS</div>
                            <ul>
                                <li><a href="<%=path%>/download/form/APPLICATION_FORM09-10.pdf">Admission Form U.T.D.</a></li>
                                <li><a href="<%=path%>/download/form/Adhoc.pdf">MFM Admission Application Form</a></li>
                                <li><a href="<%=path%>/download/form/DuplicateMarksheet.pdf">Duplicate Mark sheet Request Form</a></li>
                                <li><a href="<%=path%>/download/form/Enrollment1.pdf">Enrollment Application Form</a></li>
                                <li><a href="<%=path%>/download/form/Noduse.pdf">No-Dues Form</a></li>
                                <li><a href="<%=path%>/download/form/patrata1.pdf">Eligibility Application Form</a></li>
                                <li><a href="<%=path%>/download/form/Revaluation1.pdf">Revaluation Application Form</a></li>
                                <li><a href="<%=path%>/download/form/TC.pdf">Transfer Certificate Request Form</a></li>
                                <li><a href="<%=path%>/download/form/I_card.pdf">Identification Card application Form</a></li>
                            </ul>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td id="column4" colspan="2">
                        <%@include file="/jsp/com/footer.jsp" %>
                    </td>
                </tr>

            </table>
        </center>
    </body>
</html>
