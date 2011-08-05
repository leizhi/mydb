package com.mooo.mycoz.db;

public class DbFactory {
//	private static Object initLock = new Object();
	private static String className = "com.mooo.mycoz.db.DbMysql";
	private static DbProcess factory = null;

	public static DbProcess getInstance() {
		String classNameProp = DbConfig.getProperty("DbFactory.className");
		if (classNameProp != null) {
			className = classNameProp;
		}
		try {
			// Load the class and create an instance.
			Class<?> c = Class.forName(className);
			factory = (DbProcess) c.newInstance();
		} catch (Exception e) {
			System.err.println("Failed to load ForumFactory class " + className
					+ ". Yazd cannot function normally.");
			e.printStackTrace();
			return null;
		}
		return factory;
	}
//	public static DbProcess getInstance() {
//		if (factory == null) {
//			synchronized (initLock) {
//				if (factory == null) {
//					String classNameProp = PropertyManager.getProperty("DbFactory.className");
//					if (classNameProp != null) {
//						className = classNameProp;
//					}
//					try {
//						// Load the class and create an instance.
//						Class<?> c = Class.forName(className);
//						factory = (DbProcess) c.newInstance();
//					} catch (Exception e) {
//						System.err.println("Failed to load ForumFactory class "
//								+ className
//								+ ". Yazd cannot function normally.");
//						e.printStackTrace();
//						return null;
//					}
//				}
//			}
//		}
//		return factory;
//	}
	

//	public static void refresh(Object entity) {
//		if (factory != null) {
//			synchronized (initLock) {
//				if (factory != null) {
//					factory.refresh(entity);
//				}
//			}
//		}
//	}
}
