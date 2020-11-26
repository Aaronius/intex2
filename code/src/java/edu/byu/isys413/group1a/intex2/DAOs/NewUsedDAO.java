package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.NewUsed;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;

/**
 * A singleton object that CRUD's NewUsed objects.
 *
 * @author Group 1A
 */
public class NewUsedDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static NewUsedDAO instance = null;
  
  /** Creates a new instance of NewUsedDAO */
  private NewUsedDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized NewUsedDAO getInstance() {
    if (instance == null) {
      instance = new NewUsedDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new New Used in the database */
  public NewUsed create() throws DataException {
    NewUsed nu = new NewUsed();
    nu.setObjectAlreadyInDB(false);
    nu.setId(GUID.generate());
    Cache.getInstance().put(nu.getId(), nu);
    return nu;
  }//create
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing New Used from the database */
  public NewUsed read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (NewUsed)Cache.getInstance().get(id);
    }
    Connection conn = ConnectionPool.getInstance().get();
    try {
      return read(id, conn);
    }catch (Exception e) {
      throw new DataException("An error occurred while reading the business object information.", e);
    }finally {
      ConnectionPool.getInstance().release(conn);
    }
  }
  
  /** Internal method to read an existing New Used from the database */
  synchronized NewUsed read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (NewUsed)Cache.getInstance().get(id);
    }
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM NewUsed where id=?");
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("NewUsed with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to create a New Used object from a record */
  synchronized NewUsed readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (NewUsed)Cache.getInstance().get(rs.getString("id"));
    }
    NewUsed nu = new NewUsed();
    nu.setObjectAlreadyInDB(true);
    nu.setId(rs.getString("id"));
    Cache.getInstance().put(nu.getId(), nu);
    nu.setType(rs.getString("nutype"));
    return nu;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing New Used in the database */
  public void save(NewUsed nu) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(nu, conn);
      conn.commit();
    }catch (Exception e) {
      try{
        conn.rollback();
      }catch (SQLException e2) {
        throw new DataException("Could not roll back the database transaction!", e2);
      }
      throw new DataException("An error occurred while saving the business object information.", e);
    }finally {
      ConnectionPool.getInstance().release(conn);
    }
  }//update
  
  /** Internal method to update a New Used in the database */
  public void save(NewUsed nu, Connection conn) throws Exception {
    Cache.getInstance().put(nu.getId(), nu);
    if (nu.isObjectAlreadyInDB()) {
      update(nu, conn);
    }else{
      insert(nu, conn);
    }//if
    
  }//save
  
  /** Saves an existing New Used to the database */
  private void update(NewUsed nu, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE NewUsed SET nutype=? WHERE id=?");
    try {
      stmt.setString(1, nu.getType());
      stmt.setString(2, nu.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new New Used into the database */
  private void insert(NewUsed nu, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO NewUsed (id, nutype) VALUES (?, ?)");
    try {
      stmt.setString(1, nu.getId());
      stmt.setString(2, nu.getType());
      stmt.execute();
      nu.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing New Used from the database */
  public void delete(NewUsed nu) throws DataException {
    delete(nu.getId());
  }
  
  /** Deletes an existing New Used from the database, given its id */
  public void delete(String id) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      delete(id, conn);
      conn.commit();
    }catch (Exception e) {
      try{
        conn.rollback();
      }catch (SQLException e2) {
        throw new DataException("Could not roll back the database transaction!", e2);
      }
      throw new DataException("An error occurred while deleting the business object information.", e);
    }finally {
      ConnectionPool.getInstance().release(conn);
    }
  }
  
  /** Internal method to delete an existing New Used from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM NewUsed where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
}//class
