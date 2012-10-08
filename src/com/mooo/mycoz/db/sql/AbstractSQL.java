package com.mooo.mycoz.db.sql;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.mooo.mycoz.common.CalendarUtils;
import com.mooo.mycoz.common.ReflectUtil;
import com.mooo.mycoz.common.StringUtils;
import com.mooo.mycoz.db.DbConfig;
import com.mooo.mycoz.db.DbUtil;
import com.mooo.mycoz.db.Field;

public abstract class AbstractSQL implements ProcessSQL,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7676596824913418468L;

	public static final int DB_MYSQL=0;

	public static final int DB_ORACLE=1;
	
	public static final int DB_MSSQL=2;
	
	private static final String UPDATE="UPDATE ";
	
	private static final String ADD="INSERT INTO ";
	
	private static final String DELETE="DELETE FROM ";

	private static final String SEARCH="SELECT * FROM ";
	
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
	
	private List<Field> groupField;

	private List<Field> orderField;

	private String table;
	
	public AbstractSQL(){
		entityField = new ArrayList<Field>();
		extendField = new ArrayList<Field>();
		
		groupField = new ArrayList<Field>();
		orderField = new ArrayList<Field>();
		
		offsetRecord=-1;
		maxRecords=-1;
	}

	private void setWhereFor(String fieldName,Object fieldValue,int fieldType,String whereBy,String whereRule,boolean isPrimaryKey){
		boolean haveField = false;
		
		for(Field field:entityField){

			if(fieldName.equals(field.getFieldName())
					&& field.getWhereRule().equals(Field.RULE_EQUAL)
					&& fieldValue!=null){
				
				field.setFieldValue(fieldValue);
				field.setFieldType(fieldType);
				field.setWhereBy(whereBy);
				field.setWhereRule(whereRule);
				field.setPrimaryKey(isPrimaryKey);
				
				haveField=true;
				break;
			}
		}
		
		if(!haveField && fieldValue!=null){
			
			if(fieldValue.getClass().isAssignableFrom(String.class)){
				String value = fieldValue.toString();
				
				if(!StringUtils.isNull(value)){
					entityField.add(new Field(fieldName,fieldValue,fieldType,whereBy,whereRule,isPrimaryKey));
				}
			}else{
				entityField.add(new Field(fieldName,fieldValue,fieldType,whereBy,whereRule,isPrimaryKey));
			}
		}
	}
	
	public void setField(String fieldName,Object fieldValue,int fieldType,boolean isPrimaryKey){
		setWhereFor(fieldName,fieldValue,fieldType,Field.WHERE_BY_AND,Field.RULE_EQUAL,isPrimaryKey);
	}
	
	
	public void setLike(String fieldName,Object fieldValue){
		if(fieldName!=null && fieldValue!=null){
			boolean haveField = false;
			
			for(Field field:entityField){
				if(fieldName.equals(field.getFieldName())
						&& field.getWhereRule().equals(Field.RULE_LIKE)){
					
					if(fieldValue.getClass().isAssignableFrom(String.class)){
						String value = fieldValue.toString();
						
						if(!StringUtils.isNull(value)){
							field.setFieldValue("%"+fieldValue+"%");
	
							haveField=true;
							break;
						}
					}
				}
			}
			
			if(!haveField){
					extendField.add(new Field(fieldName,fieldValue,1000,Field.WHERE_BY_AND,Field.RULE_LIKE,false));
			}
		}
	}
	
	public void setGreaterEqual(String fieldName,Object fieldValue){
		if(fieldName!=null && fieldValue!=null){
			boolean haveField = false;
			for(Field field:entityField){
				if(fieldName.equals(field.getFieldName())
						&& field.getWhereRule().equals(Field.RULE_GREATER_EQUAL)){
					
					field.setFieldValue(fieldValue);
					
					haveField=true;
					break;
				}
			}
			
			if(!haveField)
				extendField.add(new Field(fieldName,fieldValue,1000,Field.WHERE_BY_AND,Field.RULE_GREATER_EQUAL,false));
		}
	}
	
	public void setLessEqual(String fieldName,Object fieldValue){
		if(fieldName!=null && fieldValue!=null){
			boolean haveField = false;
			for(Field field:entityField){
				if(fieldName.equals(field.getFieldName())
						&& field.getWhereRule().equals(Field.RULE_LESS_EQUAL)){
					
					field.setFieldValue(fieldValue);
					
					haveField=true;
					break;
				}
			}
			
			if(!haveField)
				extendField.add(new Field(fieldName,fieldValue,1000,Field.WHERE_BY_AND,Field.RULE_LESS_EQUAL,false));
		}
		
	}
	
	public void setWhereIn(String fieldName,Object fieldValue){
		if(fieldName!=null && fieldValue!=null){
			boolean haveField = false;
			for(Field field:entityField){
				if(fieldName.equals(field.getFieldName())
						&& field.getWhereRule().equals(Field.RULE_IN)){
					
					field.setFieldValue(fieldValue);
					
					haveField=true;
					break;
				}
			}
			
			if(!haveField)
				extendField.add(new Field(fieldName,fieldValue,1000,Field.WHERE_BY_AND,Field.RULE_IN,false));
		}
	}
	
	public void addGroupBy(String fieldName){
		if(fieldName!=null){
			boolean haveField = false;
			for(Field field:groupField){
				if(fieldName.equals(field.getFieldName())){
					
					haveField=true;
					break;
				}
			}
			
			if(!haveField)
				groupField.add(new Field(fieldName,true,false));
		}
	}
	
	public void addOrderBy(String fieldName){
		if(fieldName!=null){
			boolean haveField = false;
			for(Field field:orderField){
				if(fieldName.equals(field.getFieldName())){
					
					haveField=true;
					break;
				}
			}
			
			if(!haveField)
				orderField.add(new Field(fieldName,false,true));
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
	
	private String extendSQL(boolean isHead){
		StringBuffer buffer = new StringBuffer();
		
		//fill extend field
		for(Field field:extendField){
			
			Object fieldValue = field.getFieldValue();
			//Object choose
			if(fieldValue!=null){
				if(fieldValue.getClass().isAssignableFrom(Date.class)){
					Date value = (Date)fieldValue;
					
					if(isHead) {
						isHead = false;
						buffer.append(" WHERE ");
					}else{
						buffer.append(field.getWhereBy());
					}
					
					buffer.append(field.getFieldName() + field.getWhereRule());

					if(field.getWhereRule().equals(Field.RULE_LIKE)){
						if(field.getFieldType()==Types.TIMESTAMP){
							buffer.append("date'%"+CalendarUtils.dtformat(value)+"%'");
						}else if(field.getFieldType()==Types.DATE){
							buffer.append("date'%"+CalendarUtils.dformat(value)+"%'");
						}
					}else{
						if(field.getFieldType()==Types.TIMESTAMP){
							buffer.append("date'"+CalendarUtils.dtformat(value)+"'");
						}else if(field.getFieldType()==Types.DATE){
							buffer.append("date'"+CalendarUtils.dformat(value)+"'");
						}
					}
				}else if(fieldValue.getClass().isAssignableFrom(Integer.class)
					||fieldValue.getClass().isAssignableFrom(Long.class)
					||fieldValue.getClass().isAssignableFrom(Float.class)
					||fieldValue.getClass().isAssignableFrom(Double.class)){
					
					if(isHead) {
						isHead = false;
						buffer.append(" WHERE ");
					}else{
						buffer.append(field.getWhereBy());
					}
					
					buffer.append(field.getFieldName()+field.getWhereRule() + fieldValue);

				}else if(fieldValue.getClass().isAssignableFrom(String.class)){
					String value = (String)fieldValue;
					
					if(!value.equals("")){
						if(isHead) {
							isHead = false;
							buffer.append(" WHERE ");
						}else{
							buffer.append(field.getWhereBy());
						}
						
						buffer.append(field.getFieldName()+field.getWhereRule());

						if(field.getWhereRule().equals(Field.RULE_LIKE)){
							buffer.append("'%"+value+"%'");
						}else if(field.getWhereRule().equals(Field.RULE_IN)){
							buffer.append(" ("+value+") ");
						}else{
							buffer.append(" '"+value+"' ");
						}
					}
				}
			}
			
		}
				
		return buffer.toString();
	}
	
	private String groupBy(){
		StringBuffer buffer = new StringBuffer();
		boolean isHead = true;

		//make group by field
		for(Field field:groupField){
			
			if(field.isGroupBy()){
				if(isHead) {
					isHead = false;
					buffer.append(GROUP_BY);
				}else{
					buffer.append(",");
				}
				
				buffer.append(field.getFieldName());
			}
		}
		return buffer.toString();
	}
	
	private String orderBy(){
		StringBuffer buffer = new StringBuffer();
		boolean isHead = true;

		//make group by field
		for(Field field:orderField){
			
			if(field.isOrderBy()){
				if(isHead) {
					isHead = false;
					buffer.append(ORDER_BY);
				}else{
					buffer.append(",");
				}
				
				buffer.append(field.getFieldName());
			}
		}
		return buffer.toString();
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

			Object fieldValue = field.getFieldValue();
			
			if(field.getFieldType()==Types.TIMESTAMP){
				sql += "date'"+CalendarUtils.dtformat(((Date)fieldValue))+"'";
			}else if(field.getFieldType()==Types.DATE){
				sql += "date'"+CalendarUtils.dformat(((Date)fieldValue))+"'";
			} else {
				sql += StringUtils.fieldValue(fieldValue);
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
					sql += " SET ";
				}else{
					sql += ",";
				}
				
				fieldValue = field.getFieldValue();
				
				sql += field.getFieldName()+"=";

				if(field.getFieldType()==Types.TIMESTAMP){
					sql += "date'"+CalendarUtils.dtformat(((Date)fieldValue))+"'";
				}else if(field.getFieldType()==Types.DATE){
					sql += "date'"+CalendarUtils.dformat(((Date)fieldValue))+"'";
				} else {
					sql += StringUtils.fieldValue(fieldValue);
				}

			}
		}
		
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
					sql += "date'"+CalendarUtils.dtformat(((Date)fieldValue))+"'";
				}else if(field.getFieldType()==Types.DATE){
					sql += "date'"+CalendarUtils.dformat(((Date)fieldValue))+"'";
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
				sql += "date'"+CalendarUtils.dtformat(((Date)fieldValue))+"'";
			}else if(field.getFieldType()==Types.DATE){
				sql += "date'"+CalendarUtils.dformat(((Date)fieldValue))+"'";
			} else {
				sql += StringUtils.fieldValue(fieldValue);
			}
			
		}
		
		//fill extend field
		sql += extendSQL(isHead);
		
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
				sql += "date'"+CalendarUtils.dtformat(((Date)fieldValue))+"'";
			}else if(field.getFieldType()==Types.DATE){
				sql += "date'"+CalendarUtils.dformat(((Date)fieldValue))+"'";
			} else {
				sql += StringUtils.fieldValue(fieldValue);
			}
		}
		
		//fill extend field
		sql += extendSQL(isHead);
		
		//make group by field
		sql += groupBy();
		
		//make order by field
		sql += orderBy();
		
		if(offsetRecord>-1 && maxRecords>0){
			sql += OFFSET_PAGE+offsetRecord+","+maxRecords;
		}
		
		return sql;
	}
	
	public String countSQL(Object entity){
		String querySQL = searchSQL(entity);

		int vdex = querySQL.indexOf(OFFSET_PAGE);
		if(vdex>0){
			querySQL = querySQL.substring(0,vdex);
		}
		
		return querySQL;
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
