import util.DevLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DATABASE_URL = "jdbc:sqlite:data/webbutiken.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            DevLogger.logError(e);
            System.out.println("We're having trouble connecting to the database. Please try again later.");
            throw new SQLException("Unable to load the SQLite JDBC driver: " + e.getMessage(), e);
        }

        return DriverManager.getConnection(DATABASE_URL);
    }
}
