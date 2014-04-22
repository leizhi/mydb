package com.mooo.mycoz.db;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import com.mooo.mycoz.common.CalendarUtils;
import com.mooo.mycoz.common.StringUtils;
import com.mooo.mycoz.db.pool.DbConnectionManager;

public class DbBridgingBean {

	private String prefix;

	/**
	 * 动态赋值给系统对象 String Integer Long Float Date 等
	 * 
	 * @param bean
	 *            执行对象
	 * @param propertyName
	 *            属性
	 * @param value
	 *            参数
	 * @param format
	 *            参数格式化规格
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws ParseException
	 */
	public static void bindProperty(Object bean, String propertyName,String value) {

		try{
		// 得到方法名
		String funName = StringUtils.getFunName(propertyName);
		// get方法
		Method getMethod = bean.getClass().getMethod("get" + funName);
		// 得到参数类型
		Class<?> cl = getMethod.getReturnType();
		// set方法
		Method setMethod = bean.getClass().getMethod("set" + funName,new Class[] { cl });

		// 当参数为空时直接赋予NULL值
		if (value == null || value.trim().equals("")) {
			setMethod.invoke(bean, new Object[] { null });
			return;
		}

		// 根据不同的系统对象转换
		if (cl == String.class) {
			setMethod.invoke(bean, new Object[] { value });
			return;
		} else if (cl == Integer.class || cl == Float.class || cl == Long.class
				|| cl == Double.class || cl == Byte.class
				|| cl == Boolean.class || cl == Character.class) {
			Method valueOf = cl.getMethod("valueOf",new Class[] { String.class });
			Object valueObj = valueOf.invoke(cl, new Object[] { value });
			setMethod.invoke(bean, new Object[] { valueObj });
		} else if(cl == java.util.Date.class || cl == java.sql.Date.class){

			Object bindDate = new Date();
			
			if(value.length()==10){
				bindDate =  CalendarUtils.dparse(value);
			}else if(value.length() > 10 && value.length() < 20){
				bindDate =  CalendarUtils.dtparse(value);
			}			
			setMethod.invoke(bean, new Object[] { bindDate });
		}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void bindProperty(Object bean, String propertyName,
			Short value) throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException, ParseException,
			InstantiationException {

		// 得到方法名
		String funName = StringUtils.getFunName(propertyName);
		// get方法
		Method getMethod = bean.getClass().getMethod("get" + funName);
		// 得到参数类型
		Class<?> cl = getMethod.getReturnType();
		// set方法
		Method setMethod = bean.getClass().getMethod("set" + funName,
				new Class[] { cl });

		// 当参数为空时直接赋予NULL值
		if (value == null || value==0) {
			setMethod.invoke(bean, new Object[] { null });
			return;
		}

		// 根据不同的系统对象转换
		if (cl == Short.class ) {
			setMethod.invoke(bean, new Object[] { value });
		} 
	}
	
	public static void bindProperty(Object bean, String propertyName,
			Integer value) throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException, ParseException,
			InstantiationException {

		// 得到方法名
		String funName = StringUtils.getFunName(propertyName);
		// get方法
		Method getMethod = bean.getClass().getMethod("get" + funName);
		// 得到参数类型
		Class<?> cl = getMethod.getReturnType();
		// set方法
		Method setMethod = bean.getClass().getMethod("set" + funName,
				new Class[] { cl });

		// 当参数为空时直接赋予NULL值
		if (value == null) {
			setMethod.invoke(bean, new Object[] { null });
			return;
		}

		// 根据不同的系统对象转换
		if (cl == Integer.class ) {
			setMethod.invoke(bean, new Object[] { value });
		} 
	}
	
	public static void bindProperty(Object bean, String propertyName,
			Long value) throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException, ParseException,
			InstantiationException {

		// 得到方法名
		String funName = StringUtils.getFunName(propertyName);
		// get方法
		Method getMethod = bean.getClass().getMethod("get" + funName);
		// 得到参数类型
		Class<?> cl = getMethod.getReturnType();
		// set方法
		Method setMethod = bean.getClass().getMethod("set" + funName,
				new Class[] { cl });

		// 当参数为空时直接赋予NULL值
		if (value == null) {
			setMethod.invoke(bean, new Object[] { null });
			return;
		}

		// 根据不同的系统对象转换
		if (cl == Long.class) {
			setMethod.invoke(bean, new Object[] { value });
		}
	}
	
	public static void bindProperty(Object bean, String propertyName,
			Double value) throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException, ParseException,
			InstantiationException {

		// 得到方法名
		String funName = StringUtils.getFunName(propertyName);
		// get方法
		Method getMethod = bean.getClass().getMethod("get" + funName);
		// 得到参数类型
		Class<?> cl = getMethod.getReturnType();
		// set方法
		Method setMethod = bean.getClass().getMethod("set" + funName,
				new Class[] { cl });

		// 当参数为空时直接赋予NULL值
		if (value == null) {
			setMethod.invoke(bean, new Object[] { null });
			return;
		}

		// 根据不同的系统对象转换
		if (cl == Double.class) {
			setMethod.invoke(bean, new Object[] { value });
		}
	}
	
	public static void bindProperty(Object bean, String propertyName,
			Float value) throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException, ParseException,
			InstantiationException {

		// 得到方法名
		String funName = StringUtils.getFunName(propertyName);
		// get方法
		Method getMethod = bean.getClass().getMethod("get" + funName);
		// 得到参数类型
		Class<?> cl = getMethod.getReturnType();
		// set方法
		Method setMethod = bean.getClass().getMethod("set" + funName,
				new Class[] { cl });

		// 当参数为空时直接赋予NULL值
		if (value == null) {
			setMethod.invoke(bean, new Object[] { null });
			return;
		}

		// 根据不同的系统对象转换
		if (cl == Float.class) {
			setMethod.invoke(bean, new Object[] { value });
		}
	}
	
	public static void bindProperty(Object bean, String propertyName,Date date) throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException, ParseException,
			InstantiationException {

		// 得到方法名
		String funName = StringUtils.getFunName(propertyName);
		// get方法
		Method getMethod = bean.getClass().getMethod("get" + funName);
		// 得到参数类型
		Class<?> cl = getMethod.getReturnType();
		// set方法
		Method setMethod = bean.getClass().getMethod("set" + funName,new Class[] { cl });

		// 当参数为空时直接赋予NULL值
		if (date == null) {
			setMethod.invoke(bean, new Object[] { null });
			return;
		}
		
		if (cl == java.util.Date.class || cl == java.sql.Date.class) {
			Object dateObj = cl.newInstance();
			Method setTime = cl.getMethod("setTime", new Class[] { long.class });
			setTime.invoke(dateObj, new Object[] { date.getTime() });
			setMethod.invoke(bean, new Object[] { dateObj });
		}
	}
	/**
	 * 动态赋值给自定义对象 member.city.id = 110 bean = member objName = city propertyName
	 * = id value = 110
	 * 
	 * @param bean
	 *            执行对象
	 * @param objName
	 *            做参数的对象
	 * @param propertyName
	 *            做参数的对象的属性名称
	 * @param value
	 *            参数
	 * @param format
	 *            参数格式化规格
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws ParseException
	 * @throws InstantiationException
	 */
	public static void bindSubObject(Object bean, String objName,String propertyName, String value)
			throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException, ParseException, InstantiationException {
		
		String funName = StringUtils.getFunName(objName);
		Method getMethod = bean.getClass().getMethod("get" + funName);
		Class<?> cls = getMethod.getReturnType();
		
		Object obj = getMethod.invoke(bean);

		// 判断参数为空,直接设置NULL值.
		if (value.trim().equals("")) {
			if (obj != null) {
				Method setMethod = bean.getClass().getMethod("set" + funName,new Class[] { cls });
				setMethod.invoke(bean, new Object[] { null });
			}
			return;
		}

		// 如果对象未初次化
		if (obj == null) {
			obj = cls.newInstance();
		}

		// 设置普通系统对象的属性
		// 填充父对象
		DbBridgingBean.bindProperty(obj, propertyName, value);
		
		if (cls == java.util.Date.class || cls == java.sql.Date.class) {
			// 设置普通系统对象的属性
			Object dateObj = new Date( new Long(value));

			Method setMethod = bean.getClass().getMethod("set" + funName,new Class[] { cls });
			// 把对象填充
			setMethod.invoke(bean, new Object[] { dateObj });
		} else{
			Method setMethod = bean.getClass().getMethod("set" + funName,new Class[] { cls });
			// 把对象填充
			setMethod.invoke(bean, new Object[] { obj });
		}
		
	}
	
	public static void noNull(Object obj) throws NullPointerException{
		if(obj == null){
			throw new NullPointerException("cant no null");
		}
	}
	
	public void dbToBean(String catalog, String table){
		prefix = DbConfig.getProperty("Db.humpInterval");

		if(prefix !=null && prefix.equals("case")){
			prefix = null;
		}

//		boolean enableCase = DbConfig.getProperty("Db.case").equals("true");

		Connection con = null;
		Statement stmt = null;
		ResultSetMetaData rsmd = null;
		String sql = "";
		try {
		StringBuilder buffer = new StringBuilder();
		buffer.append("import java.util.*;\n");
		//buffer.append("import com.mooo.mycoz.dbobj.DBObject;\n");
		buffer.append("public class ");
		
		ResultSet rs = null;
		//mypool
		con = DbConnectionManager.getConnection();
		con.setCatalog(catalog);
		
		System.out.println("打开连接-------------");
		System.out.println(con);
		
		DatabaseMetaData db =  DbConnectionManager.getConnection().getMetaData();
		rs = db.getPrimaryKeys(catalog, null, StringUtils.prefixToUpperNot(table, null));
		rsmd = rs.getMetaData();
		
		while (rs.next()) {
			for (int i = 1; i < rsmd.getColumnCount()+1; i++) {
				System.out.print(StringUtils.prefixToUpperNot(rs.getString(i),prefix) + " ");
			}
			System.out.println();
		}
		
		// 表信息
		String[] t = { "TABLE", "VIEW" };
		ResultSet tableRs = db.getTables(catalog, null, table, t);
		while (tableRs.next()) {
		for (int i = 1; i < 6; i++) {
		System.out.print(tableRs.getString(i) + " ");
		}
		System.out.println("+++++++++++++++++++++++++++++++++");
		}
		// 列信息
		rs = db.getColumns(catalog, null, table, null);
		while (rs.next()) {
		for (int i = 1; i < 19; i++) {
		System.out.print(StringUtils.prefixToUpperNot(rs.getString(i),prefix) + " ");
		}
		System.out.println("+++++++++++++++++++++++++++++++++");

		}
		//主键信息
		ResultSet pkRs = db.getPrimaryKeys(catalog, null, table);
		while (pkRs.next()) {
		for (int i = 1; i < 6; i++) {
		System.out.print(pkRs.getString(i) + " ");
		}
		System.out.println("+++++++++++++++++++++++++++++++++");

		} 
		
		sql = "SELECT  * FROM "+table;
		System.out.println(sql);
		buffer.append(StringUtils.prefixToUpperNot(table,prefix));
		//buffer.append(" extends DBObject {\n");
		buffer.append(" extends DBObject{\n");

		System.out.println("beanName="+StringUtils.prefixToUpperNot(table,prefix));

		stmt = con.createStatement();
		rs = stmt.executeQuery(sql);
		rsmd = rs.getMetaData();
		
		int type=0;
		int precision=0;
		int scale=0;
		String columnName="";

		StringBuilder gsMethod = new StringBuilder();
		
		for (int i = 0; i < rsmd.getColumnCount(); i++) {
			type = rsmd.getColumnType(i+1);
			precision = rsmd.getPrecision(i+1);
			scale = rsmd.getScale(i+1);
			columnName = rsmd.getColumnName(i + 1);
			columnName = StringUtils.prefixToUpperNot(columnName,prefix);

			System.out.println(columnName+" Precision: "+ precision+" Scale: "+scale+" columnType: "+ type);

			if(type == Types.CHAR || type == Types.VARCHAR || type == Types.LONGVARCHAR || type ==Types.NVARCHAR || type==Types.OTHER){
				buffer.append("\tprivate String "+ columnName + ";\n");
				gsMethod.append(StringUtils.createMethod(columnName, "String"));
			}else if (type==Types.NUMERIC){
				if(scale ==0){
					buffer.append("\tprivate Integer "+ columnName + ";\n");
					gsMethod.append(StringUtils.createMethod(columnName, "Integer"));
				} else{
					buffer.append("\tprivate Double "+ columnName + ";\n");
					gsMethod.append(StringUtils.createMethod(columnName, "Double"));
				}
			}else if (type == Types.SMALLINT){
				buffer.append("\tprivate Short "+ columnName + ";\n");
				gsMethod.append(StringUtils.createMethod(columnName, "Short"));
			}else if (type == Types.INTEGER){
				buffer.append("\tprivate Integer "+ columnName + ";\n");
				gsMethod.append(StringUtils.createMethod(columnName, "Integer"));
			}else if (type==Types.DECIMAL){
				buffer.append("\tprivate Double "+ columnName + ";\n");
				gsMethod.append(StringUtils.createMethod(columnName, "Double"));
			}else if (type == Types.DATE){
				buffer.append("\tprivate Date "+ columnName + ";\n");
				gsMethod.append(StringUtils.createMethod(columnName, "Date"));
			}else if (type == Types.TIMESTAMP){
				buffer.append("\tprivate Date "+ columnName + ";\n");
				gsMethod.append(StringUtils.createMethod(columnName, "Date"));
				System.out.println("TIMESTAMP");

			}else if (type == Types.DOUBLE){
				buffer.append("\tprivate Double "+ columnName + ";\n");
			}
		}
		
		buffer.append(gsMethod.toString());
		buffer.append("}");

		System.out.println(buffer);

		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			System.out.println("Exception: " + e.getMessage());

		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
