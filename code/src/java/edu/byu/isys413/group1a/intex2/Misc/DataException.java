package edu.byu.isys413.group1a.intex2.Misc;

/**
 * Signals that some type of data read/write error has occurred.
 *
 * @author conan
 */
public class DataException extends Exception {
  
  /** Creates a new instance of DataException */
  public DataException(String s) {
    super(s);
  }//constructor
  
  /** Creates a new instance of DataException, with an embedded exception */
  public DataException(String s, Throwable t) {
    super(s, t);
  }//constructor
  
}//class
