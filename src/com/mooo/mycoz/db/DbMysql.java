package com.mooo.mycoz.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.mooo.mycoz.common.StringUtils;
import com.mooo.mycoz.db.pool.DbConnectionManager;
import com.mooo.mycoz.db.sql.MysqlSQL;

public class DbMysql extends MysqlSQL implements DbProcess{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public List<Object>  searchAndRetrieveList(Connection connection,Object entity,boolean noQuery)
			throws SQLException {
		
		if (noQuery) {
			refresh(entity);//entity data fill to execute SQL
		}
		
		List<Object> retrieveList = null;
		String doSql = searchSQL(entity);
		
		System.out.println("searchSQL:" + doSql);

		Connection myConn = null;
		boolean isClose = true;
		Statement stmt = null;

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
			Object bean;
			int type=0;

			while (result.next()) {

				bean = entity.getClass().newInstance();

				for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
					type = rsmd.getColumnType(i);
					
					if(type == Types.TIMESTAMP){
						DbBridgingBean.bindProperty(bean,
								StringUtils.formatHump(rsmd.getColumnName(i),null),
								result.getTimestamp(i));
					}else if(type == Types.DATE){
						DbBridgingBean.bindProperty(bean,
								StringUtils.formatHump(rsmd.getColumnName(i),null),
								result.getDate(i));	
					}else if(type == Types.BIGINT){
						DbBridgingBean.bindProperty(bean,
								StringUtils.formatHump(rsmd.getColumnName(i),null),
								result.getLong(i));	
					}else{
						DbBridgingBean.bindProperty(bean,
								StringUtils.formatHump(rsmd.getColumnName(i),null),
								result.getString(i));	
					}

				}
				retrieveList.add(bean);
			}
			// addCache(doSql, retrieveList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
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
	
	@Override
	public List<Object> searchAndRetrieveList(Object entity,boolean noQuery)
			throws SQLException {
		return searchAndRetrieveList(null,entity,noQuery);
	}
	
	@Override
	public List<Object> searchAndRetrieveList(Object entity)
			throws SQLException {
		return searchAndRetrieveList(null,entity,true);
	}
	
	@Override
	public List<Object> searchAndRetrieveList(Connection connection,
			Object entity) throws SQLException {
		return searchAndRetrieveList(connection,entity,true);
	}
	
	@Override
	public Integer count(Connection connection,Object entity,boolean noQuery) throws SQLException {
		
		if (noQuery) {
			refresh(entity);
		}
		
		String doSql = countSQL(entity);
		System.out.println("countSql:" + doSql);

		Connection myConn = null;
		boolean isClose = true;
		
		Statement stmt = null;
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
			
			if(result.next())
				total = result.getInt(1);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
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
		return total;
	}
	public Integer count(Object entity,boolean noQuery) throws SQLException {
		return count(null,entity,noQuery);
	}
	public Integer count(Object entity) throws SQLException {
		return count(null,entity,true);
	}
	public Integer count(Connection connection,Object entity) throws SQLException {
		return count(connection,entity,true);
	}
	@Override
	public void add(Connection connection,Object entity) throws SQLException {
		Connection myConn = null;
		boolean isClose = true;
		
		Statement stmt = null;
		String doSql = addSQL(entity);
		System.out.println("addSQL:" + doSql);

		try{
			if(connection != null){
				myConn = connection;
				isClose = false;
			} else {
				myConn = DbConnectionManager.getConnection();
				isClose = true;
			}
			
			stmt = myConn.createStatement();
			stmt.execute(doSql);
		}finally {
	
			try {
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
	}
	@Override
	public void add(Object entity) throws SQLException {
		add(null,entity);
	}
	
	@Override
	public void delete(Connection connection,Object entity) throws SQLException {
		refresh(entity);
		
		Connection myConn = null;
		boolean isClose = true;
		
		Statement stmt = null;
		String doSql = deleteSQL(entity);
		try{
			if(connection != null){
				myConn = connection;
				isClose = false;
			} else {
				myConn = DbConnectionManager.getConnection();
				isClose = true;
			}
			
			stmt = myConn.createStatement();
			stmt.execute(doSql);
		}finally {
	
			try {
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
		
	}
	@Override
	public void delete(Object entity) throws SQLException {
		delete(null,entity);
	}

	@Override
	public void update(Connection connection,Object entity) throws SQLException {
		refresh(entity);
		
		Connection myConn = null;
		boolean isClose = true;
		
		Statement stmt = null;
		String doSql = updateSQL(entity);
		try{
			if(connection != null){
				myConn = connection;
				isClose = false;
			} else {
				myConn = DbConnectionManager.getConnection();
				isClose = true;
			}
			
			stmt = myConn.createStatement();
			System.out.println(doSql);
			stmt.execute(doSql);
		}finally {
	
			try {
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
	}
	@Override
	public void update(Object entity) throws SQLException {
		update(null,entity);
	}
	@Override
	public void retrieve(Connection connection,Object entity) throws SQLException {
	
		refresh(entity);
		
		String doSql = searchSQL(entity);
		
		if(doSql==null || doSql.indexOf("WHERE") < 0){
			return;
		}
		
		int ls = doSql.indexOf("LIMIT");
		if(ls>0)
			doSql = doSql.substring(0,doSql.indexOf("LIMIT"));
		
		doSql += " LIMIT 1";
	

		
		System.out.println("doSql:" + doSql);
	
		Connection myConn = null;
		boolean isClose = true;
	
		Statement stmt = null;
	
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
	
			ResultSetMetaData rsmd = result.getMetaData();
			int type=0;
			
			while (result.next()) {
				for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
					type = rsmd.getColumnType(i);
					if(type == Types.TIMESTAMP || type == Types.DATE){
						DbBridgingBean.bindProperty(entity,
								StringUtils.prefixToUpper(rsmd.getColumnName(i),null,true),
								result.getTimestamp(i));
					/*
					if(type == Types.TIMESTAMP){
						DbBridgingBean.bindProperty(entity,
								StringUtils.prefixToUpper(rsmd.getColumnName(i),null,true),
								result.getTimestamp(i));
					}else if(type == Types.DATE){
						DbBridgingBean.bindProperty(entity,
								StringUtils.prefixToUpper(rsmd.getColumnName(i),null,true),
								result.getDate(i));
					*/}else {
						DbBridgingBean.bindProperty(entity,
								StringUtils.prefixToUpper(rsmd.getColumnName(i),null,true),
								result.getString(i));
					}
				}
			}
			
			// addCache(doSql, retrieveList);
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
	}
	@Override
	public void retrieve(Object entity) throws SQLException {
		retrieve(null,entity);
	}

}
