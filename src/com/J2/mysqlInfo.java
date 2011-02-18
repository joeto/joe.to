package com.J2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class mysqlInfo {
	public mysqlInfo(String us,String pa, String da){
		user=us;
		pass=pa;
		db=da;
	}
	public String user(){
		return user;
	}
	public String pass(){
		return pass;
	}
	public String db(){
		return db;
	}
	public Connection getConnection() {
		try {
			return DriverManager.getConnection(db + "?autoReconnect=true&user=" + user + "&password=" + pass);
		} catch (SQLException ex) {

		}
		return null;
	}
	private String user,pass,db;
}
