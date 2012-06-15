package com.mooo.mycoz.db.sql;

import java.util.Date;

public interface ProcessSQL extends SetupSQL{
	
	public void entityFillField(Object entity);

	public String addSQL(Object entity);
	
	public String updateSQL(Object entity);
	
	public String deleteSQL(Object entity);
	
	public String searchSQL(Object entity);
	
	public String countSQL(Object entity);
	
	public String offsetRecordSQL();
	
	public String selfDateSQL(Date date);
}
