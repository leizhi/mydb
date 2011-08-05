package com.mooo.mycoz.db.sql;


public interface MultiSQLProcess {
 	
	String getCatalog();

	void setCatalog(String catalog);
	
	void addTable(String name, String alias);
	
	void addTable(String catalog, String name, String alias);
	
	void setRetrieveField(String alias, String field);
	
	void setForeignKey(String name, String field, String fName,String fField);
	
	void setField(String field, String value);
	
	void setField(String field, int value);
	
	void setLike(String alias, String field, String value);
	
	void setGreaterEqual(String alias, String field, String value);
	
	void setLessEqual(String alias, String field, String value);
	
 	void setNotEqual(String alias, String field, String value);
 	
 	void addCustomWhereClause(String value);
 	
 	void setGroupBy(String alias, String field);
 	
 	void setOrderBy(String alias, String field, String type);
 	
 	void setOrderBy(String alias, String field);
 	
 	void setRecord(int offset, int rowcount);
 	
 	int count();
}
