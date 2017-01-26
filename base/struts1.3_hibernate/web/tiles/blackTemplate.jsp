<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head> 
        <title><tiles:getAsString name="title" ignore="true"/></title>
        <script type="text/javascript" src="index_files/mootools.js"></script>
        <script type="text/javascript" src="index_files/reflection.js"></script>
        <link href="css/template.css" rel="stylesheet" type="text/css"/>
        <link href="css/common.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <div id="container">
            <div id="toptitle">

                <h1><td width="100%" colspan="2" valign="top">
                        <tiles:insert attribute="header"/>
                    </td>Laos &amp; Cambodia</h1>

            </div>
            <div id="description">

                <p> <span class="date">&nbsp; &nbsp;[ februari 2008  / 12 afbeeldingen ]</span></p>

            </div>
            <div id="gallerycontainer">
                <tiles:insert attribute="body"/>
                <div id="footer">
                    <tiles:insert attribute="bottom"/>
                </div>
            </div>
        </div>        
    </body>
</html>

