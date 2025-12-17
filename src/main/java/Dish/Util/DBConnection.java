package Dish.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    final String URL= System.getenv("JDBC_URL");
    final String USER = System.getenv("USERNAME");
    final String PASSWORD =System.getenv("PASSWORD");
    public  Connection getDBConnection() throws SQLException {
        try {
            System.out.println("connection réussie");
            return DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
}
