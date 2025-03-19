package com.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	final static String user = "root";
	final static String password = "password";
	final static String url = "jdbc:mysql://localhost:3306/calendarapi";
	public static Connection getConnection() throws SQLException,ClassNotFoundException{
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(url,user,password);
	}
}
