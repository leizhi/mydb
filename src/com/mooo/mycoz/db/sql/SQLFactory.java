package com.mooo.mycoz.db.sql;

import com.mooo.mycoz.db.DbConfig;

public class SQLFactory {
	private static Object initLock = new Object();
	
	private static String className = "com.mooo.mycoz.db.sql.MysqlSQL";
	private static ProcessSQL factory = null;

	public static ProcessSQL getInstance() {
		synchronized (initLock) {
			String classNameProp = DbConfig.getProperty("SQLFactory.className");
			if (classNameProp != null) {
				className = classNameProp;
			}
			try {
				// Load the class and create an instance.
				Class<?> c = Class.forName(className);
				factory = (ProcessSQL) c.newInstance();
			} catch (Exception e) {
				System.err.println("Failed to load ForumFactory class " + className
						+ ". Yazd cannot function normally.");
				e.printStackTrace();
				return null;
			}
		}
		return factory;
	}
	
//	public static ProcessSQL getInstance() {
//		if(factory==null){
//			synchronized (initLock) {
//				String classNameProp = DbConfig.getProperty("SQLFactory.className");
//				if (classNameProp != null) {
//					className = classNameProp;
//				}
//				try {
//					// Load the class and create an instance.
//					Class<?> c = Class.forName(className);
//					factory = (ProcessSQL) c.newInstance();
//				} catch (Exception e) {
//					System.err.println("Failed to load ForumFactory class " + className
//							+ ". Yazd cannot function normally.");
//					e.printStackTrace();
//					return null;
//				}
//			}
//		}
//		return factory;
//	}
}
