package com.iteratrlearning.shu_book.chapter_06.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class DatabaseConnection {
    static {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
    }

    static Connection get() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:db/mydatabase", "SA", "");
    }
}
