package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.VCRT;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;

/**
 * A singleton object that CRUD's VideoCategoryReleaseType objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class VCRTDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static VCRTDAO instance = null;
  
  /** Creates a new instance of VCRTDAO */
  private VCRTDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized VCRTDAO getInstance() {
    if (instance == null) {
      instance = new VCRTDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new VCRT in the database */
  public VCRT create() throws DataException {
    VCRT vcrt = new VCRT();
    vcrt.setObjectAlreadyInDB(false);
    vcrt.setId(GUID.generate());
    Cache.getInstance().put(vcrt.getId(), vcrt);
    return vcrt;
  }//create
  
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing VCRT from the database */
  public VCRT read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (VCRT)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing VCRT from the database */
  synchronized VCRT read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (VCRT)Cache.getInstance().get(id);
    }
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM VCRT where id=?");
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("VCRT with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to read an existing VCRT from the database */
  public VCRT readByForeign(String videoCategoryId, String releaseTypeId) throws Exception{
    Connection conn = ConnectionPool.getInstance().get();
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM vcrt WHERE vcid=? and rtid=?");
    try{
      stmt.setString(1, videoCategoryId);
      stmt.setString(2, releaseTypeId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("A VCRT with Release Type ID '" + releaseTypeId + "' and Video Category ID '"+ videoCategoryId +"' not found.");
    }finally{
      stmt.close();
      ConnectionPool.getInstance().release(conn);
    }
  }//read
  
  /** Internal method to create a VCRT object from a record */
  synchronized VCRT readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (VCRT)Cache.getInstance().get(rs.getString("id"));
    }
    VCRT vcrt = new VCRT();
    vcrt.setObjectAlreadyInDB(true);
    vcrt.setId(rs.getString("id"));
    Cache.getInstance().put(vcrt.getId(), vcrt);
    vcrt.setPrice(Float.parseFloat(rs.getString("price")));
    vcrt.setDuration(Integer.parseInt(rs.getString("duration")));
    vcrt.setVideoCategory(VideoCategoryDAO.getInstance().read(rs.getString("vcid")));
    vcrt.setReleaseType(ReleaseTypeDAO.getInstance().read(rs.getString("rtid")));
    vcrt.setOverduePrice(Float.parseFloat(rs.getString("overdueprice")));
    return vcrt;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing VCRT in the database */
  public void save(VCRT vcrt) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(vcrt, conn);
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
  
  /** Internal method to update a VCRT in the database */
  public void save(VCRT vcrt, Connection conn) throws Exception {
    Cache.getInstance().put(vcrt.getId(), vcrt);
    if (vcrt.isObjectAlreadyInDB()) {
      update(vcrt, conn);
    }else{
      insert(vcrt, conn);
    }//if
    
  }//save
  
  /** Saves an existing VCRT to the database */
  private void update(VCRT vcrt, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE VCRT SET price=?, duration=?, vcid=?, rtid=?, overdueprice=? WHERE id=?");
    try {
      stmt.setFloat(1, vcrt.getPrice());
      stmt.setInt(2, vcrt.getDuration());
      stmt.setString(3, vcrt.getVideoCategory() == null ? null : vcrt.getVideoCategory().getId());
      stmt.setString(4, vcrt.getReleaseType()== null ? null : vcrt.getReleaseType().getId());
      stmt.setFloat(5, vcrt.getOverduePrice());
      stmt.setString(6, vcrt.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new VCRT into the database */
  private void insert(VCRT vcrt, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO VCRT (id, price, duration, vcid, rtid, overdueprice) VALUES (?, ?, ?, ?, ?, ?)");
    try {
      stmt.setString(1, vcrt.getId());
      stmt.setFloat(2, vcrt.getPrice());
      stmt.setInt(3, vcrt.getDuration());
      stmt.setString(4, vcrt.getVideoCategory() == null ? null : vcrt.getVideoCategory().getId());
      stmt.setString(5, vcrt.getReleaseType()== null ? null : vcrt.getReleaseType().getId());
      stmt.setFloat(6, vcrt.getOverduePrice());
      stmt.execute();
      vcrt.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing VCRT from the database */
  public void delete(VCRT vcrt) throws DataException {
    delete(vcrt.getId());
  }
  
  /** Deletes an existing VCRT from the database, given its id */
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
  
  /** Internal method to delete an existing VCRT from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM VCRT where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//reads
  
}//class
