package com.mooo.mycoz.db.sql;

import java.util.Date;

import com.mooo.mycoz.common.CalendarUtils;

public class MysqlSQL extends AbstractSQL {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8659111122527763888L;

	public String offsetRecordSQL() {
			return " LIMIT "+getOffsetRecord()+","+getMaxRecords();
	}

	public String selfDateSQL(Date date) {
		return "date'"+CalendarUtils.dformat(date) +"',";
	}

}