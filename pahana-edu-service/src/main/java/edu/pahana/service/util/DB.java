package edu.pahana.service.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static DB instance;
    private String url, user, pass;

    // Force-register MySQL driver to avoid "No suitable driver" in some servers
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found on classpath", e);
        }
    }

    public static synchronized DB get() {
        if (instance == null) instance = new DB();
        return instance;
    }

    private DB() {
        url  = System.getProperty("DB_URL",
              "jdbc:mysql://localhost:3306/pahana_edu?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        user = System.getProperty("DB_USER", "root");
        pass = System.getProperty("DB_PASS", "");
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}
