package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.ConceptualVideo;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A singleton object that CRUD's ConceptualVideo objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class ConceptualVideoDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static ConceptualVideoDAO instance = null;
  
  /** Creates a new instance of ConceptualVideoDAO */
  private ConceptualVideoDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized ConceptualVideoDAO getInstance() {
    if (instance == null) {
      instance = new ConceptualVideoDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new ConceptualVideo in the database */
  public ConceptualVideo create() throws DataException {
    ConceptualVideo cv = new ConceptualVideo();
    cv.setObjectAlreadyInDB(false);
    cv.setId(GUID.generate());
    Cache.getInstance().put(cv.getId(), cv);
    return cv;
  }//create
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Conceptual Video from the database */
  public ConceptualVideo read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (ConceptualVideo)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing Conceptual Video from the database */
  synchronized ConceptualVideo read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (ConceptualVideo)Cache.getInstance().get(id);
    }
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM conceptualvideo where id=?");
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("ConceptualVideo with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to create a Conceptual Video object from a record */
  synchronized ConceptualVideo readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (ConceptualVideo)Cache.getInstance().get(rs.getString("id"));
    }
    ConceptualVideo convideo = new ConceptualVideo();
    convideo.setObjectAlreadyInDB(true);
    convideo.setId(rs.getString("id"));
    Cache.getInstance().put(convideo.getId(), convideo);
    convideo.setTitle(rs.getString("title"));
    return convideo;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Conceptual Video in the database */
  public void save(ConceptualVideo cv) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(cv, conn);
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
  
  /** Internal method to update a Conceptual Video in the database */
  public void save(ConceptualVideo cv, Connection conn) throws Exception {
    Cache.getInstance().put(cv.getId(), cv);
    if (cv.isObjectAlreadyInDB()) {
      update(cv, conn);
    }else{
      insert(cv, conn);
    }//if
    
  }//save
  
  /** Saves an existing Conceptual Video to the database */
  private void update(ConceptualVideo cv, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE ConceptualVideo SET title=? WHERE id=?");
    try {
      stmt.setString(1, cv.getTitle());
      stmt.setString(2, cv.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Conceptual Video into the database */
  private void insert(ConceptualVideo cv, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO ConceptualVideo (id, title) VALUES (?, ?)");
    try {
      stmt.setString(1, cv.getId());
      stmt.setString(2, cv.getTitle());
      stmt.execute();
      cv.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Conceptual Video from the database */
  public void delete(ConceptualVideo cv) throws DataException {
    delete(cv.getId());
  }
  
  /** Deletes an existing Conceptual Video from the database, given its id */
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
  
  /** Internal method to delete an existing Conceptual Video from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM ConceptualVideo where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
  /** Method for getting all rental videos */
  public List<ConceptualVideo> getAll() throws Exception{
    List<ConceptualVideo> list = new ArrayList();
    Connection conn = ConnectionPool.getInstance().get();
    
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM conceptualvideo");
    ResultSet rs = stmt.executeQuery();
    
    while (rs.next()) {
      list.add(readRecord(rs));
    }
    
    return list;
  }
  
  /** Method for getting all rental videos */
  public List<ConceptualVideo> searchByTitle(String title) throws Exception{
    List<ConceptualVideo> list = new ArrayList();
    Connection conn = ConnectionPool.getInstance().get();
    
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM conceptualvideo WHERE LOWER(title) LIKE ?");
    stmt.setString(1, "%" + title.toLowerCase() + "%");
    ResultSet rs = stmt.executeQuery();
    
    while (rs.next()) {
      list.add(readRecord(rs));
    }
    
    return list;
  }
  
  
}//class
