<html>
    <head>
        <title>Photo-Gallery</title>
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
                    <td id="column3" class="column-eighty">
                        <div id="content">
                            <div class="slideshow" ss-time="1">
                                <img src="<%=path%>/images/gallery/image1.jpg" width="120px" height="75px"/>
                                <img src="<%=path%>/images/gallery/image2.jpg" width="120px" height="75px"/>
                                <img src="<%=path%>/images/gallery/image3.jpg" width="120px" height="75px"/>
                                <img src="<%=path%>/images/gallery/image4.jpg" width="120px" height="75px"/>
                                <img src="<%=path%>/images/gallery/image5.jpg" width="120px" height="75px"/>
                            </div>
                            <div class="slideshow" ss-time="1" ss-direction="right">
                                <img src="<%=path%>/images/gallery/image6.jpg" width="120px" height="75px"/>
                                <img src="<%=path%>/images/gallery/image7.jpg" width="120px" height="75px"/>
                                <img src="<%=path%>/images/gallery/image8.jpg" width="120px" height="75px"/>
                                <img src="<%=path%>/images/gallery/image9.jpg" width="120px" height="75px"/>
                                <img src="<%=path%>/images/gallery/image10.jpg" width="120px" height="75px"/>
                            </div>
                            <div class="slideshow" ss-time="1">
                                <img src="<%=path%>/images/gallery/image11.jpg" width="120px" height="75px"/>
                                <img src="<%=path%>/images/gallery/image12.jpg" width="120px" height="75px"/>
                                <img src="<%=path%>/images/gallery/image13.jpg" width="120px" height="75px"/>
                                <img src="<%=path%>/images/gallery/image14.jpg" width="120px" height="75px"/>
                                <img src="<%=path%>/images/gallery/image15.jpg" width="120px" height="75px"/>
                            </div>

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
