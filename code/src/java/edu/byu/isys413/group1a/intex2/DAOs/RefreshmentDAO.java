package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.Refreshment;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;

/**
 * A singleton object that CRUD's Refreshment objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class RefreshmentDAO  {
  
  /////////////////////////////////////////////
  ///   A singleton object
  
  private static RefreshmentDAO instance = null;
  
  /** Creates a new instance of RefreshmentDAO */
  private RefreshmentDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized RefreshmentDAO getInstance() {
    if (instance == null) {
      instance = new RefreshmentDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Refreshment in the database */
  public Refreshment create() throws DataException {
    Refreshment refresh = new Refreshment();
    refresh.setObjectAlreadyInDB(false);
    refresh.setId(GUID.generate());
    Cache.getInstance().put(refresh.getId(), refresh);
    return refresh;
  }//create
  
  
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Refreshment from the database */
  public Refreshment read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (Refreshment)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing Refreshment from the database */
  synchronized Refreshment read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (Refreshment)Cache.getInstance().get(id);
    }
    
    PreparedStatement stmt;
    if (id.length() == 12){
      stmt = conn.prepareStatement("SELECT * FROM refreshment WHERE sku=?");
    } else {
      stmt = conn.prepareStatement("SELECT * FROM refreshment WHERE id=?");
    }
    
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("Refreshment with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to create a Refreshment object from a record */
  synchronized Refreshment readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (Refreshment)Cache.getInstance().get(rs.getString("id"));
    }
    Refreshment refresh = new Refreshment();
    refresh.setObjectAlreadyInDB(true);
    refresh.setId(rs.getString("id"));
    Cache.getInstance().put(refresh.getId(), refresh);
    refresh.setSku(rs.getString("sku"));
    refresh.setDescription(rs.getString("name"));
    refresh.setAmount(rs.getFloat("price"));
    return refresh;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Refreshment in the database */
  public void save(Refreshment r) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(r, conn);
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
  
  /** Internal method to update a Refreshment in the database */
  public void save(Refreshment r, Connection conn) throws Exception {
    Cache.getInstance().put(r.getId(), r);
    if (r.isObjectAlreadyInDB()) {
      update(r, conn);
    }else{
      insert(r, conn);
    }//if
    
  }//save
  
  /** Saves an existing Refreshment to the database */
  private void update(Refreshment r, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE Refreshment SET sku=?,name=?, price=? WHERE id=?");
    try {
      stmt.setString(1, r.getSku());
      stmt.setString(2, r.getDescription());
      stmt.setFloat(3, r.getAmount());
      stmt.setString(4, r.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Refreshment into the database */
  private void insert(Refreshment r, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO Refreshment (id, sku, name, price) VALUES (?, ?, ?, ?)");
    try {
      stmt.setString(1, r.getId());
      stmt.setString(2, r.getSku());
      stmt.setString(3, r.getDescription());
      stmt.setFloat(4, r.getAmount());
      stmt.execute();
      r.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Refreshment from the database */
  public void delete(Refreshment r) throws DataException {
    delete(r.getId());
  }
  
  /** Deletes an existing Refreshment from the database, given its id */
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
  
  /** Internal method to delete an existing Refreshment from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM Refreshment where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
  
}//class
