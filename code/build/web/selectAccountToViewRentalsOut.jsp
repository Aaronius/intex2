<%@page import="edu.byu.isys413.group1a.intex2.BOs.*"%>
<%@page import="edu.byu.isys413.group1a.intex2.web.*"%>
<%@page import="java.util.*"%>
<jsp:include page="/header.jsp">
  <jsp:param name="title" value="Select Name" />
</jsp:include>

<%
 /**
 * This page offers a form so a user can enter his/her account number.  With
 * that number we will pull their checked out rental videos.
 */
%>

<div id="video">
<form action="<%=request.getContextPath()%>/edu.byu.isys413.group1a.intex2.actions.GetRentalsOutForAccount.action" method="get">
    <%
    // This comes back from GetRentalsOut.action if no match is found
    if (request.getAttribute("noResults") != null){
      out.println("<span style:\"color: red\">Sorry, the account entered was not found.  Please try again.</span><p>");
    }
    %>
    
    Please enter your account number:
    <input type="text" size="20" name="accountNumber">
    <input type="submit" value="Submit">
</form>
</div>

<jsp:include page="/footer.jsp"/>
