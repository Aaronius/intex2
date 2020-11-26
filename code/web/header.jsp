<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title><%=request.getParameter("title")%></title>
      <link href="<%=request.getContextPath()%>/includes/stylesheet.css" rel="stylesheet" type="text/css">
    </head>
    <body>
    
    <div class="container">
      <img src="<%=request.getContextPath()%>/images/pantalla_header.gif">
      <table cellspacing="0" cellpadding="0" border="0" width="820" align="center">
        <tr>
          <td style="width:180px;border-right:1px dashed #cccccc" valign="top">
            <b>NAVIGATION</b><p>
            <a href="<%=request.getContextPath()%>/">Home</a><br>
            <a href="<%=request.getContextPath()%>/searchAvailableVideos.jsp">Search & Reserve Videos</a>
            <a href="<%=request.getContextPath()%>/selectAccountToViewRentalsOut.jsp">View Checked Out Videos</a>
            <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
          </td>
          <td valign="top" style="padding-left:25px">