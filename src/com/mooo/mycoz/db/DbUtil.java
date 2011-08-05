package com.mooo.mycoz.db;

import java.sql.Connection;
import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mooo.mycoz.common.StringUtils;
import com.mooo.mycoz.db.pool.DbConnectionManager;

public class DbUtil {
	
    private static Object initLock = new Object();
    private static HashMap<String,List<String>> primaryKeys;

    public static boolean isPrimaryKey(String table,String field) {
		if (primaryKeys == null) {
			synchronized (initLock) {
				if (primaryKeys == null ) {
					primaryKeys = new HashMap<String,List<String>>();
				}
			}
		}
        
		if(!primaryKeys.containsKey(table)){
			primaryKeys.put(table, primaryKey(null, table));
		}
		
		try {
			if (primaryKeys.containsKey(table)) {
				if (primaryKeys.get(table).contains(field.toLowerCase())){
					return true;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return false;
    }
	
	public static List<String> primaryKey(Connection connection,String table) {
		
		List<String> retrieveList = null;
		
		Connection myConn = null;
		boolean isClose = true;
		
		Statement stmt = null;
		ResultSet result = null;
		try {
			retrieveList = new ArrayList<String>();

			if(connection != null){
				myConn = connection;
				isClose = false;
			} else {
				myConn = DbConnectionManager.getConnection();
				isClose = true;
			}

			result = myConn.getMetaData().getPrimaryKeys(null,null, StringUtils.upperToPrefix(table, null));
			
			while (result.next()) {
				retrieveList.add(result.getString(4).toLowerCase());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (result != null)
					result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try {
				if(isClose)
					myConn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		
		return retrieveList;

	}
	
	public static int type(Connection connection,String catalog,String table,String column) {
		
		Connection myConn = null;
		boolean isClose = true;
		
		try {
			if(connection != null){
				myConn = connection;
				isClose = false;
			} else {
				myConn = DbConnectionManager.getConnection();
				isClose = true;
			}

			ResultSet result = myConn.getMetaData().getColumns(catalog, null, table,column);
			
			if(result.next()){
				return result.getInt(5);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if(isClose)
					myConn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return 0;
	}
}
