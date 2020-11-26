package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.MembershipType;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;

/**
 * A singleton object that CRUD's MembershipType objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class MembershipTypeDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static MembershipTypeDAO instance = null;
  
  /** Creates a new instance of PhysicalVideoDAO */
  private MembershipTypeDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized MembershipTypeDAO getInstance() {
    if (instance == null) {
      instance = new MembershipTypeDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Membership Type in the database */
  public MembershipType create() throws DataException {
    MembershipType mt = new MembershipType();
    mt.setObjectAlreadyInDB(false);
    mt.setId(GUID.generate());
    Cache.getInstance().put(mt.getId(), mt);
    return mt;
  }//create
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Membership Type from the database */
  public MembershipType read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (MembershipType)Cache.getInstance().get(id);
    }
    
    Connection conn = ConnectionPool.getInstance().get();
    try {
      return read(id, conn);
    }catch (Exception e) {
      throw new DataException(e.getMessage(), e);
    }finally {
      ConnectionPool.getInstance().release(conn);
    }
  }
  
  /** Internal method to read an existing Membership Type from the database */
  synchronized MembershipType read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (MembershipType)Cache.getInstance().get(id);
    }
    
    PreparedStatement stmt;
    if (id.length() == 2){
      stmt = conn.prepareStatement("SELECT * FROM membershiptype WHERE sku=?");
    } else {
      stmt = conn.prepareStatement("SELECT * FROM membershiptype WHERE id=?");
    }
    
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("MembershipType with id/sku '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** External method to get the sku from a Membership Type using the MembershipType id*/
  public String readById(String id) throws Exception {
    Connection conn = ConnectionPool.getInstance().get();
    PreparedStatement stmt;
    
    stmt = conn.prepareStatement("SELECT sku FROM membershiptype WHERE id=?");
    
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      String sku = rs.getString("sku");
      return sku;
      
    }finally{
      stmt.close();
    }
  }//read
  
  
// Deprecated
  /** Internal method to read an existing txline from the database using and RS id
   * MembershipType readBySku(String sku) throws Exception{
   * Connection conn = ConnectionPool.getInstance().get();
   * PreparedStatement stmt = conn.prepareStatement("SELECT * FROM membershiptype WHERE sku=?");
   *
   * try{
   * stmt.setString(1, sku);
   * ResultSet rs = stmt.executeQuery();
   * if (rs.next()) {
   * return readRecord(rs);
   * }//if
   * throw new DataException("Membership Type with sku '" + sku + "' not found.");
   * }catch (Exception e) {
   * throw new DataException("An error occurred while reading the business object information.", e);
   * }finally{
   * stmt.close();
   * ConnectionPool.getInstance().release(conn);
   * }
   * }//readByRSId*/
  
  /** Internal method to create a Membership Type object from a record */
  synchronized MembershipType readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (MembershipType)Cache.getInstance().get(rs.getString("id"));
    }
    MembershipType memType = new MembershipType();
    memType.setObjectAlreadyInDB(true);
    memType.setId(rs.getString("id"));
    Cache.getInstance().put(memType.getId(), memType);
    memType.setDescription(rs.getString("description"));
    memType.setPrice(Float.parseFloat(rs.getString("price")));
    memType.setNumAllowedOut(Integer.parseInt(rs.getString("numallowed")));
    
    return memType;
  }//readRecord
  
/////////////////////////////////////////////
///   UPDATE methods
  
  /** Saves an existing Membership Type in the database */
  public void save(MembershipType mt) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(mt, conn);
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
  
  /** Internal method to update a Membership Type in the database */
  public void save(MembershipType mt, Connection conn) throws Exception {
    Cache.getInstance().put(mt.getId(), mt);
    if (mt.isObjectAlreadyInDB()) {
      update(mt, conn);
    }else{
      insert(mt, conn);
    }//if
    
  }//save
  
  /** Saves an existing Membership Type to the database */
  private void update(MembershipType mt, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE membershiptype SET description=?, price=?, numallowed=? WHERE id=?");
    try {
      stmt.setString(1, mt.getDescription());
      stmt.setFloat(2, mt.getPrice());
      stmt.setInt(3, mt.getNumAllowedOut());
      stmt.setString(4, mt.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Membership Type into the database */
  private void insert(MembershipType mt, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO membershiptype (id, price, numallowed) VALUES (?, ?, ?)");
    try {
      stmt.setString(1, mt.getDescription());
      stmt.setString(2, mt.getId());
      stmt.setFloat(3, mt.getPrice());
      stmt.setInt(4, mt.getNumAllowedOut());
      stmt.execute();
      mt.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
/////////////////////////////////////////////////
///   DELETE methods
  
  /** Deletes an existing Membership Type from the database */
  public void delete(MembershipType mt) throws DataException {
    delete(mt.getId());
  }
  
  /** Deletes an existing Membership Type from the database, given its id */
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
  
  /** Internal method to delete an existing Membership Type from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM MembershipType where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
}//class
