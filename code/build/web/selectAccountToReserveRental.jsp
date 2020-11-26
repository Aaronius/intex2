<%@page import="edu.byu.isys413.group1a.intex2.BOs.*"%>
<%@page import="edu.byu.isys413.group1a.intex2.web.*"%>
<%@page import="java.util.*"%>
<jsp:include page="/header.jsp">
  <jsp:param name="title" value="Select Name" />
</jsp:include>

<%
 /**
 * This page offers a form where the users enters his/her account number.
 * The rental video just selected is also passed through this page.
 */
%>

<%
   String rentalVideoId = (String)request.getAttribute("rentalVideoId");
   
   // Ensure page is not being accessed directly
   if (rentalVideoId == null) {
     throw new WebException("You cannot access this page directly.");
   }
%>
<div id="customer">
<%
  // This comes back from ReserveRental.action if no matching account was found.
  if (request.getAttribute("accountNotFound") != null){
     out.println("The given account was not found. Please try again.<p>");
  }
%>
  
<form action="<%=request.getContextPath()%>/edu.byu.isys413.group1a.intex2.actions.ReserveRental.action" method="get">
    Please enter your account number:
    <input type="text" name="accountNumber">
    <input type="hidden" name="rentalVideoId" value="<%=rentalVideoId%>">
    <input type="submit" value="Submit">
</form>
</div>

<jsp:include page="/footer.jsp"/>
