<%@page import="edu.byu.isys413.group1a.intex2.BOs.*"%>
<%@page import="edu.byu.isys413.group1a.intex2.web.*"%>
<%@page import="java.util.*"%>
<jsp:include page="/header.jsp">
  <jsp:param name="title" value="Select Name" />
</jsp:include>

<%
 /**
 * This page displays conceptual videos that matched the user's search criteria
 * in a combo box.  The user then selects the match that they were looking for.
 */
%>

<%
   List<ConceptualVideo> conceptualVideos = (List<ConceptualVideo>)request.getAttribute("conceptualVideos");
   
   // Ensure page is not being accessed directly
   if (conceptualVideos == null) {
     throw new WebException("You cannot access this page directly.");
   }
%>
<div id="customer">
<form action="<%=request.getContextPath()%>/edu.byu.isys413.group1a.intex2.actions.GetStoresWithVideo.action" method="get">
    Please select the video and type that you want:
    <select name="conceptualVideoId">
    <% for (ConceptualVideo conceptualVideo: conceptualVideos) { %>
      <option value="<%=conceptualVideo.getId()%>"><%=conceptualVideo.getTitle()%></option>
    <% } %>
    </select>
    <input type="submit" value="Submit">
</form>
</div>
<jsp:include page="/footer.jsp"/>