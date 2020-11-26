<%@page import="edu.byu.isys413.group1a.intex2.web.WebException" %>
<%
   // if we get to this page, we have a web exception in the request
   // (Tomcat puts it there for us per web.xml settings)
   // let's just make sure it is there
   WebException exc = (WebException)request.getAttribute("javax.servlet.error.exception");
   if (exc == null) {
     throw new JspException("error.jsp cannot be called directly!");
   }
%>

<jsp:include page="/header.jsp">
  <jsp:param name="title" value="Error" />
</jsp:include>

An error has been encountered and is as follows:
<p>

<span style="color:#FF0000"><%=exc.getMessage()%></span>

<jsp:include page="/footer.jsp"/>
