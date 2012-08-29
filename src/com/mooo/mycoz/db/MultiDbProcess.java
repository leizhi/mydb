package com.mooo.mycoz.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.mooo.mycoz.db.sql.ProcessMultiSQL;

public interface MultiDbProcess  extends ProcessMultiSQL {
	
	public List<Object> searchAndRetrieveList(Connection connection) 
			throws SQLException;
	public List<Object> searchAndRetrieveList()
			throws SQLException;

	public int count(Connection connection) throws SQLException;
	public int count() throws SQLException;
}
