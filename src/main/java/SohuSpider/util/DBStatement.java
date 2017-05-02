package main.java.SohuSpider.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DBStatement {
    static Statement stmt;
    static Connection con;

    private DBStatement(){
        try {
            /**
             * jdbc四大配置参数：
             * 1.driverClassName:com.mysql.jdbc.Driver
             * 2.url:jdbc:mysql://localhost:3306/mydb
             * 3.username:root
             * 4.password:123
             */
            Class.forName("com.mysql.jdbc.Driver");//加载驱动类(注册驱动类)
            String mySqlUrl = "jdbc:mysql://localhost:3306/sohu";
            String username = "root";
            String password = "196214";

            //得到连接对象
            con = DriverManager.getConnection(mySqlUrl, username, password);

            /*对数据库做增、删、改
             * 1.通过Connection对象创建Statement
             *   Statement语句的发送器，它的功能就是向数据库发送sql语句！
             * 2.调用他的int executeUpdate(String sql),返回影响了几行
             */
            //通过Connection 得到Statement;
            stmt = con.createStatement();
        }catch (Exception e){
        }
    }

    private static final DBStatement dbStatement = new DBStatement();

    //静态工厂方法
    public synchronized static Statement getInstance() {
        return dbStatement.stmt;
    }

    //静态工厂方法
    public synchronized static Connection getCon() {
        return dbStatement.con;
    }
    
    /*
    public static void main(String[] args){
  
    	String sql = "select count(*) from news_info";
        try {
			ResultSet rs = stmt.executeQuery(sql);
			//一定要先将结果集指针移动到第一行
			rs.next();
			System.out.println(rs.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    */

}
