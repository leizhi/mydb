package com.mooo.mycoz.db.sql;


import java.text.SimpleDateFormat;
import java.util.Date;

public class MysqlSQL extends AbstractSQL {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8659111122527763888L;

	public void setRecord(Integer offsetRecord, Integer maxRecords) {
			if(offsetRecord < 0)
				offsetRecord = 0;
			
			setLimitBy(new StringBuilder(" LIMIT "+offsetRecord+","+maxRecords));
			setByLimit(true);
	}

	public void refresh(Object entity){
		refresh(entity,true);
	}
	
	public String selfDateSQL(Date date) {
		return "date'"+new SimpleDateFormat("yyyy-MM-dd").format(date) +"',";
	}
}