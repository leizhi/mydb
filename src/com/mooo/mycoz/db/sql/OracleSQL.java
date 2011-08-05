package com.mooo.mycoz.db.sql;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OracleSQL extends AbstractSQL{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3500897378220229889L;
	
	public void setRecord(Integer offsetRecord, Integer maxRecords) {
			setLimitBy(new StringBuilder(" rownum >="+offsetRecord+" AND rownum <="+maxRecords+offsetRecord));
			setByLimit(true);
	}
	
	public void refresh(Object entity){
		refresh(entity,false);
	}
	
	public String selfDateSQL(Date date) {
		return "to_date('"+new SimpleDateFormat("yyyy-MM-dd").format(date) +"','yyyy-MM-dd'),";
	}

}
