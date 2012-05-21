package com.mooo.mycoz.db.sql;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OracleSQL extends AbstractSQL{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6651865972979304313L;
	
	public void setRecord(Integer offsetRecord, Integer maxRecords) {
			setLimitBy(new StringBuilder(" rownum >="+offsetRecord+" AND rownum <="+maxRecords+offsetRecord));
			setByLimit(true);
	}
	
	public String selfDateSQL(Date date) {
		return "to_date('"+new SimpleDateFormat("yyyy-MM-dd").format(date) +"','yyyy-MM-dd'),";
	}

}
