package com.mooo.mycoz.db.sql;

public interface SetupSQL {
	
	public void setField(String fieldName,Object fieldValue,int fieldType,boolean isPrimaryKey);
	
	public void setLike(String fieldName,Object fieldValue);
	
	public void setGreater(String fieldName,Object fieldValue);

	public void setGreaterEqual(String fieldName,Object fieldValue);
	
	public void setLess(String fieldName,Object fieldValue);

	public void setLessEqual(String fieldName,Object fieldValue);
	
	public void setNotEqual(String fieldName,Object fieldValue);

	public void setWhereIn(String fieldName,Object fieldValue);
	
	public void addGroupBy(String fieldName);
	
	public void addOrderBy(String fieldName);
	
	public void setRecord(int offsetRecord, int maxRecords);
}
