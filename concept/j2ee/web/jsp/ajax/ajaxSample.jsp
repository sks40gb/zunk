<html>
    <head>
        <title>JSP and Servlet using AJAX</title>

        <style>
            #progressBarContainer {
                margin-top:10px;
                float:left;
                height:10px;
                padding:1px;
                width:500;
                border:1px solid #92917C;
            }
            #progressBar {
                float:left;
                height:100%;
                width:1;
                background-color:#003366;
            }
        </style>

        <script type="text/javascript">

            function getXMLObject()  //XML OBJECT
            {
                var xmlHttp = false;
                try {
                    xmlHttp = new ActiveXObject("Msxml2.XMLHTTP")  // For Old Microsoft Browsers
                }
                catch (e) {
                    try {
                        xmlHttp = new ActiveXObject("Microsoft.XMLHTTP")  // For Microsoft IE 6.0+
                    }
                    catch (e2) {
                        xmlHttp = false   // No Browser accepts the XMLHTTP Object then false
                    }
                }
                if (!xmlHttp && typeof XMLHttpRequest != 'undefined') {
                    xmlHttp = new XMLHttpRequest();        //For Mozilla, Opera Browsers
                }
                return xmlHttp;  // Mandatory Statement returning the ajax object created
            }

            var xmlhttp = new getXMLObject();	//xmlhttp holds the ajax object

            function ajaxFunction() {
                var getdate = new Date();  //Used to prevent caching during ajax call
                runtimevalue = document.getElementById("per").value;
                if(xmlhttp) {
                    xmlhttp.open("GET","<%=application.getContextPath()%>/gettime?percentage=" + runtimevalue ,true); //gettime will be the servlet name
                    xmlhttp.onreadystatechange  = handleServerResponse;
                    xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                    xmlhttp.send("percentage=" + runtimevalue);

                }
            }

            function handleServerResponse() {
                if (xmlhttp.readyState == 4) {
                    if(xmlhttp.status == 200) {
                        document.myForm.time.value=xmlhttp.responseText; //Update the HTML Form element
                        document.getElementById('progressBar').style.width = xmlhttp.responseText;
                    }
                    else {
                        alert("Error during AJAX call. Please try again");
                    }
                }
            }
        </script>
        <body>
            <br/>
            <form name="myForm" action="gettime" method="get">
                <table>
                    <tr>
                        <td>Text : </td>
                        <td><input type="textField" id="per"/></td>
                    </tr>
                    <tr>
                        <td>Percentage :</td>
                        <td><input type="text" name="time" /></td>
                    </tr>
                </table>
                <input type="button" onClick="javascript:ajaxFunction();" value="click"/>

                <div id="progressBarContainer">
                    <div id="progressBar"></div>
                </div>
            </form>
        </body>
    </head>
</html>




