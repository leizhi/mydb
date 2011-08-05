package com.mooo.mycoz.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface DbProcess {
	
	public static final boolean OPEN_QUERY = false; 
	public static final boolean CLOSE_QUERY = true;
	
	List<Object> searchAndRetrieveList(Object entity) throws SQLException;
	List<Object> searchAndRetrieveList(Object entity,boolean noQuery) throws SQLException;

	List<Object> searchAndRetrieveList(Connection connection,Object entity) 
		throws SQLException;
	List<Object> searchAndRetrieveList(Connection connection,Object entity,boolean noQuery) 
	throws SQLException;
	
	Integer count(Object entity) throws SQLException;
	Integer count(Object entity,boolean noQuery) throws SQLException;

	Integer count(Connection connection,Object entity) throws SQLException;
	Integer count(Connection connection,Object entity,boolean noQuery) throws SQLException;
	
	void add(Object entity) throws SQLException;
	void add(Connection connection,Object entity) throws SQLException;

	void delete(Object entity) throws SQLException;
	void delete(Connection connection,Object entity) throws SQLException;

	void update(Object entity) throws SQLException;
	void update(Connection connection,Object entity) throws SQLException;

	void retrieve(Object entity) throws SQLException;
	void retrieve(Connection connection,Object entity) throws SQLException;
	
	void setRecord(Integer offsetRecord, Integer maxRecords);
	
	void refresh(Object entity);

	void setLike(String field);
	void setGreaterEqual(String field);
	void setLessEqual(String field);
	
	void setExtent(String field,Date start,Date end);
	void setExtent(String field,Integer start,Integer end);
	void setExtent(String field,String start,String end);

	void addGroupBy(String field);
	void addOrderBy(String field);
	
	String getCatalog();
	void setCatalog(String catalog);
}
