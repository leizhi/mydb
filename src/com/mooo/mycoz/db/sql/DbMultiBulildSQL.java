package com.mooo.mycoz.db.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mooo.mycoz.common.StringUtils;
import com.mooo.mycoz.db.pool.DbConnectionManager;

public class DbMultiBulildSQL implements MultiSQLProcess {

	public String catalog;
	public Map<String,Class<?>> objs;
	public Map<String,String> tables;
	public List<String> whereKey;
	public List<String> retrieveFields;
	public List<String> groupBy;
	public List<String> orderBy;
	public int offsetRecord;
	public int maxRecords;
	
	
	public DbMultiBulildSQL() {
		catalog = null;
		objs = new HashMap<String,Class<?>>();
		tables = new HashMap<String,String>();
		whereKey = new ArrayList<String>();
		retrieveFields = new ArrayList<String>();
		groupBy = new ArrayList<String>();
		orderBy = new ArrayList<String>();
		offsetRecord = 0;
		maxRecords = 0;
	}
	
	public void clear() {
		catalog = null;
		tables.clear();
		whereKey.clear();
		retrieveFields.clear();
		groupBy.clear();
		orderBy.clear();
		offsetRecord = 0;
		maxRecords = 0;
	}
	public void addTable(Class<?> clazz, String alias) {
		objs.put(alias, clazz);
		
		if (catalog != null)
			tables.put(alias, catalog + "." + clazz.getSimpleName());
		else
			tables.put(alias, getDbName(clazz));
	}
	
	public void addTable(String name, String alias) {
		if (catalog != null)
			tables.put(alias, catalog + "." + name);
		else
			tables.put(alias, name);
	}

	public void addTable(String catalog, String name, String alias) {
		tables.put(alias, catalog + "." + name);
	}

	public void setRetrieveField(String alias, String field) {
		retrieveFields.add(alias + "." + field);
	}

	public void setForeignKey(String name, String field, String fName,
			String fField) {
		whereKey.add(name + "." + field + "=" + fName + "." + fField);
	}

	public void setField(String alias,String field, String value) {
		if (!StringUtils.isNull(value)) {
			whereKey.add(alias+"."+field + "='" + value + "'");
		}
	}
	
	public void setField(String alias,String field, int value) {
			whereKey.add(alias+"."+field + "=" + value);
	}
	
	public void setField(String field, String value) {
		if (!StringUtils.isNull(value)) {
			whereKey.add(field + "='" + value + "'");
		}
	}

	public void setField(String field, int value) {
		whereKey.add(field + "=" + value);
	}

	public void setLike(String alias, String field, String value) {
		if (!StringUtils.isNull(value)) {
			whereKey.add(alias + "." + field + " LIKE '%" + value + "%'");
		}
	}

	public void setGreaterEqual(String alias, String field, String value) {
		whereKey.add(alias + "." + field + " >= '" + value + "'");
	}

	public void setLessEqual(String alias, String field, String value) {
		whereKey.add(alias + "." + field + " <= '" + value + "'");
	}

	public void setNotEqual(String alias, String field, String value) {
		whereKey.add(alias + "." + field + " <> '" + value + "'");
	}

	public void addCustomWhereClause(String value) {
		whereKey.add(value);
	}

	public void setGroupBy(String alias, String field) {
		groupBy.add(alias + "." + field);
	}

	public void setOrderBy(String alias, String field, String type) {
		orderBy.add(field + " " + type);
	}

	public void setOrderBy(String alias, String field) {
		orderBy.add(alias + "." + field);
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String searchSQL() {
		String key;
		String value;
		String sql = "";
		
		if (retrieveFields != null && !retrieveFields.isEmpty()) {
			sql += "SELECT ";
			for (Iterator<String> it = retrieveFields.iterator(); it.hasNext();) {
				value = it.next();
				sql += value + ",";
			}
			sql = sql.substring(0, sql.lastIndexOf(","));

		}

		if (tables != null && !tables.isEmpty()) {
			sql += " FROM ";
			for (Iterator<?> it = tables.keySet().iterator(); it.hasNext();) {
				key = (String) it.next();
				value = (String) tables.get(key);
				sql += value +" "+key+ ",";
			}
			sql = sql.substring(0, sql.lastIndexOf(","));
		}

		if (whereKey != null && !whereKey.isEmpty()) {
			sql += " WHERE ";
			for (Iterator<String> it = whereKey.iterator(); it.hasNext();) {
				value = it.next();
				sql += value + " AND ";
			}
			sql = sql.substring(0, sql.lastIndexOf(" AND "));
		}

		if (groupBy != null && !groupBy.isEmpty()) {
			sql += " GROUP BY ";
			for (Iterator<String> it = groupBy.iterator(); it.hasNext();) {
				value = it.next();
				sql += value + ",";
			}
			sql = sql.substring(0, sql.lastIndexOf(","));
		}

		if (orderBy != null && !orderBy.isEmpty()) {
			sql += " ORDER BY ";
			for (Iterator<String> it = orderBy.iterator(); it.hasNext();) {
				value = it.next();
				sql += value + ",";
			}
			sql = sql.substring(0, sql.lastIndexOf(","));
		}

		if (offsetRecord != 0 && maxRecords != 0) {
			sql += " LIMIT " + offsetRecord + "," + maxRecords;

		} else if (maxRecords != 0) {
			sql += " LIMIT " + maxRecords;

		}

		// clear();

		return sql;
	}

	public String buildCountSQL() {
		String searchSQL = searchSQL();

		String sql = "SELECT COUNT(*) ";
		searchSQL = searchSQL.substring(searchSQL.indexOf("FROM"));
		sql += searchSQL;
		return sql;
	}

	public void setRecord(int offsetRecord, int maxRecords) {
		this.offsetRecord = offsetRecord;
		this.maxRecords = maxRecords;
	}

	public int count() {
		return count(null);
	}
	
	public int count(Connection connection) {
		long startTime = System.currentTimeMillis();

		String doSql = buildCountSQL();
		
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
				total = result.getInt(1);
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
	
	public String getDbName(Class<?> clazz){
		
		String value ="";
		String fillName = clazz.getName();
		String[] packArray=fillName.split("\\.");

		for(int i=packArray.length-2;i<packArray.length;i++)
			value += packArray[i]+".";
		value = value.substring(0, value.length()-1);
		
		return value;
	}

}
