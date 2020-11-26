<%@page import="edu.byu.isys413.group1a.intex2.BOs.*"%>
<%@page import="edu.byu.isys413.group1a.intex2.web.*"%>
<%@page import="java.util.*"%>
<jsp:include page="/header.jsp">
  <jsp:param name="title" value="Select Name" />
</jsp:include>

<%
 /**
 * This page offers a form so a user can enter a title to search available rental
 * videos.  It will return a list of conceptutal videos.
 */
%>

<div id="video">
<form action="<%=request.getContextPath()%>/edu.byu.isys413.group1a.intex2.actions.GetSearchVideos.action" method="get">
    <%
    // This comes back from GetSearchVideos.action if no match is found
    if (request.getAttribute("noResults") != null){
      out.println("<span style:\"color: red\">Sorry, your search did not return any videos.  Please try again.</span><p>");
    }
    %>
    
    Please enter the video you wish to rent:
    <input type="text" size="20" name="searchTitle">
    <input type="submit" value="Submit">
</form>
</div>

<jsp:include page="/footer.jsp"/>
