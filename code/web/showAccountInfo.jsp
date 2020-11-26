<%@page import="edu.byu.isys413.group1a.intex2.BOs.*"%>
<%@page import="edu.byu.isys413.group1a.intex2.web.*"%>
<%@page import="java.util.*"%>
<%
   // grab the account information from the request
   Account account = (Account)request.getAttribute("account");
   if (account == null) {
     throw new WebException("You cannot access this page directly.");
   }
%>

<jsp:include page="/header.jsp">
  <jsp:param name="title" value="Account Information" />
</jsp:include>

Welcome to your account information page.  Here's the scoop:
<p>
<table border=1 cellspacing=0 cellpadding=5>
    <tr>
      <td>Account GUID</td>
      <td><%=account.getId()%> <br>(you wouldn't normally show this, of course)</td>
    </tr><tr>
      <td>Credit Card</td>
      <td><%=account.getCcNum()%> <br>(or this :)</td>
    </tr><tr>
      <td>Owner</td>
      <td><%=account.getOwner().getFirstName() + " " + account.getOwner().getLastName()%></td>
    </tr><tr>
      <td>Members</td>
      <td>
        <ul>
          <% for (Customer cust: account.getCustomers()) { %>
          <li><%=cust.getFirstName() + " " + cust.getLastName()%></li>
          <% } %>
        </ul>
      </td>
    </tr>
</table>



<jsp:include page="/footer.jsp"/>
