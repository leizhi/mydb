package com.mooo.mycoz.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mooo.mycoz.db.pool.DbConnectionManager;

public class Transaction {
	private static Log log = LogFactory.getLog(Transaction.class);

	private Connection connection;
	//private boolean abortTransaction;
	private boolean supportsTransactions;

	public void start() {
		try {
				connection = DbConnectionManager.getConnection();
				supportsTransactions = connection.getMetaData().supportsTransactions();
				
				if (supportsTransactions) {
					connection.setAutoCommit(false);
				}
		} catch (NullPointerException e) {
			e.printStackTrace();
			if(log.isDebugEnabled())log.debug("NullPointerException :" + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			if(log.isDebugEnabled())log.debug("SQLException:" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isTransactional() {
		return supportsTransactions;
	}

	public Connection getConnection() {
		return connection;
	}

	public void rollback() {
		try {
			if (supportsTransactions) {
				if (connection != null)
					connection.rollback();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			if(log.isDebugEnabled())log.debug("NullPointerException :" + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void commit() {
		try {
			if (supportsTransactions) {
				if (connection != null)
					connection.commit();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			if(log.isDebugEnabled())log.debug("NullPointerException :" + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void end() {
		try {
			if (supportsTransactions) {
					connection.setAutoCommit(true);
			}

			if (connection != null) {
				connection.close();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			if(log.isDebugEnabled())log.debug("NullPointerException :" + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
