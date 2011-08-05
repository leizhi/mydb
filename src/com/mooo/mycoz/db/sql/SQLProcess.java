package com.mooo.mycoz.db.sql;

import java.sql.SQLException;
import java.util.Date;

public interface SQLProcess {
	 String getCatalog();
	 void setCatalog(String catalog);

	 String getTable();
	 void setTable(String table);
	
	void setField(String field, String value);
	void setField(String field, Integer value);
	void setField(String field, Double value);
	void setField(String field, Date value,Integer columnType);
	
	void setLike(String field);
	void setGreaterEqual(String field);
	void setLessEqual(String field);

	void addGroupBy(String field);
	void addOrderBy(String field);
	
	void setRecord(Integer offsetRecord, Integer maxRecords);
	
 	String addSQL(Object entity) throws SQLException;
 	String deleteSQL(Object entity) throws SQLException;
 	String updateSQL(Object entity) throws SQLException;
 	String searchSQL(Object entity) throws SQLException;
 	String countSQL(Object entity) throws SQLException;
 	
 	void entityFillField(Object entity);
}
