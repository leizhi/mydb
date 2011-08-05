package com.mooo.mycoz.db;

import java.sql.Connection;   
import java.sql.DatabaseMetaData;   
import java.sql.ParameterMetaData;   
import java.sql.PreparedStatement;   
import java.sql.ResultSet;   
import java.sql.SQLException;   
import java.sql.Statement;   
  
//import dataSource.DataSource;   
  
/**  
 * 简单数据库连接工具类  
 * 单例模式  
 * @author kevin.wangwei  
 * Email:wanwei.234@163.com  
 * 2009-12-26  
 */  
public final class JDBCUtil {   
   /**当前对象引用*/  
   private static JDBCUtil instance=null;   
   /**数据源对象*/  
  // private  DataSource dataSource=null;   
   /**定义一个私有的构造函数，禁止创建该对象实例*/  
   private  JDBCUtil(){   
      // dataSource=new DataSource();   
   }   
   /**  
    * 或得该工具类对象  
    * @return JdbcInstanceUtil对象  
    * @throws SQLException   
    */  
   public static JDBCUtil getInstance(){   
       if(instance==null){   
           synchronized(JDBCUtil.class){   
           if(instance==null){//如果没有这个判断，当两个线程同时访问这个方法会出现创建两个对象   
           instance=new JDBCUtil();   
           }   
       }   
   }   
       return instance;   
   }   
   /**  
    * 获得数据库连接对象  
    * @return 数据库连接对象  
    * @throws SQLException  
    */  
  // public  Connection getConnection() throws SQLException{   
   //     return dataSource.getConnection();   
   //}   
   /**  
    * TYPE_SCROLL_SENSITIVE jdbc规范：查出来结果要可以感应到数据的变化  
    * TYPE_SCROLL_INSENSITIVE 不必感应到数据库变化  
    * 获得可以滚动结果集---即结果集的指针可以上下移动来读取数据，以及定位来读取数据  
    * @param conn 数据库连接  
    * @return Statement对象  
    * @throws SQLException  
    */  
   public Statement scrollResultSet(Connection conn) throws SQLException{   
       if(conn==null){   
           return null;   
       }   
       Statement st=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);   
       return st;   
   }   
   /**  
    * 获得PreparedStatement对象只读结果集  
    * @param sql  数据库 sql语句  
    * @return PreparedStatement 只读结果集  
    * @throws SQLException  
    */  
   public PreparedStatement scrollReadOnlyResultSet(String sql,Connection conn)throws SQLException{   
       if(conn==null){   
           return null;   
       }   
       PreparedStatement st=conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);   
       return st;   
   }   
   /**  
    * 获得可更新结果集---这种更新结果集一般不用, 用法：先定位到某一行，调用rs.updateString(1,900)再调用rs.updateRow();  
    * @param conn 数据库连接  
    * @return Statement对象  
    * @throws SQLException  
    */  
   public  Statement updateResultSet(Connection conn) throws SQLException{   
       if(conn==null){   
           return null;   
       }   
       Statement st=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);   
       return st;   
   }   
   /**  
    * 获得PreparedStatement类中参数元信息，封装的sql语句信息（？数目，字段类型等）  
    * @param ps PreparedStatement  
    * @return ParameterMetaData  
    * @throws SQLException  
    */  
   public int getPreparedStatementMetaDatan(PreparedStatement ps) throws SQLException{   
        if(ps==null){   
            return 0;   
        }   
        ParameterMetaData pmd=ps.getParameterMetaData();   
        return pmd.getParameterCount();   
    }   
    /**  
     * 获得数据库一些基本信息 数据库名称、版本、是否支持事务  
     * @param conn Connection  
     * @throws SQLException   
     */  
    public  void getDataBaseMessage(Connection conn) throws SQLException{   
        if(conn==null){   
            System.out.println("当前连接不存在！");   
        }   
        DatabaseMetaData dbmd=conn.getMetaData();   
        String whether=dbmd.supportsTransactions()?"":"不";   
        System.out.println("你当前使用数据库为："+dbmd.getDatabaseProductName());   
        System.out.println("数据库版本为："+dbmd.getDatabaseProductVersion());   
        System.out.println("该数据库"+whether+"支持事务");   
    }   
    /**  
     * 释放资源   
     * 保证程序在出现任何异常都要关闭连接对象  
     * @param rs 结果集  
     * @param st statement  
     * @param conn 连接  
     */  
    public void free(ResultSet rs,Statement st,Connection conn){   
            try {   
                if(rs!=null){   
                    rs.close();   
                }   
            } catch (SQLException e) {   
                e.printStackTrace();   
            }finally{   
                try {   
                    if(st!=null){   
                        st.close();   
                    }   
                } catch (SQLException e) {   
                    e.printStackTrace();   
                }finally{   
                    try {   
                        //if(conn!=null){   
                        //    dataSource.closeConnection(conn);   
                       // }   
                    } catch (Exception e) {   
                        e.printStackTrace();   
                    }   
                }   
            }   
           
    }   
}  