package salon.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:salon.db";

    // Получить соединение с БД
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC драйвер не найден!", e);
        }
    }

    // Тест подключения
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Соединение с БД выполнено успешно!");
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к БД: " + e.getMessage());
        }
    }
}
