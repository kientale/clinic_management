package com.kien.project.clinicmanagement.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionDatabase {
	
	public static Connection getConnection() throws Exception {
		String url = "jdbc:mysql://localhost:3306/clinic_management";
		String user = "root";
		String password = "230104";
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(url, user, password);
	}
}
