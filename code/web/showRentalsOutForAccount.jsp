<%@page import="edu.byu.isys413.group1a.intex2.BOs.*"%>
<%@page import="edu.byu.isys413.group1a.intex2.web.*"%>
<%@page import="java.util.*"%>
<jsp:include page="/header.jsp">
  <jsp:param name="title" value="Select Name" />
</jsp:include>

<%
  /**
    * This page displays rentals checked out for a given account.
    */
%>

<%
  List<RentalVideo> rentalVideoList = (List<RentalVideo>)request.getAttribute("rentalVideoList");

  // Ensure page is not being accessed directly
  if (rentalVideoList == null) {
    throw new WebException("You cannot access this page directly.");
  }

  // Ensure page is not being accessed directly
  if (rentalVideoList.size() == 0) {
    out.println("No rental videos are checked out for this account.");
  } else {
    for(RentalVideo rentalVideo: rentalVideoList){
      out.println(rentalVideo.getVcrtcv().getCv().getTitle() + "<br>");
    }
  }
%>

<jsp:include page="/footer.jsp"/>
