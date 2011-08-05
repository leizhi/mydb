package com.mooo.mycoz.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class ReflectUtil {

	public static Class<?> clazz;

	public static List<String> getMethodNames(String className) {
		List<String> names = new ArrayList<String>();

		try {

			clazz = Class.forName(className);

			Method[] methods = clazz.getDeclaredMethods();

			for (Method method : methods) {
				names.add(method.getName());
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return names;

	}

	public static List<String> getMethodNames(Class<?> clazz) {

		List<String> names = new ArrayList<String>();
		Method[] methods = clazz.getDeclaredMethods();

		for (Method method : methods) {
			names.add(method.getName());
		}
		
		return names;
	}

	public void getBean(){
		try {
			Object obj = Class.forName("com.mooo.cxrd.Dbobj").newInstance();
			Class<? extends Object> cls = obj.getClass();
			Class<?>[] paraTypes = new Class[] { Object.class,Object.class };
			try {
				Method method = cls.getMethod("methodName", paraTypes);
				Object paraValues[] = new Object[] { new Object(), new Object() };
				
				try {
					//Object rObj = method.invoke(obj, paraValues);
					
					method.invoke(obj, paraValues);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
