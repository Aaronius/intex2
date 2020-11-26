package edu.byu.isys413.group1a.intex2.BOs;

/**
 * The super-class of all business objects in the program.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public abstract class BusinessObject {
  
  private boolean objectAlreadyInDB = false;
  protected String id = null;
  
  /** Creates a new instance of BusinessObject */
  public BusinessObject() {
  }//constructor
  /** Gets the id */
  public String getId() {
    return id;
  }
  /** Sets the id */
  public void setId(String id) {
    this.id = id;
  }
  /** Checks to see if the object is already in the database */
  public boolean isObjectAlreadyInDB() {
    return objectAlreadyInDB;
  }
  /** Sets the object in the database if it is not already in the database*/
  public void setObjectAlreadyInDB(boolean objectAlreadyInDB) {
    this.objectAlreadyInDB = objectAlreadyInDB;
  }
  
}//class
