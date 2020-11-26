package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.JournalEntry;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;
import java.util.Calendar;

/**
 * A singleton object that CRUD's JournalEntry objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class JournalEntryDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static JournalEntryDAO instance = null;
  
  /** Creates a new instance of JournalEntryDAO */
  private JournalEntryDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized JournalEntryDAO getInstance() {
    if (instance == null) {
      instance = new JournalEntryDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Journal Entry business object */
  public JournalEntry create() throws DataException {
    JournalEntry journalEntry = new JournalEntry();
    journalEntry.setObjectAlreadyInDB(false);
    journalEntry.setId(GUID.generate());
    Cache.getInstance().put(journalEntry.getId(), journalEntry);
    return journalEntry;
  }//create
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Journal Entry from the database */
  public JournalEntry read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (JournalEntry)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing GL Account from the database */
  synchronized JournalEntry read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (JournalEntry)Cache.getInstance().get(id);
    }
    
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM journalentry WHERE id=?");
    
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("Journal Entry with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to create a journal entry object from a record */
  synchronized JournalEntry readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (JournalEntry)Cache.getInstance().get(rs.getString("id"));
    }
    JournalEntry journalEntry = new JournalEntry();
    journalEntry.setObjectAlreadyInDB(true);
    journalEntry.setId(rs.getString("id"));
    Cache.getInstance().put(journalEntry.getId(), journalEntry);
    journalEntry.setTx(TxDAO.getInstance().read(rs.getString("txid")));
    journalEntry.setGlaccount(rs.getString("glaccount"));
    journalEntry.setDorc(rs.getString("dorc"));
    journalEntry.setAmount(rs.getFloat("amount"));
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(rs.getLong("jedate"));
    journalEntry.setDate(cal);
    return journalEntry;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Journal Entry in the database */
  public void save(JournalEntry journalEntry) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(journalEntry, conn);
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
  
  /** Internal method to update a Journal Entry in the database */
  public void save(JournalEntry journalEntry, Connection conn) throws Exception {
    Cache.getInstance().put(journalEntry.getId(), journalEntry);
    if (journalEntry.isObjectAlreadyInDB()) {
      update(journalEntry, conn);
    }else{
      insert(journalEntry, conn);
    }//if
    
  }//save
  
  /** Saves an existing Journal Entry to the database */
  private void update(JournalEntry journalEntry, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE journalentry SET txid=?, glid=?, dorc=?, amount=?, jedate=? WHERE id=?");
    try {
      stmt.setString(1, journalEntry.getTx().getId());
      stmt.setString(2, journalEntry.getGlaccount());
      stmt.setString(3, journalEntry.getDorc());
      stmt.setFloat(4, journalEntry.getAmount());
      stmt.setLong(5, journalEntry.getDate().getTimeInMillis());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Journal Entry into the database */
  private void insert(JournalEntry journalEntry, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO journalentry (id, txid, glaccount, dorc, amount, jedate) VALUES (?, ?, ?, ?, ?, ?)");
    try {
      stmt.setString(1, journalEntry.getId());
      stmt.setString(2, journalEntry.getTx().getId());
      stmt.setString(3, journalEntry.getGlaccount());
      stmt.setString(4, journalEntry.getDorc());
      stmt.setFloat(5, journalEntry.getAmount());
      stmt.setLong(6, journalEntry.getDate().getTimeInMillis());
      stmt.execute();
      journalEntry.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing journal entry from the database */
  public void delete(JournalEntry journalEntry) throws DataException {
    delete(journalEntry.getId());
  }
  
  /** Deletes an existing journal entry from the database, given its id */
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
  
  /** Internal method to delete an existing journal entry from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM journalEntry where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
}