package org.system.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class LocalDbAcess {
	  static final String DB_DRIVER = "org.hsqldb.jdbc.JDBCDriver";
	  public static final int INT_DB_ID_NULL = -1;
	  private static final String INSERT_LOCK_OBJECT = "INSERT_LOCK_OBJECT";
	  private boolean aUpdated = false;
	  private Connection aConnection = null;
	  
	  private static LocalDbAcess aInstance = new LocalDbAcess();
	  
	  public static LocalDbAcess getInstance()
	  {
	    return aInstance;
	  }
	  
	  private String getCryptKey()
	  {
	    return "f14cc324bb74d4ce44e768a56612e9a8";
	  }
	  
	  public synchronized void connect(String path_to_dbfile)
	  throws SQLException, ClassNotFoundException
	  {
	    if ((this.aConnection == null) || (this.aConnection.isClosed()))
	    {
	      String db_user = "sa";
	      String db_password = "";
	      Class.forName("org.hsqldb.jdbc.JDBCDriver");
	      String db_url = "jdbc:hsqldb:file:" + path_to_dbfile + ";crypt_key=" + getCryptKey() + ";crypt_type=blowfish";
	      this.aConnection = DriverManager.getConnection(db_url, db_user, db_password);
	      notifyAll();
	    }
	  }
	  
	  public Connection getConnection()
	  {
	    return getConnection(true);
	  }
	  
	  Connection getConnection(boolean paramBoolean)
	  {
	    if (paramBoolean) {
	      waitForUpdating();
	    }
	    return this.aConnection;
	  }
	  
	  public synchronized void shutdown()
	    throws SQLException
	  {
	    if (this.aConnection != null)
	    {
	      Statement localStatement = null;
	      try
	      {
	        localStatement = this.aConnection.createStatement();
	        localStatement.execute("SHUTDOWN");
	      }
	      finally
	      {
	        if (localStatement != null) {
	          localStatement.close();
	        }
	        this.aConnection.close();
	        this.aConnection = null;
	      }
	    }
	  }
	  
	  private synchronized void waitForUpdating()
	  {
	    while (!this.aUpdated) {
	      try
	      {
	        wait();
	      }
	      catch (InterruptedException localInterruptedException)
	      {
	    	//logger
	      }
	    }
	  }
	  
	  synchronized void updated()
	  {
	    this.aUpdated = true;
	    notifyAll();
	  }
	  
	  public Object getInsertLockObject()
	  {
	    return "INSERT_LOCK_OBJECT";
	  }
}
