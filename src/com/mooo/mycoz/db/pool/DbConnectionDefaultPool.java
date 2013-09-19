/**
 * Copyright (C) 2001 Yasna.com. All rights reserved.
 *
 * ===================================================================
 * The Apache Software License, Version 1.1
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        Yasna.com (http://www.yasna.com)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Yazd" and "Yasna.com" must not be used to
 *    endorse or promote products derived from this software without
 *    prior written permission. For written permission, please
 *    contact yazd@yasna.com.
 *
 * 5. Products derived from this software may not be called "Yazd",
 *    nor may "Yazd" appear in their name, without prior written
 *    permission of Yasna.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL YASNA.COM OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of Yasna.com. For more information
 * on Yasna.com, please see <http://www.yasna.com>.
 */

/**
 * Copyright (C) 2000 CoolServlets.com. All rights reserved.
 *
 * ===================================================================
 * The Apache Software License, Version 1.1
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        CoolServlets.com (http://www.coolservlets.com)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Jive" and "CoolServlets.com" must not be used to
 *    endorse or promote products derived from this software without
 *    prior written permission. For written permission, please
 *    contact webmaster@coolservlets.com.
 *
 * 5. Products derived from this software may not be called "Jive",
 *    nor may "Jive" appear in their name, without prior written
 *    permission of CoolServlets.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL COOLSERVLETS.COM OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of CoolServlets.com. For more information
 * on CoolServlets.com, please see <http://www.coolservlets.com>.
 */

package com.mooo.mycoz.db.pool;

import java.io.IOException;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mooo.mycoz.db.DbConfig;

/**
 * Default Yazd connection provider. It uses the excellent connection pool
 * available from http://www.javaexchange.com. This connection provider is a a
 * good choice unless you can use a container-managed one.
 */
public class DbConnectionDefaultPool extends DbConnectionProvider {
	private static Log log = LogFactory.getLog(DbConnectionDefaultPool.class);

	private ConnectionPool connectionPool = null;
	private Properties props;
	private Properties propDescriptions;

	private Object initLock = new Object();

	public DbConnectionDefaultPool() {
		// this.manager = manager;
		props = new Properties();
		propDescriptions = new Properties();
		// Initialize all property values
		initializeProperties();
		// Load any existing property values
		loadProperties();
	}

	/**
	 * Returns a database connection.
	 */
	public Connection getConnection() {
		if (connectionPool == null) {
			// block until the init has been done
			synchronized (initLock) {
				// if still null, something has gone wrong
				if (connectionPool == null) {
					System.err
							.println("Warning: DbConnectionDefaultPool.getConnection() was "
									+ "called when the internal pool has not been initialized.");
					return null;
				}
			}
		}
		return new ConnectionWrapper(connectionPool.getConnection(),
				connectionPool);
	}

	/**
	 * Starts the pool.
	 */
	protected void start() {
		// acquire lock so that no connections can be returned.
		synchronized (initLock) {
			// Get properties
			String driver = props.getProperty("driver");
			String server = props.getProperty("server");
			String username = props.getProperty("username");
			String password = props.getProperty("password");
			int minConnections = 0, maxConnections = 0;
			double connectionTimeout = 0.0;
			try {
				minConnections = Integer.parseInt(props
						.getProperty("minConnections"));
				maxConnections = Integer.parseInt(props
						.getProperty("maxConnections"));
				connectionTimeout = Double.parseDouble(props
						.getProperty("connectionTimeout"));
			} catch (Exception e) {
				System.err
						.println("Error: could not parse default pool properties. "
								+ "Make sure the values exist and are correct.");
				e.printStackTrace();
				return;
			}
			try {
				connectionPool = new ConnectionPool(driver, server, username,
						password, minConnections, maxConnections,
						connectionTimeout);
			} catch (IOException ioe) {
				System.err.println("Error starting DbConnectionDefaultPool: "
						+ ioe);
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * Restarts the pool to take into account any property changes.
	 */
	protected void restart() {
		// Kill off pool.
		destroy();
		// Reload properties.
		loadProperties();
		// Start a new pool.
		start();
	}

	/**
	 * Destroys the connection pool.
	 */
	protected void destroy() {
		if (connectionPool != null) {
			try {
				connectionPool.destroy(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Release reference to connectionPool
		connectionPool = null;
	}

	/**
	 * Returns the value of a property of the connection provider.
	 * 
	 * @param name
	 *            the name of the property.
	 * @returns the value of the property.
	 */
	public String getProperty(String name) {
		return (String) props.get(name);
	}

	/**
	 * Returns the description of a property of the connection provider.
	 * 
	 * @param name
	 *            the name of the property.
	 * @return the description of the property.
	 */
	public String getPropertyDescription(String name) {
		return (String) propDescriptions.get(name);
	}

	/**
	 * Returns an enumeration of the property names for the connection provider.
	 */
	public Enumeration<?> propertyNames() {
		return props.propertyNames();
	}

	/**
	 * Sets a property of the connection provider. Each provider has a set
	 * number of properties that are determined by the author. Trying to set a
	 * non- existant property will result in an IllegalArgumentException.
	 * 
	 * @param name
	 *            the name of the property to set.
	 * @param value
	 *            the new value for the property.
	 * 
	 */
	public void setProperty(String name, String value) {
		props.put(name, value);
		saveProperties();
	}

	/**
	 * Give default values to all the properties and descriptions.
	 */
	private void initializeProperties() {
		props.put("driver", "");
		props.put("server", "");
		props.put("username", "");
		props.put("password", "");
		props.put("minConnections", "");
		props.put("maxConnections", "");
		props.put("logPath", "");
		props.put("connectionTimeout", "");

		propDescriptions.put("driver",
				"JDBC driver. e.g. 'oracle.jdbc.driver.OracleDriver'");
		propDescriptions
				.put("server",
						"JDBC connect string. e.g. 'jdbc:oracle:thin:@203.92.21.109:1526:orcl'");
		propDescriptions.put("username", "Database username. e.g. 'Scott'");
		propDescriptions.put("password", "Database password. e.g. 'Tiger'");
		propDescriptions
				.put("minConnections",
						"Minimum # of connections to start with in pool. Three is the recommended minimum");
		propDescriptions
				.put("maxConnections",
						"Maximum # of connections in dynamic pool. Fifteen should give good performance for an average load.");
		propDescriptions
				.put("logPath",
						"Absolute path name for log file. e.g. 'c:\\logs\\yazdDbLog.log'");
		propDescriptions.put("connectionTimeout",
				"Time in days between connection resets. e.g. '.5'");
	}

	/**
	 * Load whatever properties that already exist.
	 */
	private void loadProperties() {
		String driver = DbConfig
				.getProperty("DbConnectionDefaultPool.driver");
		String server = DbConfig
				.getProperty("DbConnectionDefaultPool.server");
		String username = DbConfig
				.getProperty("DbConnectionDefaultPool.username");
		String password = DbConfig
				.getProperty("DbConnectionDefaultPool.password");
		String minConnections = DbConfig
				.getProperty("DbConnectionDefaultPool.minConnections");
		String maxConnections = DbConfig
				.getProperty("DbConnectionDefaultPool.maxConnections");
		String logPath = DbConfig
				.getProperty("DbConnectionDefaultPool.logPath");
		String connectionTimeout = DbConfig
				.getProperty("DbConnectionDefaultPool.connectionTimeout");

		if (driver != null) {
			props.setProperty("driver", driver);
		}
		if (server != null) {
			props.setProperty("server", server);
		}
		if (username != null) {
			props.setProperty("username", username);
		}
		if (password != null) {
			props.setProperty("password", password);
		}
		if (minConnections != null) {
			props.setProperty("minConnections", minConnections);
		}
		if (maxConnections != null) {
			props.setProperty("maxConnections", maxConnections);
		}
		if (logPath != null) {
			props.setProperty("logPath", logPath);
		}
		if (connectionTimeout != null) {
			props.setProperty("connectionTimeout", connectionTimeout);
		}
	}

	private void saveProperties() {
		DbConfig.setProperty("DbConnectionDefaultPool.driver",
				props.getProperty("driver"));
		DbConfig.setProperty("DbConnectionDefaultPool.server",
				props.getProperty("server"));
		DbConfig.setProperty("DbConnectionDefaultPool.username",
				props.getProperty("username"));
		DbConfig.setProperty("DbConnectionDefaultPool.password",
				props.getProperty("password"));
		DbConfig.setProperty("DbConnectionDefaultPool.minConnections",
				props.getProperty("minConnections"));
		DbConfig.setProperty("DbConnectionDefaultPool.maxConnections",
				props.getProperty("maxConnections"));
		DbConfig.setProperty("DbConnectionDefaultPool.logPath",
				props.getProperty("logPath"));
		DbConfig.setProperty(
				"DbConnectionDefaultPool.connectionTimeout",
				props.getProperty("connectionTimeout"));
	}

	/**
	 * DbConnectionBroker
	 * 
	 * @version 1.0.11 12/7/99
	 * @author Marc A. Mnich
	 * 
	 *         ---------------------------------------- Modified June 18, 2000
	 *         by Matt Tucker Changes: - New package name, class name to make it
	 *         nice to embed as an internal class. - Source code reformatting. -
	 *         Added more error handling code in constructor, createConn method
	 *         so that more information is given to Yazd users.
	 *         DbConnectionBroker rules! Download it from javaexchange.com
	 *         ----------------------------------------
	 * 
	 *         DbConnectionBroker A servlet-based broker for database
	 *         connections. Creates and manages a pool of database connections.
	 * @version 1.0.11 12/7/99
	 * @author Marc A. Mnich
	 */
	private class ConnectionPool implements Runnable {
		private Thread runner;

		private Connection[] connPool;
		private int[] connStatus;

		private long[] connLockTime, connCreateDate;
		private String[] connID;
		private String dbDriver, dbServer, dbLogin, dbPassword;
		private int currConnections, connLast, /* minConns, */maxConns,
				maxConnMSec;

		// available: set to false on destroy, checked by getConnection()
		private boolean available = true;

		// private PrintWriter log;
		private SQLWarning currSQLWarning;

		/**
		 * Creates a new Connection Broker<br>
		 * dbDriver: JDBC driver. e.g. 'oracle.jdbc.driver.OracleDriver'<br>
		 * dbServer: JDBC connect string. e.g.
		 * 'jdbc:oracle:thin:@203.92.21.109:1526:orcl'<br>
		 * dbLogin: Database login name. e.g. 'Scott'<br>
		 * dbPassword: Database password. e.g. 'Tiger'<br>
		 * minConns: Minimum number of connections to start with.<br>
		 * maxConns: Maximum number of connections in dynamic pool.<br>
		 * logFileString: Absolute path name for log file. e.g.
		 * 'c:\temp\mylog.log' <br>
		 * maxConnTime: Time in days between connection resets. (Reset does a
		 * basic cleanup)<br>
		 */
		public ConnectionPool(String dbDriver, String dbServer, String dbLogin,
				String dbPassword, int minConns, int maxConns,
				double maxConnTime) throws IOException {
			connPool = new Connection[maxConns];
			connStatus = new int[maxConns];
			connLockTime = new long[maxConns];
			connCreateDate = new long[maxConns];
			connID = new String[maxConns];
			currConnections = minConns;
			this.maxConns = maxConns;
			this.dbDriver = dbDriver;
			this.dbServer = dbServer;
			this.dbLogin = dbLogin;
			this.dbPassword = dbPassword;
			maxConnMSec = (int) (maxConnTime * 86400000.0); // 86400 sec/day
			if (maxConnMSec < 30000) { // Recycle no less than 30 seconds.
				maxConnMSec = 30000;
			}

			log.debug("Starting ConnectionPool:");
			log.debug("dbDriver = " + dbDriver);
			log.debug("dbServer = " + dbServer);
			log.debug("dbLogin = " + dbLogin);
			log.debug("minconnections = " + minConns);
			log.debug("maxconnections = " + maxConns);
			log.debug("Total refresh interval = " + maxConnTime + " days");
			log.debug("-----------------------------------------");

			// Initialize the pool of connections with the mininum connections:
			// Problems creating connections may be caused during reboot when
			// the
			// servlet is started before the database is ready. Handle this
			// by waiting and trying again. The loop allows 5 minutes for
			// db reboot.
			boolean connectionsSucceeded = false;
			int dbLoop = 20;

			try {
				for (int i = 1; i < dbLoop; i++) {
					try {
						for (int j = 0; j < currConnections; j++) {
							createConn(j);
						}
						connectionsSucceeded = true;
						break;
					} catch (SQLException e) {
						log.debug("--->Attempt ("
								+ String.valueOf(i)
								+ " of "
								+ String.valueOf(dbLoop)
								+ ") failed to create new connections set at startup: ");
						log.debug("    " + e);
						log.debug("    Will try again in 15 seconds...");
						try {
							Thread.sleep(15000);
						} catch (InterruptedException e1) {
						}
					}
				}
				if (!connectionsSucceeded) { // All attempts at connecting to db
												// exhausted
					log.debug("\r\nAll attempts at connecting to Database exhausted");
					throw new IOException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException();
			}

			// Fire up the background housekeeping thread

			runner = new Thread(this);
			runner.start();

		} // End ConnectionPool()

		/**
		 * Housekeeping thread. Runs in the background with low CPU overhead.
		 * Connections are checked for warnings and closure and are periodically
		 * restarted. This thread is a catchall for corrupted connections and
		 * prevents the buildup of open cursors. (Open cursors result when the
		 * application fails to close a Statement). This method acts as fault
		 * tolerance for bad connection/statement programming.
		 */
		public void run() {
			boolean forever = true;
			Statement stmt = null;
			while (forever) {
				// Get any Warnings on connections and print to event file
				for (int i = 0; i < currConnections; i++) {
					try {
						currSQLWarning = connPool[i].getWarnings();
						if (currSQLWarning != null) {
							log.debug("Warnings on connection "
									+ String.valueOf(i) + " " + currSQLWarning);
							connPool[i].clearWarnings();
						}
					} catch (SQLException e) {
						log.debug("Cannot access Warnings: " + e);
					}
				}

				for (int i = 0; i < currConnections; i++) { // Do for each
															// connection
					long age = System.currentTimeMillis() - connCreateDate[i];

					synchronized (connStatus) {
						if (connStatus[i] > 0) { // In use, catch it next time!
							continue;
						}
						connStatus[i] = 2; // Take offline (2 indicates
											// housekeeping lock)
					}

					try { // Test the connection with createStatement call
						if (age > maxConnMSec) { // Force a reset at the max
													// conn time
							throw new SQLException();
						}

						stmt = connPool[i].createStatement();
						connStatus[i] = 0; // Connection is O.K.
						// log.debug("Connection confirmed for conn = " +
						// String.valueOf(i));

						// Some DBs return an object even if DB is shut down
						if (connPool[i].isClosed()) {
							throw new SQLException();
						}
						// Connection has a problem, restart it
					} catch (SQLException e) {
						try {
							log.debug(new Date().toString()
									+ " ***** Recycling connection "
									+ String.valueOf(i) + ":");

							connPool[i].close();
							createConn(i);
						} catch (SQLException e1) {
							log.debug("Failed: " + e1);
							connStatus[i] = 0; // Can't open, try again next
												// time
						}
					} finally {
						try {
							if (stmt != null) {
								stmt.close();
							}
						} catch (SQLException e1) {
						}
						;
					}
				}

				try {
					Thread.sleep(20000);
				} // Wait 20 seconds for next cycle
				catch (InterruptedException e) {
					// Returning from the run method sets the internal
					// flag referenced by Thread.isAlive() to false.
					// This is required because we don't use stop() to
					// shutdown this thread.
					return;
				}
			}
		} // End run

		/**
		 * This method hands out the connections in round-robin order. This
		 * prevents a faulty connection from locking up an application entirely.
		 * A browser 'refresh' will get the next connection while the faulty
		 * connection is cleaned up by the housekeeping thread.
		 * 
		 * If the min number of threads are ever exhausted, new threads are
		 * added up the the max thread count. Finally, if all threads are in
		 * use, this method waits 2 seconds and tries again, up to ten times.
		 * After that, it returns a null.
		 */
		public Connection getConnection() {

			Connection conn = null;

			if (available) {
				boolean gotOne = false;

				for (int outerloop = 1; outerloop <= 10; outerloop++) {

					try {
						int loop = 0;
						int roundRobin = connLast + 1;
						if (roundRobin >= currConnections)
							roundRobin = 0;

						do {
							synchronized (connStatus) {
								log.debug("connStatus[" + roundRobin + "]:"
										+ connStatus[roundRobin]);

								if ((connStatus[roundRobin] < 1)
										&& (!connPool[roundRobin].isClosed())) {
									conn = connPool[roundRobin];
									connStatus[roundRobin] = 1;
									connLockTime[roundRobin] = System
											.currentTimeMillis();
									connLast = roundRobin;
									gotOne = true;
									break;
								} else {
									loop++;
									roundRobin++;
									if (roundRobin >= currConnections)
										roundRobin = 0;
								}
							}
						} while ((gotOne == false) && (loop < currConnections));
					} catch (SQLException e1) {
					}

					if (gotOne) {
						break;
					} else {
						synchronized (this) { // Add new connections to the pool
							if (currConnections < maxConns) {
								try {
									createConn(currConnections);
									currConnections++;
								} catch (SQLException e) {
									log.debug("Unable to create new connection: "
											+ e);
								}
							}
						}

						try {
							log.debug("-----> Connections Exhausted!  Will wait and try "
									+ "again in loop " + String.valueOf(outerloop));
							
							Thread.sleep(2000);
						} catch (InterruptedException e) {
						}
					}
				} // End of try 10 times loop

			} else {
				log.debug("Unsuccessful getConnection() request during destroy()");
			} // End if(available)

			return conn;
		}

		/**
		 * Returns the local JDBC ID for a connection.
		 */
		public int idOfConnection(Connection conn) {
			int match;
			String tag;

			try {
				tag = conn.toString();
			} catch (NullPointerException e1) {
				tag = "none";
			}

			match = -1;

			for (int i = 0; i < currConnections; i++) {
				if (connID[i].equals(tag)) {
					match = i;
					break;
				}
			}
			return match;
		}

		/**
		 * Frees a connection. Replaces connection back into the main pool for
		 * reuse.
		 */
		public String freeConnection(Connection conn) {
			String res = "";

			int thisconn = idOfConnection(conn);
			if (thisconn >= 0) {
				connStatus[thisconn] = 0;
				res = "freed " + conn.toString();
				log.debug("Freed connection " + String.valueOf(thisconn)
						+ " normal exit: ");
			} else {
				log.debug("----> Could not free connection!!!");
			}

			return res;
		}

		private void createConn(int i) throws SQLException {
			Date now = new Date();
			try {
				Class.forName(dbDriver);
				connPool[i] = DriverManager.getConnection(dbServer, dbLogin,
						dbPassword);
				connStatus[i] = 0;
				connID[i] = connPool[i].toString();
				connLockTime[i] = 0;
				connCreateDate[i] = now.getTime();

				log.debug(now.toString() + "  Opening connection "
						+ String.valueOf(i) + " " + connPool[i].toString()
						+ ":");
			} catch (ClassNotFoundException e2) {
				e2.printStackTrace();
				throw new SQLException(e2.getMessage());
			}
		}

		/**
		 * Shuts down the housekeeping thread and closes all connections in the
		 * pool. Call this method from the destroy() method of the servlet.
		 */

		/**
		 * Multi-phase shutdown. having following sequence:
		 * <OL>
		 * <LI><code>getConnection()</code> will refuse to return connections.
		 * <LI>The housekeeping thread is shut down.<br>
		 * Up to the time of <code>millis</code> milliseconds after shutdown of
		 * the housekeeping thread, <code>freeConnection()</code> can still be
		 * called to return used connections.
		 * <LI>After <code>millis</code> milliseconds after the shutdown of the
		 * housekeeping thread, all connections in the pool are closed.
		 * <LI>If any connections were in use while being closed then a
		 * <code>SQLException</code> is thrown.
		 * <LI>The log is closed.
		 * </OL>
		 * <br>
		 * Call this method from a servlet destroy() method.
		 * 
		 * @param millis
		 *            the time to wait in milliseconds.
		 * @exception SQLException
		 *                if connections were in use after <code>millis</code>.
		 */
		public void destroy(int millis) throws SQLException {

			// Checking for invalid negative arguments is not necessary,
			// Thread.join() does this already in runner.join().

			// Stop issuing connections
			available = false;

			// Shut down the background housekeeping thread
			runner.interrupt();

			// Wait until the housekeeping thread has died.
			try {
				runner.join(millis);
			} catch (InterruptedException e) {
			} // ignore

			// The housekeeping thread could still be running
			// (e.g. if millis is too small). This case is ignored.
			// At worst, this method will throw an exception with the
			// clear indication that the timeout was too short.

			long startTime = System.currentTimeMillis();

			// Wait for freeConnection() to return any connections
			// that are still used at this time.
			int useCount;
			while ((useCount = getUseCount()) > 0
					&& System.currentTimeMillis() - startTime <= millis) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				} // ignore
			}

			// Close all connections, whether safe or not
			for (int i = 0; i < currConnections; i++) {
				try {
					connPool[i].close();
				} catch (SQLException e1) {
					log.debug("Cannot close connections on Destroy");
				}
			}

			if (useCount > 0) {
				// bt-test successful
				String msg = "Unsafe shutdown: Had to close " + useCount
						+ " active DB connections after " + millis + "ms";
				log.debug(msg);
				// Close all open files
				// Throwing following Exception is essential because servlet
				// authors
				// are likely to have their own error logging requirements.
				throw new SQLException(msg);
			}

		}// End destroy()

		/**
		 * Returns the number of connections in use.
		 */
		// This method could be reduced to return a counter that is
		// maintained by all methods that update connStatus.
		// However, it is more efficient to do it this way because:
		// Updating the counter would put an additional burden on the most
		// frequently used methods; in comparison, this method is
		// rarely used (although essential).
		public int getUseCount() {
			int useCount = 0;
			synchronized (connStatus) {
				for (int i = 0; i < currConnections; i++) {
					if (connStatus[i] > 0) { // In use
						useCount++;
					}
				}
			}
			return useCount;
		}// End getUseCount()

	} // End class

	/**
	 * An implementation of the Connection interface that wraps an underlying
	 * Connection object. It releases the connection back to a connection pool
	 * when Connection.close() is called.
	 */
	public class ConnectionWrapper implements Connection {

		private Connection connection;
		private ConnectionPool connectionPool;

		public ConnectionWrapper(Connection connection,
				ConnectionPool connectionPool) {
			this.connection = connection;
			this.connectionPool = connectionPool;
		}

		/**
		 * Instead of closing the underlying connection, we simply release it
		 * back into the pool.
		 */
		public void close() throws SQLException {
			connectionPool.freeConnection(this.connection);
			log.debug("ConnectionWrapper close");

			// Release object references. Any further method calls on the
			// connection will fail.
			connection = null;
			connectionPool = null;
		}

		public String toString() {
			if (connection != null) {
				return connection.toString();
			} else {
				return "CoolServlets connection wrapper";
			}
		}

		@Override
		public Statement createStatement() throws SQLException {
			return connection.createStatement();
		}

		public void setHoldability(int holdability) throws SQLException {
			connection.setHoldability(holdability);
		}

		public int getHoldability() throws SQLException {
			return connection.getHoldability();
		}

		public Savepoint setSavepoint() throws SQLException {
			return connection.setSavepoint();
		}

		public Savepoint setSavepoint(String name) throws SQLException {
			return connection.setSavepoint(name);
		}

		public void rollback(Savepoint savepoint) throws SQLException {
			connection.rollback(savepoint);
		}

		public PreparedStatement prepareStatement(String sql)
				throws SQLException {
			return connection.prepareStatement(sql);
		}

		public CallableStatement prepareCall(String sql) throws SQLException {
			return connection.prepareCall(sql);
		}

		public String nativeSQL(String sql) throws SQLException {
			return connection.nativeSQL(sql);
		}

		public void setAutoCommit(boolean autoCommit) throws SQLException {
			connection.setAutoCommit(autoCommit);
		}

		public boolean getAutoCommit() throws SQLException {
			return connection.getAutoCommit();
		}

		public void commit() throws SQLException {
			connection.commit();
		}

		public void rollback() throws SQLException {
			connection.rollback();
		}

		public boolean isClosed() throws SQLException {
			return connection.isClosed();
		}

		public DatabaseMetaData getMetaData() throws SQLException {
			return connection.getMetaData();
		}

		public void setReadOnly(boolean readOnly) throws SQLException {
			connection.setReadOnly(readOnly);
		}

		public boolean isReadOnly() throws SQLException {
			return connection.isReadOnly();
		}

		public void setCatalog(String catalog) throws SQLException {
			connection.setCatalog(catalog);
		}

		public String getCatalog() throws SQLException {
			return connection.getCatalog();
		}

		public void setTransactionIsolation(int level) throws SQLException {
			connection.setTransactionIsolation(level);
		}

		public int getTransactionIsolation() throws SQLException {
			return connection.getTransactionIsolation();
		}

		public SQLWarning getWarnings() throws SQLException {
			return connection.getWarnings();
		}

		public void clearWarnings() throws SQLException {
			connection.clearWarnings();
		}

		public void releaseSavepoint(Savepoint savepoint) throws SQLException {
			connection.releaseSavepoint(savepoint);
		}

		public Statement createStatement(int resultSetType,
				int resultSetConcurrency) throws SQLException {
			return connection.createStatement(resultSetType,
					resultSetConcurrency);
		}

		public Statement createStatement(int resultSetType,
				int resultSetConcurrency, int resultSetHoldability)
				throws SQLException {
			return connection.createStatement(resultSetType,
					resultSetConcurrency, resultSetHoldability);
		}

		public PreparedStatement prepareStatement(String sql,
				int resultSetType, int resultSetConcurrency)
				throws SQLException {
			return connection.prepareStatement(sql, resultSetType,
					resultSetConcurrency);
		}

		public CallableStatement prepareCall(String sql, int resultSetType,
				int resultSetConcurrency) throws SQLException {
			return connection.prepareCall(sql, resultSetType,
					resultSetConcurrency);
		}

		public Map<String, Class<?>> getTypeMap() throws SQLException {
			return connection.getTypeMap();
		}

		public PreparedStatement prepareStatement(String sql,
				String[] columnNames) throws SQLException {
			return connection.prepareStatement(sql, columnNames);
		}

		public PreparedStatement prepareStatement(String sql,
				int[] columnIndexes) throws SQLException {
			return connection.prepareStatement(sql, columnIndexes);
		}

		public PreparedStatement prepareStatement(String sql,
				int autoGeneratedKeys) throws SQLException {
			return connection.prepareStatement(sql, autoGeneratedKeys);
		}

		public PreparedStatement prepareStatement(String sql,
				int resultSetType, int resultSetConcurrency,
				int resultSetHoldability) throws SQLException {
			return connection.prepareStatement(sql, resultSetType,
					resultSetConcurrency, resultSetHoldability);
		}

		public CallableStatement prepareCall(String sql, int resultSetType,
				int resultSetConcurrency, int resultSetHoldability)
				throws SQLException {
			return connection.prepareCall(sql, resultSetType,
					resultSetConcurrency, resultSetHoldability);
		}

		public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
			connection.setTypeMap(map);
		}

		public <T> T unwrap(Class<T> iface) throws SQLException {
			return null;
		}

		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return false;
		}

		public Clob createClob() throws SQLException {
			return null;
		}

		public Blob createBlob() throws SQLException {
			return null;
		}

		public NClob createNClob() throws SQLException {
			return null;
		}

		public SQLXML createSQLXML() throws SQLException {
			return null;
		}

		public boolean isValid(int timeout) throws SQLException {
			return false;
		}

		public void setClientInfo(String name, String value)
				throws SQLClientInfoException {

		}

		public void setClientInfo(Properties properties)
				throws SQLClientInfoException {

		}

		public String getClientInfo(String name) throws SQLException {
			return null;
		}

		public Properties getClientInfo() throws SQLException {
			return null;
		}

		public Array createArrayOf(String typeName, Object[] elements)
				throws SQLException {
			return null;
		}

		public Struct createStruct(String typeName, Object[] attributes)
				throws SQLException {
			return null;
		}

		public void abort(Executor arg0) throws SQLException {
		}

		public int getNetworkTimeout() throws SQLException {
			return 0;
		}

		public String getSchema() throws SQLException {
			return null;
		}

		public void setNetworkTimeout(Executor arg0, int arg1)
				throws SQLException {
		}

		public void setSchema(String arg0) throws SQLException {
		}

	}
}
