package salon.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import salon.Service;

import java.sql.*;

public class database {
    public static final String url = "jdbc:sqlite:salon.db"; // путь к файлу базы данных
    public static String curLog = null; // Логин нынешнего юзера

    // Класс для хранения данных о записи клиента
    public static class ClientAppointment {
        private final int id;
        private final String serviceName;
        private final String appointmentDate;
        private final String specialist;
        private final double price;
        private final String status;

        // конструктор
        public ClientAppointment(int id, String serviceName, String appointmentDate,
                                 String specialist, double price, String status) {
            this.id = id;
            this.serviceName = serviceName;
            this.appointmentDate = appointmentDate;
            this.specialist = specialist;
            this.price = price;
            this.status = status;
        }

        // Геттеры, чтобы получать данные из объекта записи
        public int getId() { return id; }
        public String getServiceName() { return serviceName; }
        public String getAppointmentDate() { return appointmentDate; }
        public String getSpecialist() { return specialist; }
        public double getPrice() { return price; }
        public String getStatus() { return status; }
    }

    // Класс для хранения данных профиля клиента
    public static class ClientProfile {
        private final String firstName;
        private final String lastName;
        private final String patronymic;
        private final String phone;
        private final String email;
        private final String login;
        private final String gender;
        private final String password;

        public ClientProfile(String firstName, String lastName, String patronymic,
                             String phone, String email, String login,
                             String gender, String password) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.patronymic = patronymic;
            this.phone = phone;
            this.email = email;
            this.login = login;
            this.gender = gender;
            this.password = password;
        }

        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPatronymic() { return patronymic; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getLogin() { return login; }
        public String getGender() { return gender; }
        public String getPassword() { return password; }
    }

    public static boolean loginClientDB(String inputLogin, String inputPass) {
        String query = "SELECT password FROM clients WHERE login = ?"; // запрос пароля по логину

        try (Connection conn = DriverManager.getConnection(database.url); // соединяемся с базой
             PreparedStatement pstmt = conn.prepareStatement(query)) { // готовим запрос

            pstmt.setString(1, inputLogin); // подставляем логин в запрос
            ResultSet rs = pstmt.executeQuery(); // получаем результат

            if (rs.next()) {
                String dbPassword = rs.getString("password"); // достаем пароль из базы

                if (dbPassword.equals(inputPass)) { // проверяем, совпал ли пароль
                    curLog = inputLogin; // запоминаем, кто зашел
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
        String query = "SELECT password FROM admins WHERE login = ?"; // запрос в таблицу админов

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

        String checkSql = "SELECT count(*) FROM clients WHERE login = ?"; // запрос для проверки занятости логина
        String insertSql = "INSERT INTO clients (first_name, last_name, patronymic, phone, email, login, gender, password) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; // запрос на добавление нового клиента

        try (Connection conn = DriverManager.getConnection(database.url)) {

            // Сначала проверяем, нет ли уже такого логина
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, login);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Ошибка: Логин '" + login + "' уже занят.");
                    return false;
                }
            }

            // Если логин свободен, вставляем все данные нового пользователя
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, patronymic);
                pstmt.setString(4, phone);
                pstmt.setString(5, email);
                pstmt.setString(6, login);
                pstmt.setString(7, gender);
                pstmt.setString(8, password);

                int rows = pstmt.executeUpdate(); // выполняем запись в таблицу
                return rows > 0; // если добавилась хотя бы одна строка — успех
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getCurrentClientFirstName() {
        ClientProfile profile = getCurrentClientProfile(); // получаем весь профиль
        return profile == null ? null : profile.getFirstName(); // возвращаем только имя
    }

    public static ClientProfile getCurrentClientProfile() {
        if (curLog == null) {
            return null; // если никто не вошел, возвращать нечего
        }

        String query = "SELECT first_name, last_name, patronymic, phone, email, login, gender, password FROM clients WHERE login = ?"; // берем всё о текущем юзере

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, curLog);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // создаем и возвращаем объект с данными из базы
                return new ClientProfile(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("patronymic"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("gender"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean updateCurrentClient(ClientProfile profile) {
        if (curLog == null || profile == null) {
            return false;
        }

        // запрос на обновление данных пользователя по его текущему логину
        String query = "UPDATE clients SET first_name = ?, last_name = ?, patronymic = ?, phone = ?, email = ?, login = ?, gender = ?, password = ? WHERE login = ?";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, profile.getFirstName());
            pstmt.setString(2, profile.getLastName());
            pstmt.setString(3, profile.getPatronymic());
            pstmt.setString(4, profile.getPhone());
            pstmt.setString(5, profile.getEmail());
            pstmt.setString(6, profile.getLogin());
            pstmt.setString(7, profile.getGender());
            pstmt.setString(8, profile.getPassword());
            pstmt.setString(9, curLog); // ищем по старому логину

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                curLog = profile.getLogin(); // обновляем текущий логин в программе, если он изменился
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static ObservableList<Service> getServicesCatalog() {
        ObservableList<Service> services = FXCollections.observableArrayList(); // список для JavaFX
        String query = "SELECT id, name, category, duration, price FROM services ORDER BY category, name"; // берем список услуг

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // добавляем каждую услугу в список
                services.add(new Service(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("duration"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            services.clear(); // если ошибка, чистим список
        }

        return services;
    }

    public static boolean addCurrentClientAppointment(Service service, String appointmentDate, String specialist) {
        if (curLog == null || service == null || appointmentDate == null || specialist == null) {
            return false;
        }
        // запрос на создание новой записи к мастеру
        String query = "INSERT INTO appointments (client_login, service_id, service_name, appointment_date, price, specialist, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, curLog);
            pstmt.setInt(2, service.getId());
            pstmt.setString(3, service.getName());
            pstmt.setString(4, appointmentDate);
            pstmt.setDouble(5, service.getPrice());
            pstmt.setString(6, specialist);
            pstmt.setString(7, "назначена"); // по умолчанию статус новый

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ObservableList<ClientAppointment> getCurrentClientAppointments() {
        ObservableList<ClientAppointment> appointments = FXCollections.observableArrayList();
        if (curLog == null) {
            return appointments;
        }

        // запрос на получение всех записей конкретного клиента (сортируем по дате)
        String query = "SELECT id, service_name, appointment_date, specialist, price, status " +
                "FROM appointments WHERE client_login = ? ORDER BY appointment_date DESC, id DESC";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, curLog);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // заполняем список записей данными из таблицы
                appointments.add(new ClientAppointment(
                        rs.getInt("id"),
                        rs.getString("service_name"),
                        rs.getString("appointment_date"),
                        rs.getString("specialist"),
                        rs.getDouble("price"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            appointments.clear();
        }

        return appointments;
    }

    public static boolean cancelCurrentClientAppointment(int appointmentId) {
        if (curLog == null) {
            return false;
        }

        // меняем статус записи на "отменено" только если она была в статусе "назначена"
        String query = "UPDATE appointments SET status = ? WHERE id = ? AND client_login = ? AND status = ?";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "отменено");
            pstmt.setInt(2, appointmentId);
            pstmt.setString(3, curLog);
            pstmt.setString(4, "назначена");

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}