package edu.byu.isys413.group1a.intex2.Misc;

import java.sql.*;
import java.util.*;

/**
 * Version 1.0
 *
 * A connection pool for database connections.
 * Note that javax.sql has a standard connection
 * pool interface, but this does not implement
 * it.  This is a bare bones connection pool and
 * is not robust or efficient enough to be used
 * in the real world.  It is just for example
 * purposes, and it assumes that we need simplicity
 * over function.
 *
 * @author Conan C. Albrecht <conan@warp.byu.edu>
 */
public class ConnectionPool {
  
  //////////////////////////////////////////////
  ///   Singelton code
  
  /** The singelton instance of the class */
  private static ConnectionPool instance = null;
  
  /** Creates a new instance of ConnectionPool */
  private ConnectionPool() {
  }
  
  /** Returns the singelton instance of the ConnectionPool */
  public static synchronized ConnectionPool getInstance() {
    if (instance == null) {
      instance = new ConnectionPool();
    }//if
    return instance;
  }//getInstance
  
  
  /////////////////////////////////////////////
  ///   Connection factory
  
  private Connection createConnection() throws Exception {
    //Logger.global.info("Creating a new database connection in the pool.");
    //Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
    //Connection conn = DriverManager.getConnection(dbURL);
    DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());

    Connection conn =   DriverManager.getConnection(
                   "jdbc:sqlserver://localhost:1032;database=intex;user=sa;password=passisys"
            );
    //localhost:1032;database=intex;user=sa;password=passisys"
    //"jdbc:sqlserver://10.25.97.29:22;database=intex;user=sa;password=passisys"    
    conn.setAutoCommit(false);
    return conn;
  }//createConnection
  
  
  //////////////////////////////////////////////
  ///   Public methods
  
  List<Connection> freeConnections = new LinkedList<Connection>();
  List<Connection> usedConnections = new LinkedList<Connection>();
  
  /** Returns a connection to the database */
  public synchronized Connection get() throws DataException {
    try {
      // do we have enough connections to assign one out?
      if (freeConnections.size() == 0) {
        freeConnections.add(createConnection());
      }
      
      // return the first free connection
      Connection conn = freeConnections.remove(0);
      usedConnections.add(conn);
      //Logger.global.info("Gave out a connection from the pool.  Free size is now: " + freeConnections.size() + "/" + (freeConnections.size() + usedConnections.size()));
      return conn;
    }catch (Exception e) {
      throw new DataException("An error occurred while retrieving a database connection from the pool", e);
    }
  }//get
  
  /** Releases a connection that was previously in use */
  public synchronized void release(Connection conn) throws DataException {
    try {
      // be sure that this connection was committed (so it is at a fresh, new transaction)
      conn.commit();
      
      // first remove the connection from the used list
      usedConnections.remove(conn);
      
      // next add it back to the free connection list
      freeConnections.add(conn);
      //Logger.global.info("Released a connection back to the pool.  Free size is now: " + freeConnections.size() + "/" + (freeConnections.size() + usedConnections.size()));
    }catch (Exception e) {
      throw new DataException("An error occurred while releasing a database connection back to the pool", e);
    }
  }//release
  
  
  
}//class
