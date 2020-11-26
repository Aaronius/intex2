<%@page import="edu.byu.isys413.group1a.intex2.BOs.*"%>
<%@page import="edu.byu.isys413.group1a.intex2.web.*"%>
<%@page import="java.util.*"%>
<jsp:include page="/header.jsp">
  <jsp:param name="title" value="Select Name" />
</jsp:include>

<%
  /**
  * This page displays the rental reservation confirmation.
  */
%>

<%
   String status = request.getAttribute("tooManyRentals").toString();

   // Show denial of reservation if more than three rentals reserved.
   if(status.equals("true")){
%>
   <p>Sorry, you have more than three rentals reserved.</p>
   <%
   }else{
       RentalVideo rentalVideo = (RentalVideo)request.getAttribute("rentalVideo");
   %>
   <p><%=rentalVideo.getVcrtcv().getCv().getTitle()%> has been reserved for one hour.</p>
   <%
   }
   %>

<jsp:include page="/footer.jsp"/>