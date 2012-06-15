package com.mooo.mycoz.db.sql;

import java.util.Date;

public class OracleSQL extends AbstractSQL{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6651865972979304313L;
	
	public String offsetRecordSQL() {
		return " rownum >="+getOffsetRecord()+" AND rownum <="+(getMaxRecords()+getOffsetRecord());
	}
	
	public String selfDateSQL(Date date) {
		return "to_date('"+dformat.format(date) +"','yyyy-MM-dd'),";
	}

}
