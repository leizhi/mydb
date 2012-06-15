package com.mooo.mycoz.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.mooo.mycoz.db.sql.SetupSQL;

public interface DbProcess  extends SetupSQL {
	
	public List<Object> searchAndRetrieveList(Connection connection) 
			throws SQLException;
	public List<Object> searchAndRetrieveList()
			throws SQLException;

	public int count(Connection connection) throws SQLException;
	public int count() throws SQLException;

	public void add(Connection connection) throws SQLException;
	public void add() throws SQLException;

	public void delete(Connection connection) throws SQLException;
	public void delete() throws SQLException;

	public void update(Connection connection) throws SQLException;
	public void update() throws SQLException;

	public void retrieve(Connection connection) throws SQLException;
	public void retrieve() throws SQLException;
}
