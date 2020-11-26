package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.VideoCategory;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A singleton object that CRUD's VideoCategory objects.
 *
 * @author Group 1A
 */
public class VideoCategoryDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static VideoCategoryDAO instance = null;
  
  /** Creates a new instance of VideoCategoryDAO */
  private VideoCategoryDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized VideoCategoryDAO getInstance() {
    if (instance == null) {
      instance = new VideoCategoryDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Video Category in the database */
  public VideoCategory create() throws DataException {
    VideoCategory vc = new VideoCategory();
    vc.setObjectAlreadyInDB(false);
    vc.setId(GUID.generate());
    Cache.getInstance().put(vc.getId(), vc);
    return vc;
  }//create
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Video Category from the database */
  public VideoCategory read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (VideoCategory)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing Video Category from the database */
  synchronized VideoCategory read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (VideoCategory)Cache.getInstance().get(id);
    }
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM videoCategory where id=?");
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("VideoCategory with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to create a Video Category object from a record */
  synchronized VideoCategory readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (VideoCategory)Cache.getInstance().get(rs.getString("id"));
    }
    VideoCategory vc = new VideoCategory();
    vc.setObjectAlreadyInDB(true);
    vc.setId(rs.getString("id"));
    Cache.getInstance().put(vc.getId(), vc);
    vc.setCategory(rs.getString("category"));
    return vc;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Video Category in the database */
  public void save(VideoCategory vc) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(vc, conn);
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
  
  /** Internal method to update a Video Category in the database */
  public void save(VideoCategory vc, Connection conn) throws Exception {
    Cache.getInstance().put(vc.getId(), vc);
    if (vc.isObjectAlreadyInDB()) {
      update(vc, conn);
    }else{
      insert(vc, conn);
    }//if
    
  }//save
  
  /** Saves an existing Video Category to the database */
  private void update(VideoCategory vc, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE VideoCategory SET category=? WHERE id=?");
    try {
      stmt.setString(1, vc.getCategory());
      stmt.setString(2, vc.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Video Category into the database */
  private void insert(VideoCategory vc, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO VideoCategory (id, category) VALUES (?, ?)");
    try {
      stmt.setString(1, vc.getId());
      stmt.setString(2, vc.getCategory());
      stmt.execute();
      vc.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Video Category from the database */
  public void delete(VideoCategory vc) throws DataException {
    delete(vc.getId());
  }
  
  /** Deletes an existing Video Category from the database, given its id */
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
  
  /** Internal method to delete an existing Video Category from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM VideoCategory where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
  /** Method for getting all Video Categories */
  public List getAll() throws Exception{
    List<VideoCategory> list = new ArrayList();
    Connection conn = ConnectionPool.getInstance().get();
    
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM videocategory");
    ResultSet rs = stmt.executeQuery();
    
    while (rs.next()) {
      list.add(readRecord(rs));
    }
    
    return list;
  }
  
}//class
