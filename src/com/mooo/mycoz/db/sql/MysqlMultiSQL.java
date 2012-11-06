package com.mooo.mycoz.db.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mooo.mycoz.common.StringUtils;

public class MysqlMultiSQL implements ProcessMultiSQL {

	public Map<String,Class<?>> objs;
	public Map<String,String> tables;
	public List<String> whereKey;
	public List<String> retrieveFields;
	public List<String> groupBy;
	public List<String> orderBy;
	public int offsetRecord;
	public int maxRecords;
	
	public MysqlMultiSQL() {
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
		tables.put(alias, getDbName(clazz));
	}
	
	public void addTable(String name, String alias) {
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

	public void setGreaterEqual(String alias, String field, int value) {
		whereKey.add(alias + "." + field + " >= " + value);
	}
	
	public void setGreaterEqual(String alias, String field, String value) {
		whereKey.add(alias + "." + field + " >= '" + value + "'");
	}

	public void setLessEqual(String alias, String field, int value) {
		whereKey.add(alias + "." + field + " <= " + value);
	}
	
	public void setLessEqual(String alias, String field, String value) {
		whereKey.add(alias + "." + field + " <= '" + value + "'");
	}

	public void setNotEqual(String alias, String field, String value) {
		whereKey.add(alias + "." + field + " <> '" + value + "'");
	}

	public void setNotEqual(String alias, String field, int value) {
		whereKey.add(alias + "." + field + " <> " + value);
	}
	
	public void addCustomWhereClause(String value) {
		whereKey.add(value);
	}

	public void setGroupBy(String alias, String field) {
		groupBy.add(alias + "." + field);
	}

	public void setOrderBy(String alias, String field, String type) {
		orderBy.add(alias + "." + field + " " + type);
	}

	public void setOrderBy(String alias, String field) {
		orderBy.add(alias + "." + field);
	}

	public String searchSQL() {
		String key;
		String value;
		String sql = "";
		
		boolean isHead = true;
		
		if (retrieveFields != null && !retrieveFields.isEmpty()) {
			sql += "SELECT ";
			for (Iterator<String> it = retrieveFields.iterator(); it.hasNext();) {
				value = it.next();

				if(isHead) {
					isHead = false;
					sql += value;
				}else{
					sql += ","+value;
				}
			}
		}

		isHead = true;
		if (tables != null && !tables.isEmpty()) {
			sql += " FROM ";
			for (Iterator<?> it = tables.keySet().iterator(); it.hasNext();) {
				key = (String) it.next();
				value = (String) tables.get(key);
				
				if(isHead) {
					isHead = false;
					sql += value +" "+key;
				}else{
					sql +=  ","+value +" "+key;
				}
				
			}
		}

		isHead = true;
		if (whereKey != null && !whereKey.isEmpty()) {
			sql += " WHERE ";
			for (Iterator<String> it = whereKey.iterator(); it.hasNext();) {
				value = it.next();
				if(isHead) {
					isHead = false;
					sql += value;
				}else{
					sql += " AND "+value;
				}
			}
		}

		isHead = true;
		if (groupBy != null && !groupBy.isEmpty()) {
			sql += " GROUP BY ";
			for (Iterator<String> it = groupBy.iterator(); it.hasNext();) {
				value = it.next();
				if(isHead) {
					isHead = false;
					sql += value;
				}else{
					sql += ","+value;
				}
			}
		}

		isHead = true;
		if (orderBy != null && !orderBy.isEmpty()) {
			sql += " ORDER BY ";
			for (Iterator<String> it = orderBy.iterator(); it.hasNext();) {
				value = it.next();
				if(isHead) {
					isHead = false;
					sql += value;
				}else{
					sql += ","+value;
				}
			}
		}

		if (offsetRecord != 0 && maxRecords != 0) {
			sql += " LIMIT " + offsetRecord + "," + maxRecords;

		} else if (maxRecords != 0) {
			sql += " LIMIT " + maxRecords;

		}

		return sql;
	}

	public String buildCountSQL() {
		String searchSQL = searchSQL();

		int vdex = searchSQL.indexOf("LIMIT");
		if(vdex>0){
			searchSQL = searchSQL.substring(0,vdex);
		}
		
		return searchSQL;
	}

	public void setRecord(int offsetRecord, int maxRecords) {
		this.offsetRecord = offsetRecord;
		this.maxRecords = maxRecords;
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
