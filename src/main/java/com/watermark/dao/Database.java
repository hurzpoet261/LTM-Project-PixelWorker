package com.watermark.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // *** THAY ĐỔI THÔNG TIN NÀY CHO PHÙ HỢP VỚI MÁY BẠN ***
    private static final String URL = "jdbc:mysql://localhost:3306/watermark_db";
    private static final String USER = "***";       // Thay bằng user MySQL của bạn
    private static final String PASS = "***";   // Thay bằng mật khẩu MySQL của bạn

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
}