package main.java.SohuSpider.count;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import main.java.SohuSpider.util.DBStatement;

/*
 * 查询数据库中新闻条目的数量 
 */

public class Counter {
	//数据库连接
	static Connection con = DBStatement.getCon();
		
	static Statement stmt = DBStatement.getInstance();
	
	static String sqlCount = "select count(*) from news_info";
	static void monitor(){
		while (true) {
			try {
				ResultSet rs = stmt.executeQuery(sqlCount);
				
				/**
				 * 一定要先将结果集指针移动到第一行
				 */
				rs.next(); 
				System.out.println(rs.getInt(1));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(5000); //每隔5s查询一次
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
		
	}
	
	public static void main(String[] args){
		monitor();
	}
}
