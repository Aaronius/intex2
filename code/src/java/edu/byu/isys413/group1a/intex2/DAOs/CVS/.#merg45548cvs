package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.ReleaseType;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A singleton object that CRUD's ReleaseType objects.
 *
 * @author Group 1A
 */
public class ReleaseTypeDAO  {
  
  /////////////////////////////////////////////
  ///   A singleton object
  
  private static ReleaseTypeDAO instance = null;
  
  /** Creates a new instance of ReleaseTypeDAO */
  private ReleaseTypeDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized ReleaseTypeDAO getInstance() {
    if (instance == null) {
      instance = new ReleaseTypeDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Release Type in the database */
  public ReleaseType create() throws DataException {
    ReleaseType rt = new ReleaseType();
    rt.setObjectAlreadyInDB(false);
    rt.setId(GUID.generate());
    Cache.getInstance().put(rt.getId(), rt);
    return rt;
  }//create
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Release Type from the database */
  public ReleaseType read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (ReleaseType)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing Release Type from the database */
  synchronized ReleaseType read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (ReleaseType)Cache.getInstance().get(id);
    }
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM releasetype where id=?");
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("ReleaseType with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to create a Release Type object from a record */
  synchronized ReleaseType readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (ReleaseType)Cache.getInstance().get(rs.getString("id"));
    }
    ReleaseType reltype = new ReleaseType();
    reltype.setObjectAlreadyInDB(true);
    reltype.setId(rs.getString("id"));
    Cache.getInstance().put(reltype.getId(), reltype);
    reltype.setType(rs.getString("rtype"));
    return reltype;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Release Type in the database */
  public void save(ReleaseType rt) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(rt, conn);
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
  
  /** Internal method to update a Release Type in the database */
  public void save(ReleaseType rt, Connection conn) throws Exception {
    Cache.getInstance().put(rt.getId(), rt);
    if (rt.isObjectAlreadyInDB()) {
      update(rt, conn);
    }else{
      insert(rt, conn);
    }//if
    
  }//save
  
  /** Saves an existing Release Type to the database */
  private void update(ReleaseType rt, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE ReleaseType SET rtype=? WHERE id=?");
    try {
      stmt.setString(1, rt.getType());
      stmt.setString(2, rt.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Release Type into the database */
  private void insert(ReleaseType rt, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO ReleaseType (id, rtype) VALUES (?, ?)");
    try {
      stmt.setString(1, rt.getId());
      stmt.setString(2, rt.getType());
      stmt.execute();
      rt.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Release Type from the database */
  public void delete(ReleaseType rt) throws DataException {
    delete(rt.getId());
  }
  
  /** Deletes an existing Release Type from the database, given its id */
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
  
  /** Internal method to delete an existing Release Type from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM ReleaseType where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
  /** Method for getting all Release Types */
  public List getAll() throws Exception{
    List<ReleaseType> list = new ArrayList();
    Connection conn = ConnectionPool.getInstance().get();
    
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM releasetype");
    ResultSet rs = stmt.executeQuery();
    
    while (rs.next()) {
      list.add(readRecord(rs));
    }
    
    return list;
  }
  
}//class
