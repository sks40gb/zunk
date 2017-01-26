<html>
    <head>
        <title>Health-Centre</title>
        <%@include file="/jsp/com/init.jsp" %>
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
                    <td id="column3" class="column-eighty">
                        <div id="content">
                            <div class="column-seventy-five"  style="float:left">

                            <div class="title">   FACILITIES IN THE CAMPUS</div>

                            <div class="sub-title">HEALTH CENTRE</div>

                            <p>Health facilities to the students and residents is available in the health centre located near the University Computer Centre. One Medical officer, Dr. A.N. Mondal from University's Medical College, CIMS with other assistant staff are posted in the center. Specialist from CIMS visit the health centre in the campus time to time for specialized check up & advice. Contact Phone : 07752-202317, 260048,260356</p>


                            <p>University has a separate SC/ST cell as per the UGC guideline, which was established in November 1988. The cell processes and provides, assistance to the SC/ST students to get scholarships as per the govt. rules and deals with all the problems of the SC/ST students. The cell organizes special coaching programs for the students belonging to this category The cell also observes the reservation policy pursued by the academic departments in regard of admission.</p>
                            </div>
                            <div class="column-twenty" style="float:right"><img src="<%=application.getContextPath()%>/images/static/mbbs.jpg"></div>
                            <div style="float:right">Dr. A.N. Mondal</div>
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
