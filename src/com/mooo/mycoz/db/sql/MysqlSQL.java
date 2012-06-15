package com.mooo.mycoz.db.sql;

import java.util.Date;

public class MysqlSQL extends AbstractSQL {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8659111122527763888L;

	public String offsetRecordSQL() {
			return " LIMIT "+getOffsetRecord()+","+getMaxRecords();
	}

	public String selfDateSQL(Date date) {
		return "date'"+dformat.format(date) +"',";
	}

}