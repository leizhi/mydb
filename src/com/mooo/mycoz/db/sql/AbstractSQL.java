package com.mooo.mycoz.db.sql;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mooo.mycoz.common.ReflectUtil;
import com.mooo.mycoz.common.StringUtils;
import com.mooo.mycoz.db.DbConfig;
import com.mooo.mycoz.db.DbUtil;
import com.mooo.mycoz.db.ExtentField;
import com.mooo.mycoz.db.Field;

public abstract class AbstractSQL implements SQLProcess, Serializable{
	
	private static Log log = LogFactory.getLog(AbstractSQL.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 5695615314838758248L;
	
	private String catalog;
	private String table;

	private boolean byWhere;
	private boolean byGroup;
	private boolean byOrder;
	private boolean byLimit;

	private StringBuilder whereBy;
	private StringBuilder groupBy;
	private StringBuilder orderBy;
	private StringBuilder limitBy;

	private boolean isSave;
	private boolean isUpdate;
	private boolean isSearch;

	private StringBuilder saveKey;
	private StringBuilder saveValue;
	private StringBuilder saveSql;
	
	private StringBuilder updateSql;
	private StringBuilder deleteSql;
	private StringBuilder searchSql;
	private StringBuilder countSql;

	private Map<String, Field> fields;
	private Map<String, Object> columnValues;
	
	private Map<String, ExtentField<?>> extentValues;

	private String prefix;
	private boolean enableCase;

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}
	
	public boolean isByLimit() {
		return byLimit;
	}

	public void setByLimit(boolean byLimit) {
		this.byLimit = byLimit;
	}

	public StringBuilder getLimitBy() {
		return limitBy;
	}

	public void setLimitBy(StringBuilder limitBy) {
		this.limitBy = limitBy;
	}

	public Map<String, Object> getColumnValues() {
		return columnValues;
	}

	public void setColumnValues(Map<String, Object> columnValues) {
		this.columnValues = columnValues;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public boolean isEnableCase() {
		return enableCase;
	}

	public void setEnableCase(boolean enableCase) {
		this.enableCase = enableCase;
	}
	
	abstract public void refresh(Object entity);
	
	public void refresh(Object entity,boolean sc){
		prefix = DbConfig.getProperty("Db.humpInterval");
		
		if(prefix !=null && prefix.equals("case")){
			prefix = null;
		}
		
		enableCase = DbConfig.getProperty("Db.case").equals("true");
		
		if(sc){
			if(!StringUtils.isNull(catalog)){
				refresh(catalog,
						StringUtils.upperToPrefix(entity.getClass().getSimpleName(),prefix));
			}else{
				refresh(StringUtils.getCatalog(entity.getClass(),1),
						StringUtils.upperToPrefix(entity.getClass().getSimpleName(),prefix));
			}
		}else{
			refresh(null,
					StringUtils.upperToPrefix(entity.getClass().getSimpleName(),prefix));
		}
		
		entityFillField(entity);
	}
	
	public void refresh(String catalog,String table){
		
		this.catalog = catalog;
		this.table = table;
		
		byWhere = false;
		byGroup = false;
		byOrder = false;
		byLimit = false;

		whereBy = new StringBuilder(" WHERE ");
		groupBy = new StringBuilder(" GROUP BY ");
		orderBy = new StringBuilder(" ORDER BY ");
		
		isSave = false;
		isUpdate = false;
		isSearch = true; // default search

		saveKey = new StringBuilder("(");
		saveValue = new StringBuilder(") VALUES(");

		saveSql = new StringBuilder("INSERT INTO ");

		updateSql = new StringBuilder("UPDATE ");
		deleteSql = new StringBuilder("DELETE FROM ");
		searchSql = new StringBuilder("SELECT * FROM ");
		countSql = new StringBuilder("SELECT COUNT(*) AS total FROM ");

		if(catalog != null) {
			saveSql.append(catalog + ".");
			updateSql.append(catalog + ".");
			deleteSql.append(catalog + ".");
			searchSql.append(catalog + ".");
			countSql.append(catalog + ".");
		}
		
		saveSql.append(table);
		updateSql.append(table + " SET ");
		deleteSql.append(table);
		searchSql.append(table);
		countSql.append(table);

		fields = new HashMap<String, Field>();
		columnValues = new HashMap<String, Object>();
		
		extentValues = new HashMap<String, ExtentField<?>>();
	}
	
	public void refreshSQL(){
		whereBy = new StringBuilder(" WHERE ");
		groupBy = new StringBuilder(" GROUP BY ");
		orderBy = new StringBuilder(" ORDER BY ");
		
		saveKey = new StringBuilder("(");
		saveValue = new StringBuilder(") VALUES(");

		saveSql = new StringBuilder("INSERT INTO ");

		updateSql = new StringBuilder("UPDATE ");
		deleteSql = new StringBuilder("DELETE FROM ");
		searchSql = new StringBuilder("SELECT * FROM ");
		countSql = new StringBuilder("SELECT COUNT(*) AS total FROM ");

		if(catalog != null) {
			saveSql.append(catalog + ".");
			updateSql.append(catalog + ".");
			deleteSql.append(catalog + ".");
			searchSql.append(catalog + ".");
			countSql.append(catalog + ".");
		}
		
		saveSql.append(table);
		updateSql.append(table + " SET ");
		deleteSql.append(table);
		searchSql.append(table);
		countSql.append(table);
	}
	
	///////////////////////////////
	public void setField(String field, String value) {
		try {
			if (field == null || value == null)
				new Exception("set value is null");

			fields.put(field, new Field(field,Types.VARCHAR));
			columnValues.put(field, value);
		} catch (Exception e) {
		}
	}

	public void setField(String field, Integer value) {
		try {
			if (field == null || value == null)
				new Exception("set value is null");

			fields.put(field, new Field(field,Types.INTEGER));
			columnValues.put(field, value);
		} catch (Exception e) {
		}
	}

	public void setField(String field, Double value) {
		try {
			if (field == null || value == null)
				new Exception("set value is null");

			fields.put(field, new Field(field,Types.DOUBLE));
			columnValues.put(field, value);
		} catch (Exception e) {
		}
	}

	public void setField(String field, Date value,Integer columnType) {
		try {
			if (field == null || value == null)
				throw new Exception("set value is null");

			fields.put(field, new Field(field,columnType));
			columnValues.put(field, value);
			
		} catch (Exception e) {
		}
	}
	
	public void setLike(String field) {
		if (fields.containsKey(field)) {
			Field likeField = (Field) fields.get(field);
			likeField.setWhereByLike(true);
			likeField.setWhereByEqual(false);
			likeField.setWhereByGreaterEqual(false);
			likeField.setWhereByLessEqual(false);
		}
	}
	
	public void setGreaterEqual(String field) {
		if (fields.containsKey(field)) {
			Field geField = (Field) fields.get(field);
			geField.setWhereByLike(false);
			geField.setWhereByEqual(false);
			geField.setWhereByGreaterEqual(true);
			geField.setWhereByLessEqual(false);
		}
	}
	
	public void setLessEqual(String field) {
		if (fields.containsKey(field)) {
			Field leField = (Field) fields.get(field);
			leField.setWhereByLike(false);
			leField.setWhereByEqual(false);
			leField.setWhereByGreaterEqual(false);
			leField.setWhereByLessEqual(true);
		}
	}

	public void setExtent(String field,Date start,Date end) {
		if (extentValues != null) {
			extentValues.put(field, new ExtentField<Date>(start,end));
			
			if (fields.containsKey(field)) {
				Field leField = (Field) fields.get(field);
				leField.setWhereByLike(false);
				leField.setWhereByEqual(false);
				leField.setWhereByGreaterEqual(false);
				leField.setWhereByLessEqual(false);
			}
		}
	}
	
	public void setExtent(String field,Integer start,Integer end) {
		if (extentValues != null) {
			extentValues.put(field, new ExtentField<Integer>(start,end));
			
			if (fields.containsKey(field)) {
				Field leField = (Field) fields.get(field);
				leField.setWhereByLike(false);
				leField.setWhereByEqual(false);
				leField.setWhereByGreaterEqual(false);
				leField.setWhereByLessEqual(false);
			}
		}
	}
	
	public void setExtent(String field,String start,String end) {
		if (extentValues != null) {
			extentValues.put(field, new ExtentField<String>(start,end));
			
			if (fields.containsKey(field)) {
				Field leField = (Field) fields.get(field);
				leField.setWhereByLike(false);
				leField.setWhereByEqual(false);
				leField.setWhereByGreaterEqual(false);
				leField.setWhereByLessEqual(false);
			}
		}
	}
	
	@Override
	public void addGroupBy(String groupField) {
		groupBy.append(groupField+",");
		byGroup = true;
	}
	
	@Override
	public void addOrderBy(String orderField) {
		orderBy.append(orderField+",");
		byOrder = true;
	}
	
	abstract public String selfDateSQL(Date date);

	public String addSQL(Object entity) {
		refresh(entity);
		
		if(fields == null || columnValues == null)
			return null;
		
		Field field;
		String key;
		
		for (Iterator<?> it = fields.keySet().iterator(); it.hasNext();) {
			key = (String) it.next();
			field = (Field) fields.get(key);
			
			Object obj = columnValues.get(key);

			if(field.isSave()) {
				isSave = true;
				saveKey.append(field.getName()+",");
				
				if(obj.getClass().isAssignableFrom(Integer.class)){
					saveValue.append(obj+",");
				}else if(obj.getClass().isAssignableFrom(String.class)){
					saveValue.append("'"+obj+"',");
				}else if(obj.getClass().isAssignableFrom(Date.class)){
					if(field.getType()==Types.TIMESTAMP){
						saveValue.append("date'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((Date)obj)) +"',");
					} else {
						saveValue.append(selfDateSQL((Date)obj));
					}
				}else if(obj.getClass().isAssignableFrom(Double.class)){
					saveValue.append(obj+",");
				}
			}
			//if(log.isDebugEnabled())log.debug(field.getName()+"="+value);
		}
		
		if(isSave){
			saveKey.deleteCharAt(saveKey.lastIndexOf(","));
			saveValue.deleteCharAt(saveValue.lastIndexOf(","));
			saveValue.append(")");
			
			saveSql.append(saveKey);
			saveSql.append(saveValue);
		}

		if(log.isDebugEnabled())log.debug("saveSql="+saveSql);

		return saveSql.toString();
	}

	public String deleteSQL(Object entity) {
		if(fields == null || columnValues == null)
			return null;
		
		Field field;
		String key;
		
		for (Iterator<?> it = fields.keySet().iterator(); it.hasNext();) {
			key = (String) it.next();
			field = (Field) fields.get(key);
			
			Object obj = columnValues.get(key);

			if(field.isWhereByEqual()) {
				byWhere = true;
				
				if(obj.getClass().isAssignableFrom(Integer.class)){
					whereBy.append(field.getName()+" = "+obj +" AND ");
				}else if(obj.getClass().isAssignableFrom(Date.class)){
					whereBy.append(field.getName()+" = date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"' AND ");
				}else if(obj.getClass().isAssignableFrom(Double.class)){
					whereBy.append(field.getName()+" = "+obj +" AND ");
				} else {
					whereBy.append(field.getName()+" = '"+obj +"' AND ");
				}
			}
			
			if(field.isWhereByGreaterEqual()) {
				byWhere = true;
				
				if(obj.getClass().isAssignableFrom(Integer.class)){
					whereBy.append(field.getName()+" >= "+obj +" AND ");
				}else if(obj.getClass().isAssignableFrom(Date.class)){
					whereBy.append(field.getName()+" >= date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"' AND ");
				}else if(obj.getClass().isAssignableFrom(Double.class)){
					whereBy.append(field.getName()+" >= "+obj +" AND ");
				} else {
					whereBy.append(field.getName()+" >= '"+obj +"' AND ");
				}
			}

			if(field.isWhereByLessEqual()) {
				byWhere = true;

				if(obj.getClass().isAssignableFrom(Integer.class)){
					whereBy.append(field.getName()+" <= "+obj +" AND ");
				}else if(obj.getClass().isAssignableFrom(Date.class)){
					whereBy.append(field.getName()+" <= date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"' AND ");
				}else if(obj.getClass().isAssignableFrom(Double.class)){
					whereBy.append(field.getName()+" <= "+obj +" AND ");
				} else {
					whereBy.append(field.getName()+" <= '"+obj +"' AND ");
				}
			}
	
			if(log.isDebugEnabled())log.debug("whereBy="+whereBy);
		}
		
		if(byWhere)
			whereBy.delete(whereBy.lastIndexOf("AND"),whereBy.lastIndexOf("AND")+3);

		if(byWhere)
			deleteSql.append(whereBy);
		
		if(log.isDebugEnabled())log.debug("deleteSql="+deleteSql);

		return deleteSql.toString();
	}

	public String updateSQL(Object entity) {
		if(fields == null || columnValues == null)
			return null;
		
		Field field;
		String key;
		
		for (Iterator<?> it = fields.keySet().iterator(); it.hasNext();) {
			key = (String) it.next();
			field = (Field) fields.get(key);
			
			Object obj = columnValues.get(key);
		
			if(field.isUpdate()) {
				isUpdate = true;
				if (DbUtil.isPrimaryKey(this.getTable(),field.getName())) {
					byWhere = true;
					
					if(obj.getClass().isAssignableFrom(Integer.class)){
						whereBy.append(field.getName()+" = "+obj +" AND ");
					}else if(obj.getClass().isAssignableFrom(Date.class)){
						if(field.getType()==Types.TIMESTAMP){
							whereBy.append(field.getName()+" = date'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((Date)obj)) +"' AND ");
						} else {
							whereBy.append(field.getName()+" = date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"' AND ");
						}
					}else if(obj.getClass().isAssignableFrom(Double.class)){
						whereBy.append(field.getName()+" = "+obj +" AND ");
					} else {
						whereBy.append(field.getName()+" = '"+obj +"' AND ");
					}
					
					continue;
				}

				if(obj.getClass().isAssignableFrom(Integer.class)){
					updateSql.append(field.getName()+" = "+obj +",");
				}else if(obj.getClass().isAssignableFrom(Date.class)){
					if(field.getType()==Types.TIMESTAMP){
						updateSql.append(field.getName()+" = date'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((Date)obj)) +"',");
					} else {
						updateSql.append(field.getName()+" = date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"',");
					}
				}else if(obj.getClass().isAssignableFrom(Double.class)){
					updateSql.append(field.getName()+" = "+obj +",");
				} else {
					updateSql.append(field.getName()+" = '"+obj +"',");
				}

			}
			
			// where build
			/*
			if(field.isWhereByEqual()) {
				byWhere = true;
				
				if(obj.getClass().isAssignableFrom(Integer.class)){
					whereBy.append(field.getName()+" = "+obj +" AND ");
				}else if(obj.getClass().isAssignableFrom(Date.class)){
					whereBy.append(field.getName()+" = date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"' AND ");
				}else if(obj.getClass().isAssignableFrom(Double.class)){
					whereBy.append(field.getName()+" = "+obj +" AND ");
				} else {
					whereBy.append(field.getName()+" = '"+obj +"' AND ");
				}
			}
			
			if(field.isWhereByGreaterEqual()) {
				byWhere = true;
				
				if(obj.getClass().isAssignableFrom(Integer.class)){
					whereBy.append(field.getName()+" >= "+obj +" AND ");
				}else if(obj.getClass().isAssignableFrom(Date.class)){
					whereBy.append(field.getName()+" >= date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"' AND ");
				}else if(obj.getClass().isAssignableFrom(Double.class)){
					whereBy.append(field.getName()+" >= "+obj +" AND ");
				} else {
					whereBy.append(field.getName()+" >= '"+obj +"' AND ");
				}
			}

			if(field.isWhereByLessEqual()) {
				byWhere = true;

				if(obj.getClass().isAssignableFrom(Integer.class)){
					whereBy.append(field.getName()+" <= "+obj +" AND ");
				}else if(obj.getClass().isAssignableFrom(Date.class)){
					whereBy.append(field.getName()+" <= date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"' AND ");
				}else if(obj.getClass().isAssignableFrom(Double.class)){
					whereBy.append(field.getName()+" <= "+obj +" AND ");
				} else {
					whereBy.append(field.getName()+" <= '"+obj +"' AND ");
				}
			} */
			// build end
			
		}
		
		if(byWhere)
			whereBy.delete(whereBy.lastIndexOf("AND"),whereBy.lastIndexOf("AND")+3);
			
		if(isUpdate){
			updateSql.deleteCharAt(updateSql.lastIndexOf(","));
			
			if(byWhere)
				updateSql.append(whereBy);
		}

		if(log.isDebugEnabled())log.debug("updateSql="+updateSql);

		return updateSql.toString();
	}
//////////////search default mysql
	public String searchSQL(Object entity) {
		
		if(fields == null || columnValues == null)
			return null;

		try {	
			Field field;
			String key;
	
			Object obj;
			int columnType = 0;
			
			for(String eKey: extentValues.keySet()){
				byWhere = true;

				ExtentField<?> extentField = extentValues.get(eKey);
				obj = extentField.getStart();

				columnType = DbUtil.type(null,getCatalog(),getTable(),StringUtils.upperToPrefix(eKey,prefix));
				
				if(obj.getClass().isAssignableFrom(Integer.class)){
					whereBy.append(eKey+" >= "+extentField.getStart() +" AND ");
					whereBy.append(eKey+" <= "+extentField.getEnd() +" AND ");
				}else if(obj.getClass().isAssignableFrom(Date.class)){
					if(columnType==Types.TIMESTAMP){
						whereBy.append(eKey+" >= date'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(extentField.getStart()) +"' AND ");
						whereBy.append(eKey+" <= date'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(extentField.getEnd()) +"' AND ");
					} else {
						whereBy.append(eKey+" >= date'"+new SimpleDateFormat("yyyy-MM-dd").format(extentField.getStart()) +"' AND ");
						whereBy.append(eKey+" <= date'"+new SimpleDateFormat("yyyy-MM-dd").format(extentField.getEnd()) +"' AND ");
					}
				}else if(obj.getClass().isAssignableFrom(Double.class)){
					whereBy.append(eKey+" >= "+extentField.getStart() +" AND ");
					whereBy.append(eKey+" <= "+extentField.getEnd() +" AND ");
				} else {
					whereBy.append(eKey+" >= '"+extentField.getStart() +"' AND ");
					whereBy.append(eKey+" <= '"+extentField.getEnd() +"' AND ");
				}
			}
			
			for (Iterator<?> it = fields.keySet().iterator(); it.hasNext();) {
			
				key = (String) it.next();
				field = (Field) fields.get(key);
				
				obj = columnValues.get(key);
				
				if(field.isWhereByEqual()) {
					byWhere = true;
					
					if(obj.getClass().isAssignableFrom(Integer.class)){
						whereBy.append(field.getName()+" = "+obj +" AND ");
					}else if(obj.getClass().isAssignableFrom(Date.class)){
						if(field.getType()==Types.TIMESTAMP){
							whereBy.append(field.getName()+" = date'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((Date)obj)) +"' AND ");
						} else {
							whereBy.append(field.getName()+" = date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"' AND ");
						}
					}else if(obj.getClass().isAssignableFrom(Double.class)){
						whereBy.append(field.getName()+" = "+obj +" AND ");
					} else {
						whereBy.append(field.getName()+" = '"+obj +"' AND ");
					}
				}
				
				if(field.isWhereByGreaterEqual()) {
					byWhere = true;
					
					if(obj.getClass().isAssignableFrom(Integer.class)){
						whereBy.append(field.getName()+" >= "+obj +" AND ");
					}else if(obj.getClass().isAssignableFrom(Date.class)){
						if(field.getType()==Types.TIMESTAMP){
							whereBy.append(field.getName()+" >= date'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((Date)obj)) +"' AND ");
						} else {
							whereBy.append(field.getName()+" >= date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"' AND ");
						}
					}else if(obj.getClass().isAssignableFrom(Double.class)){
						whereBy.append(field.getName()+" >= "+obj +" AND ");
					} else {
						whereBy.append(field.getName()+" >= '"+obj +"' AND ");
					}
				}
	
				if(field.isWhereByLessEqual()) {
					byWhere = true;
	
					if(obj.getClass().isAssignableFrom(Integer.class)){
						whereBy.append(field.getName()+" <= "+obj +" AND ");
					}else if(obj.getClass().isAssignableFrom(Date.class)){
						if(field.getType()==Types.TIMESTAMP){
							whereBy.append(field.getName()+" <= date'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((Date)obj)) +"' AND ");
						} else {
							whereBy.append(field.getName()+" <= date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"' AND ");
						}
					}else if(obj.getClass().isAssignableFrom(Double.class)){
						whereBy.append(field.getName()+" <= "+obj +" AND ");
					} else {
						whereBy.append(field.getName()+" <= '"+obj +"' AND ");
					}
				}
	
				if(field.isWhereByLike()) {
					byWhere = true;
	
					if(obj.getClass().isAssignableFrom(Integer.class)){
						whereBy.append(field.getName()+" LIKE "+obj +" AND ");
					}else if(obj.getClass().isAssignableFrom(Date.class)){
						if(field.getType()==Types.TIMESTAMP){
							whereBy.append(field.getName()+" LIKE date'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((Date)obj)) +"' AND ");
						} else {
							whereBy.append(field.getName()+" LIKE date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"' AND ");
						}
					}else if(obj.getClass().isAssignableFrom(Double.class)){
						whereBy.append(field.getName()+" LIKE "+obj +" AND ");
					} else {
						whereBy.append(field.getName()+" LIKE '%"+obj +"%' AND ");
					}
				}
			}
			
			if(byWhere)
				whereBy.delete(whereBy.lastIndexOf("AND"),whereBy.lastIndexOf("AND")+3);
			
			if(byGroup)
				groupBy.deleteCharAt(groupBy.lastIndexOf(","));
			
			if(byOrder)
				orderBy.deleteCharAt(orderBy.lastIndexOf(","));
			
			if(isSearch){
				if(searchSql.lastIndexOf(",") > 0)
					searchSql.deleteCharAt(searchSql.lastIndexOf(","));
				
				if(byWhere) {
					searchSql.append(whereBy);
				}
				
				if(byGroup) {
					searchSql.append(groupBy);
				}
				
				if(byOrder) {
					searchSql.append(orderBy);
				}
				
				if(byLimit)
					searchSql.append(limitBy);
			}
			
			if(log.isDebugEnabled())log.debug("searchSql="+searchSql);
	
			return searchSql.toString();
		} finally {
			refreshSQL();
		}
	}
	
	public String countSQL(Object entity) {
		
		if(fields == null || columnValues == null)
			return null;
		
		try {
			Field field;
			String key;
			
			Object obj;
			int columnType = 0;
			
			for(String eKey: extentValues.keySet()){
				byWhere = true;

				ExtentField<?> extentField = extentValues.get(eKey);
				obj = extentField.getStart();

				columnType = DbUtil.type(null,getCatalog(),getTable(),StringUtils.upperToPrefix(eKey,prefix));

				if(obj.getClass().isAssignableFrom(Integer.class)){
					whereBy.append(eKey+" >= "+extentField.getStart() +" AND ");
					whereBy.append(eKey+" <= "+extentField.getEnd() +" AND ");
				}else if(obj.getClass().isAssignableFrom(Date.class)){
					if(columnType==Types.TIMESTAMP){
						whereBy.append(eKey+" >= date'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(extentField.getStart()) +"' AND ");
						whereBy.append(eKey+" <= date'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(extentField.getEnd()) +"' AND ");
					} else {
						whereBy.append(eKey+" >= date'"+new SimpleDateFormat("yyyy-MM-dd").format(extentField.getStart()) +"' AND ");
						whereBy.append(eKey+" <= date'"+new SimpleDateFormat("yyyy-MM-dd").format(extentField.getEnd()) +"' AND ");
					}
				}else if(obj.getClass().isAssignableFrom(Double.class)){
					whereBy.append(eKey+" >= "+extentField.getStart() +" AND ");
					whereBy.append(eKey+" <= "+extentField.getEnd() +" AND ");
				} else {
					whereBy.append(eKey+" >= '"+extentField.getStart() +"' AND ");
					whereBy.append(eKey+" <= '"+extentField.getEnd() +"' AND ");
				}
			}
			
			for (Iterator<?> it = fields.keySet().iterator(); it.hasNext();) {
			
				key = (String) it.next();
				field = (Field) fields.get(key);
				
				obj = columnValues.get(key);
				
				if(field.isWhereByEqual()) {
					byWhere = true;
					
					if(obj.getClass().isAssignableFrom(Integer.class)){
						whereBy.append(field.getName()+" = "+obj +" AND ");
					}else if(obj.getClass().isAssignableFrom(Date.class)){
						if(field.getType()==Types.TIMESTAMP){
							whereBy.append(field.getName()+" = date'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((Date)obj)) +"' AND ");
						} else {
							whereBy.append(field.getName()+" = date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"' AND ");
						}
					}else if(obj.getClass().isAssignableFrom(Double.class)){
						whereBy.append(field.getName()+" = "+obj +" AND ");
					} else {
						whereBy.append(field.getName()+" = '"+obj +"' AND ");
					}
				}
				
				if(field.isWhereByGreaterEqual()) {
					byWhere = true;
					
					if(obj.getClass().isAssignableFrom(Integer.class)){
						whereBy.append(field.getName()+" >= "+obj +" AND ");
					}else if(obj.getClass().isAssignableFrom(Date.class)){
						if(field.getType()==Types.TIMESTAMP){
							whereBy.append(field.getName()+" >= date'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((Date)obj)) +"' AND ");
						} else {
							whereBy.append(field.getName()+" >= date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"' AND ");
						}
					}else if(obj.getClass().isAssignableFrom(Double.class)){
						whereBy.append(field.getName()+" >= "+obj +" AND ");
					} else {
						whereBy.append(field.getName()+" >= '"+obj +"' AND ");
					}
				}
	
				if(field.isWhereByLessEqual()) {
					byWhere = true;
	
					if(obj.getClass().isAssignableFrom(Integer.class)){
						whereBy.append(field.getName()+" <= "+obj +" AND ");
					}else if(obj.getClass().isAssignableFrom(Date.class)){
						if(field.getType()==Types.TIMESTAMP){
							whereBy.append(field.getName()+" <= date'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((Date)obj)) +"' AND ");
						} else {
							whereBy.append(field.getName()+" <= date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"' AND ");
						}
					}else if(obj.getClass().isAssignableFrom(Double.class)){
						whereBy.append(field.getName()+" <= "+obj +" AND ");
					} else {
						whereBy.append(field.getName()+" <= '"+obj +"' AND ");
					}
				}
	
				if(field.isWhereByLike()) {
					byWhere = true;
	
					if(obj.getClass().isAssignableFrom(Integer.class)){
						whereBy.append(field.getName()+" LIKE "+obj +" AND ");
					}else if(obj.getClass().isAssignableFrom(Date.class)){
						if(field.getType()==Types.TIMESTAMP){
							whereBy.append(field.getName()+" LIKE date'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((Date)obj)) +"' AND ");
						} else {
							whereBy.append(field.getName()+" LIKE date'"+new SimpleDateFormat("yyyy-MM-dd").format(((Date)obj)) +"' AND ");
						}
					}else if(obj.getClass().isAssignableFrom(Double.class)){
						whereBy.append(field.getName()+" LIKE "+obj +" AND ");
					} else {
						whereBy.append(field.getName()+" LIKE '%"+obj +"%' AND ");
					}
				}
			}
			
			if(byWhere)
				whereBy.delete(whereBy.lastIndexOf("AND"),whereBy.lastIndexOf("AND")+3);
					
			if(isSearch){
				if(countSql.lastIndexOf(",") > 0)
					countSql.deleteCharAt(countSql.lastIndexOf(","));
				
				if(byWhere) {
					countSql.append(whereBy);
				}
				
				if(byGroup) {
					countSql.append(groupBy);
				}
				
				if(byOrder) {
					countSql.append(orderBy);
				}
	
				if(byLimit)
					countSql.append(limitBy);
			}
			
			if(log.isDebugEnabled())log.debug("countSql="+countSql);
			return countSql.toString();
		} finally {
			refreshSQL();
		}
	}
	
	public void entityFillField(Object entity) {
		try {
			List<String> methods = ReflectUtil.getMethodNames(entity.getClass());
			
			String method;
			String field;
			int columnType = 0;

			for (Iterator<String> it = methods.iterator(); it.hasNext();) {
				method = it.next();
				if(method.indexOf("get")==0){
					
					Method getMethod;
					getMethod = entity.getClass().getMethod(method);
					
					Object obj = getMethod.invoke(entity);
					
					if(obj !=null) {
						field = method.substring(method.indexOf("get")+3);
						columnType = DbUtil.type(null,getCatalog(),getTable(),StringUtils.upperToPrefix(field,prefix));
						
						if(obj.getClass().isAssignableFrom(Integer.class))
							setField(StringUtils.upperToPrefix(field,prefix), (Integer)obj);
						else if(obj.getClass().isAssignableFrom(String.class)){
							setField(StringUtils.upperToPrefix(field,prefix), (String)obj);
						}else if(obj.getClass().isAssignableFrom(Date.class)){
							if(columnType == Types.TIMESTAMP){
								setField(StringUtils.upperToPrefix(field,prefix), (Date)obj,Types.TIMESTAMP);
							} else{
								setField(StringUtils.upperToPrefix(field,prefix), (Date)obj,columnType);
							}
						}else if(obj.getClass().isAssignableFrom(Double.class)){
							setField(StringUtils.upperToPrefix(field,prefix), (Double)obj);
						}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
