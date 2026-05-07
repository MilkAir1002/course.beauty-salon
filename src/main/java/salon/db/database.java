package salon.db;

import java.sql.*;

public class database {
    public static final String url = "jdbc:sqlite:salon.db";
    public static String curLog = null; // Логин нынешнего юзера

    public static boolean loginClientDB(String inputLogin, String inputPass) {
        String query = "SELECT password FROM clients WHERE login = ?";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, inputLogin);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");

                if (dbPassword.equals(inputPass)) {
                    curLog = inputLogin;
                    System.out.println("Вход разрешен!");
                    return true;
                } else {
                    System.out.println("Неверный пароль.");
                    return false;
                }
            } else {
                System.out.println("Пользователь не найден.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean loginAdminDB(String inputLogin, String inputPass) {
        String query = "SELECT password FROM admins WHERE login = ?";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, inputLogin);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");

                if (dbPassword.equals(inputPass)) {
                    curLog = inputLogin;
                    System.out.println("Вход разрешен!");
                    return true;
                } else {
                    System.out.println("Неверный пароль.");
                    return false;
                }
            } else {
                System.out.println("Пользователь не найден.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerUser(String firstName, String lastName, String patronymic,
                                String phone, String email, String login,
                                String gender, String password) {

        String checkSql = "SELECT count(*) FROM clients WHERE login = ?";
        String insertSql = "INSERT INTO clients (first_name, last_name, patronymic, phone, email, login, gender, password) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(database.url)) {

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, login);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Ошибка: Логин '" + login + "' уже занят.");
                    return false;
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, patronymic);
                pstmt.setString(4, phone);
                pstmt.setString(5, email);
                pstmt.setString(6, login);
                pstmt.setString(7, gender);
                pstmt.setString(8, password);

                int rows = pstmt.executeUpdate();
                return rows > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
