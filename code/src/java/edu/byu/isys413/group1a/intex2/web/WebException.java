package edu.byu.isys413.group1a.intex2.web;

import javax.servlet.*;

/**
 * A simple exception to indicate web exceptions.  If you throw
 * this exception from anywhere in a servlet or JSP file,
 * Tomcat will call the error.jsp page (see web.xml).
 *
 * @author Conan C. Albrecht
 */
public class WebException extends ServletException {
  
  /** Creates a new WebException */
  public WebException() {
    super();
  }
  /** Creates a new WebExcpetion with the following text */
  public WebException(String message) {
    super(message);
  }
  /** Creates a new WebException with the following text and a throwable object */
  public WebException(String message, Throwable cause) {
    super(message, cause);
  }
  /** Creates a new WebException with a throwable object */
  public WebException(Throwable cause) {
    super(cause);
  }
  
}
