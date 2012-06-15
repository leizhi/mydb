package com.mooo.mycoz.db.sql;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.mooo.mycoz.common.ReflectUtil;
import com.mooo.mycoz.common.StringUtils;
import com.mooo.mycoz.db.DbConfig;
import com.mooo.mycoz.db.DbUtil;
import com.mooo.mycoz.db.Field;

public abstract class AbstractSQL implements SetupSQL,ProcessSQL,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7676596824913418468L;

	public static final int DB_MYSQL=0;

	public static final int DB_ORACLE=1;
	
	public static final int DB_MSSQL=2;
	
	public static SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private static final String UPDATE="UPDATE ";
	
	private static final String ADD="INSERT INTO ";
	
	private static final String DELETE="DELETE FROM ";

	private static final String SEARCH="SELECT * FROM ";
	
	private static final String COUNT="SELECT COUNT(*) FROM ";
	
	private static final String GROUP_BY=" GROUP BY ";
	
	private static final String ORDER_BY=" ORDER BY ";
	
	private static final String OFFSET_PAGE=" LIMIT ";
	
	private String prefix;

	private String catalog;
	
	private int offsetRecord, maxRecords;
	
	//Note: and,delete,update SQL not retrieve field
	//		just any select SQL have retrieve field
	
	//Affect the field parameters for SQL
	//Note: and,update are SQL affect field
	//		delete,select are SQL not affect field
	
	//Filter the field parameters for SQL
	//Note: and SQL not filter field
	//		update,delete,select have filter field
	
	
	private List<Field> entityField;
	
	private List<Field> extendField;
	
	private String table;
	
	public AbstractSQL(){
		entityField = new ArrayList<Field>();
		extendField = new ArrayList<Field>();
		
		offsetRecord=-1;
		maxRecords=-1;
	}

	private void setWhereFor(String fieldName,Object fieldValue,int fieldType,String whereBy,String whereRule,boolean isPrimaryKey){
		boolean haveField = false;
		
		for(Field field:entityField){

			if(fieldName.equals(field.getFieldName())
					&& field.getWhereRule().equals(Field.RULE_EQUAL)){
				haveField=true;
				
				field.setFieldValue(fieldValue);
				field.setFieldType(fieldType);
				field.setWhereBy(whereBy);
				field.setWhereRule(whereRule);
				field.setPrimaryKey(isPrimaryKey);
			}
		}
		
		if(!haveField)
			entityField.add(new Field(fieldName,fieldValue,fieldType,whereBy,whereRule,isPrimaryKey));
	}
	
	public void setField(String fieldName,Object fieldValue,int fieldType,boolean isPrimaryKey){
		setWhereFor(fieldName,fieldValue,fieldType,Field.WHERE_BY_AND,Field.RULE_EQUAL,isPrimaryKey);
	}
	
	
	public void setLike(String fieldName,Object fieldValue){
		boolean haveField = false;
		
		for(Field field:entityField){
			if(fieldName.equals(field.getFieldName())
					&& field.getWhereRule().equals(Field.RULE_LIKE)){
				haveField=true;
				
				field.setFieldValue(fieldValue);
//				field.setFieldType(1000);
//				field.setWhereBy(Field.WHERE_BY_AND);
//				field.setWhereRule(Field.RULE_LIKE);
//				field.setPrimaryKey(false);
			}
		}
		
		if(!haveField)
			extendField.add(new Field(fieldName,fieldValue,1000,Field.WHERE_BY_AND,Field.RULE_LIKE,false));
	}
	
	public void setGreaterEqual(String fieldName,Object fieldValue){
		boolean haveField = false;
		
		for(Field field:entityField){
			if(fieldName.equals(field.getFieldName())
					&& field.getWhereRule().equals(Field.RULE_GREATER_EQUAL)){
				haveField=true;
				
				field.setFieldValue(fieldValue);
			}
		}
		
		if(!haveField)
			extendField.add(new Field(fieldName,fieldValue,1000,Field.WHERE_BY_AND,Field.RULE_GREATER_EQUAL,false));
	}
	
	public void setLessEqual(String fieldName,Object fieldValue){
		boolean haveField = false;
		
		for(Field field:entityField){
			if(fieldName.equals(field.getFieldName())
					&& field.getWhereRule().equals(Field.RULE_LESS_EQUAL)){
				haveField=true;
				
				field.setFieldValue(fieldValue);
			}
		}
		
		if(!haveField)
			extendField.add(new Field(fieldName,fieldValue,1000,Field.WHERE_BY_AND,Field.RULE_LESS_EQUAL,false));
		
	}
	
	public void setWhereIn(String fieldName,Object fieldValue){
		boolean haveField = false;
		
		for(Field field:entityField){
			if(fieldName.equals(field.getFieldName())
					&& field.getWhereRule().equals(Field.RULE_IN)){
				haveField=true;
				
				field.setFieldValue(fieldValue);
			}
		}
		
		if(!haveField)
			extendField.add(new Field(fieldName,fieldValue,1000,Field.WHERE_BY_AND,Field.RULE_IN,false));
	}
	
	public void addGroupBy(String fieldName){
		for(Field field:entityField){
			if(fieldName.equals(field.getFieldName())){
				field.setGroupBy(true);
				break;
			}
		}
	}
	
	public void addOrderBy(String fieldName){
		for(Field field:entityField){
			if(fieldName.equals(field.getFieldName())){
				field.setOrderBy(true);
				break;
			}
		}
	}
	
	public void setRecord(int offsetRecord, int maxRecords) {
		this.offsetRecord=offsetRecord;
		this.maxRecords=maxRecords;
	}
	
	public void entityFillField(Object entity) {
		try {
			
			prefix = DbConfig.getProperty("Db.humpInterval");
			
			if(prefix !=null && prefix.equals("case")){
				prefix = null;
			}
			
//			enableCase = DbConfig.getProperty("Db.case").equals("true");
			
			catalog = StringUtils.getCatalog(entity.getClass(),1);
			table = StringUtils.upperToPrefix(entity.getClass().getSimpleName(),prefix);

			List<String> methods = ReflectUtil.getMethodNames(entity.getClass());
			
			String method;
			String field;
			
			int columnType = 0;
			String columnName = null;
			boolean isPrimaryKey = false;
			
			for (Iterator<String> it = methods.iterator(); it.hasNext();) {
				method = it.next();
				if(method.indexOf("get")==0){
					
					Method getMethod;
					getMethod = entity.getClass().getMethod(method);
					
					Object columnValue = getMethod.invoke(entity);
					
					if(columnValue !=null) {
						field = method.substring(method.indexOf("get")+3);
						
						columnName = StringUtils.upperToPrefixNot(field,prefix);
						
						columnType = DbUtil.type(catalog,table,columnName);
						
						if(columnType>0){
							isPrimaryKey = DbUtil.isPrimaryKey(catalog, table,columnName);
							
							setField(columnName,columnValue,columnType,isPrimaryKey);
							
//							System.out.println(columnName+" "+catalog+" "+table+" "+
//							columnValue+" "+columnType+" "+isPrimaryKey);
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
	
	public String addSQL(Object entity){
		entityFillField(entity);
		
		String sql = ADD;
		
		sql += " "+catalog+".";
		
		sql += table;
		
		sql += " (";
		
		boolean isHead = true;
		for(Field field:entityField){

			if(isHead) 
				isHead = false;
			else
				sql += ",";
			
			sql += field.getFieldName();
			
		}
		
		sql += ") VALUES(";
		
		isHead = true;
		for(Field field:entityField){

			if(isHead) 
				isHead = false;
			else
				sql += ",";

			sql += field.getFieldName()+"=";
			Object fieldValue = field.getFieldValue();
			
			if(field.getFieldType()==Types.TIMESTAMP){
				sql += "date'"+dtformat.format(((Date)fieldValue))+"'";
			}else if(field.getFieldType()==Types.DATE){
				sql += "date'"+dformat.format(((Date)fieldValue))+"'";
			} else {
				sql += StringUtils.sqlValue(fieldValue);
			}
			
		}
		
		sql += ")"; 
		
		return sql;
	}
	
	public String updateSQL(Object entity){
		entityFillField(entity);
		
		String sql = UPDATE;
		
		sql += " "+catalog+".";
		
		sql += table;
		
		Object fieldValue;
		boolean isHead = true;

		for(Field field:entityField){

			if(!field.isPrimaryKey() ){
				if(isHead) {
					isHead = false;
					sql += " SET (";
				}else{
					sql += ",";
				}
				
				fieldValue = field.getFieldValue();
				
				sql += field.getFieldName()+"=";

				if(field.getFieldType()==Types.TIMESTAMP){
					sql += "date'"+dtformat.format(((Date)fieldValue))+"'";
				}else if(field.getFieldType()==Types.DATE){
					sql += "date'"+dformat.format(((Date)fieldValue))+"'";
				} else {
					sql += StringUtils.sqlValue(fieldValue);
				}

			}
		}
		
		sql += ")";
		
		isHead = true;
		for(Field field:entityField){
			
			if(field.isPrimaryKey() ){
				if(isHead) {
					isHead = false;
					sql += " WHERE ";
				}else{
					sql += field.getWhereBy();
				}
				
				fieldValue = field.getFieldValue();
				
				sql += field.getFieldName()+"=";

				if(field.getFieldType()==Types.TIMESTAMP){
					sql += "date'"+dtformat.format(((Date)fieldValue))+"'";
				}else if(field.getFieldType()==Types.DATE){
					sql += "date'"+dformat.format(((Date)fieldValue))+"'";
				} else {
					sql += fieldValue.toString();
				}
				
			}
		}
		
		return sql;
	}
	
	public String deleteSQL(Object entity){
		entityFillField(entity);
		
		String sql = DELETE;
		
		sql += catalog+".";
		
		sql += table;
		
		boolean isHead = true;
		
		for(Field field:entityField){
			
			if(isHead) {
				isHead = false;
				sql += " WHERE ";
			}else{
				sql += field.getWhereBy();
			}
			
			sql += field.getFieldName()+field.getWhereRule();
			
			Object fieldValue = field.getFieldValue();
			
			if(field.getFieldType()==Types.TIMESTAMP){
				sql += "date'"+dtformat.format(((Date)fieldValue))+"'";
			}else if(field.getFieldType()==Types.DATE){
				sql += "date'"+dformat.format(((Date)fieldValue))+"'";
			} else {
				sql += StringUtils.sqlValue(fieldValue);
			}
			
		}
		
		//fill extend field
		for(Field field:extendField){
			
			if(isHead) {
				isHead = false;
				sql += " WHERE ";
			}else{
				sql += field.getWhereBy();
			}
			
			sql += field.getFieldName()+field.getWhereRule();
			
			Object fieldValue = field.getFieldValue();
			
			if(field.getFieldType()==Types.TIMESTAMP){
				sql += "date'"+dtformat.format(((Date)fieldValue))+"'";
			}else if(field.getFieldType()==Types.DATE){
				sql += "date'"+dformat.format(((Date)fieldValue))+"'";
			} else {
				sql += StringUtils.sqlValue(fieldValue);
			}
			
		}
		
		return sql;
	}
	
	public String searchSQL(Object entity){
		entityFillField(entity);
		
		String sql = SEARCH;
				
		sql += catalog+".";
		
		sql += table;
		
		boolean isHead = true;
		
		for(Field field:entityField){
			
			if(isHead) {
				isHead = false;
				sql += " WHERE ";
			}else{
				sql += field.getWhereBy();
			}
			
			sql += field.getFieldName()+"=";
			Object fieldValue = field.getFieldValue();
			
			if(field.getFieldType()==Types.TIMESTAMP){
				sql += "date'"+dtformat.format(((Date)fieldValue))+"'";
			}else if(field.getFieldType()==Types.DATE){
				sql += "date'"+dformat.format(((Date)fieldValue))+"'";
			} else {
				sql += StringUtils.sqlValue(fieldValue);
			}
		}
		
		//fill extend field
		for(Field field:extendField){
			
			if(isHead) {
				isHead = false;
				sql += " WHERE ";
			}else{
				sql += field.getWhereBy();
			}
			
			sql += field.getFieldName()+field.getWhereRule();
			
			Object fieldValue = field.getFieldValue();
			
			if(field.getFieldType()==Types.TIMESTAMP){
				sql += "date'"+dtformat.format(((Date)fieldValue))+"'";
			}else if(field.getFieldType()==Types.DATE){
				sql += "date'"+dformat.format(((Date)fieldValue))+"'";
			} else {
				sql += StringUtils.sqlValue(fieldValue);
			}
			
		}
		
		//make group by field
		isHead = true;
		for(Field field:entityField){
			
			if(field.isGroupBy()){
				if(isHead) {
					isHead = false;
					sql += GROUP_BY;
				}else{
					sql += ",";
				}
				
				sql += field.getFieldName();
			}
		}
		
		//make order by field
		isHead = true;
		for(Field field:entityField){
			
			if(field.isOrderBy()){
				if(isHead) {
					isHead = false;
					sql += ORDER_BY;
				}else{
					sql += ",";
				}
				
				sql += field.getFieldName();
			}
		}
		
		
		if(offsetRecord>-1 && maxRecords>0){
			sql += OFFSET_PAGE+offsetRecord+","+maxRecords;
		}
		
		return sql;
	}
	
	public String countSQL(Object entity){
		String sql = searchSQL(entity);
		
		return COUNT+" ("+sql.substring(0,sql.indexOf(OFFSET_PAGE)+1)+") result";
	}
	
	abstract public String offsetRecordSQL();
	
	abstract public String selfDateSQL(Date date);

	public int getOffsetRecord() {
		return offsetRecord;
	}

	public void setOffsetRecord(int offsetRecord) {
		this.offsetRecord = offsetRecord;
	}

	public int getMaxRecords() {
		return maxRecords;
	}

	public void setMaxRecords(int maxRecords) {
		this.maxRecords = maxRecords;
	}
	
}
