package school.hei.prog3td2.util;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL = dotenv.get("JDBC_URL");
    private static final String USER = dotenv.get("USERNAME");
    private static final String PASSWORD = dotenv.get("PASSWORD");

    public Connection getDBConnection() throws SQLException {
        Connection connection =
                DriverManager.getConnection(URL, USER, PASSWORD);
        return connection;
    }
}
