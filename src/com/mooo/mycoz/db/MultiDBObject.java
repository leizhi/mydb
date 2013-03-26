package com.mooo.mycoz.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mooo.mycoz.common.StringUtils;
import com.mooo.mycoz.db.pool.DbConnectionManager;
import com.mooo.mycoz.db.sql.MysqlMultiSQL;

public class MultiDBObject extends MysqlMultiSQL implements MultiDbProcess {
	
	/**
	 * 
	 */
	public synchronized List<Object> searchAndRetrieveList(Connection connection) throws SQLException{
		long startTime = System.currentTimeMillis();

		List<Object> retrieveList = null;
		String doSql = searchSQL();
		System.out.println("searchSQL->" + doSql);

		Connection myConn = null;
		Statement stmt = null;
		boolean isClose = true;

		try {
			retrieveList = new ArrayList<Object>();
			
			if(connection != null){
				myConn = connection;
				isClose = false;
			} else {
				myConn = DbConnectionManager.getConnection();
				isClose = true;
			}
			
			stmt = myConn.createStatement();
			ResultSet result = stmt.executeQuery(doSql);
			ResultSetMetaData rsmd = result.getMetaData();

			String key;
			String catalog,table,column;
			
			while (result.next()) {
				
				Map<String, Object> allRow = new HashMap<String, Object>();
				for (Entry<String, Class<?>>  entry:objs.entrySet()) {
					key = entry.getKey();
					
					Class<?> cls = objs.get(key);
					Object bean = cls.newInstance();
					
					allRow.put(key, bean);
				}
				
				for (int i=1; i < rsmd.getColumnCount()+1; i++) {
					
					catalog = rsmd.getCatalogName(i);
					table = rsmd.getTableName(i);
					
					column = rsmd.getColumnName(i);

					int type = DbUtil.type(myConn,catalog,table,StringUtils.upperToPrefix(column,null));
					
					if(allRow.containsKey(StringUtils.toLowerFirst(table))){
						Object bean = allRow.get(StringUtils.toLowerFirst(table));
						
						boolean enableCase = new Boolean(DbConfig.getProperty("Db.case"));
						
						if(type == Types.TIMESTAMP){
							DbBridgingBean.bindProperty(bean,StringUtils.prefixToUpper(rsmd.getColumnName(i),null,enableCase),result.getTimestamp(i));
						}else {
							DbBridgingBean.bindProperty(bean,StringUtils.prefixToUpper(rsmd.getColumnName(i),null,enableCase),result.getString(i));	
						}					
					}
				}
				retrieveList.add(allRow);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

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
		
		long finishTime = System.currentTimeMillis();
		long hours = (finishTime - startTime) / 1000 / 60 / 60;
		long minutes = (finishTime - startTime) / 1000 / 60 - hours * 60;
		long seconds = (finishTime - startTime) / 1000 - hours * 60 * 60 - minutes * 60;
		
		System.out.println(finishTime - startTime);
		System.out.println("search expends:   " + hours + ":" + minutes + ":" + seconds);
		return retrieveList;
	}
	
	/**
	 * 
	 */
	public List<Object> searchAndRetrieveList() throws SQLException{
		return searchAndRetrieveList(null);
	}
	
	public int count() {
		return count(null);
	}
	
	public synchronized int count(Connection connection) {
		long startTime = System.currentTimeMillis();

		String doSql = buildCountSQL();
		
		System.out.println("countSQL->" + doSql);

		Connection myConn = null;
		Statement stmt = null;
		boolean isClose = true;

		int total=0;

		try {
			if(connection != null){
				myConn = connection;
				isClose = false;
			} else {
				myConn = DbConnectionManager.getConnection();
				isClose = true;
			}
			
			stmt = myConn.createStatement();
			ResultSet result = stmt.executeQuery(doSql);
			
			while (result.next()) {
				total ++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

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
		long finishTime = System.currentTimeMillis();
		long hours = (finishTime - startTime) / 1000 / 60 / 60;
		long minutes = (finishTime - startTime) / 1000 / 60 - hours * 60;
		long seconds = (finishTime - startTime) / 1000 - hours * 60 * 60 - minutes * 60;
		
		System.out.println(finishTime - startTime);
		System.out.println("count expends:   " + hours + ":" + minutes + ":" + seconds);
		return total;
	}
}
