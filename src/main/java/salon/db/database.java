package salon.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import salon.*;

import java.time.LocalDate;

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
        public int getId() {
            return id;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getAppointmentDate() {
            return appointmentDate;
        }

        public String getSpecialist() {
            return specialist;
        }

        public double getPrice() {
            return price;
        }

        public String getStatus() {
            return status;
        }
    }

    // Класс для хранения данных о записи (для администратора)
    public static class AdminAppointment {
        private final int id;
        private final String clientLogin;
        private final String serviceName;
        private final String appointmentDate;
        private final String specialist;
        private final double price;
        private final String status;

        public AdminAppointment(int id, String clientLogin, String serviceName,
                                String appointmentDate, String specialist,
                                double price, String status) {
            this.id = id;
            this.clientLogin = clientLogin;
            this.serviceName = serviceName;
            this.appointmentDate = appointmentDate;
            this.specialist = specialist;
            this.price = price;
            this.status = status;
        }

        public int getId() { return id; }
        public String getClientLogin() { return clientLogin; }
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

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getPatronymic() {
            return patronymic;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public String getLogin() {
            return login;
        }

        public String getGender() {
            return gender;
        }

        public String getPassword() {
            return password;
        }
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

    public static boolean loginAdminDB(String login, String password) {
        String query = "SELECT password FROM admins WHERE login = ?"; // запрос в таблицу админов

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");

                if (dbPassword.equals(password)) {
                    curLog = login;
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

    // Получить всех администраторов
    public static ObservableList<Admin> getAllAdmins() {
        ObservableList<Admin> admins = FXCollections.observableArrayList();
        String query = "SELECT id, login, full_name, email, phone, created_at, last_login FROM admins ORDER BY id";

        try (Connection conn = DriverManager.getConnection(database.url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                admins.add(new Admin(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("created_at"),
                        rs.getString("last_login")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
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

    public static int getCurrentClientSuccessfulAppointmentsCount() {
        if (curLog == null) {
            return 0;
        }

        String query = "SELECT COUNT(*) FROM appointments WHERE client_login = ? " +
                "AND (LOWER(COALESCE(status, '')) = ? " +
                "OR (LOWER(COALESCE(status, '')) <> ? AND date(appointment_date) < date('now', 'localtime')))";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, curLog);
            pstmt.setString(2, "исполнено");
            pstmt.setString(3, "отменено");

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static boolean hasCurrentClientDiscount() {
        return getCurrentClientSuccessfulAppointmentsCount() >= 5;
    }

    public static double getPriceWithCurrentClientDiscount(double price) {
        return hasCurrentClientDiscount() ? price * 0.9 : price;
    }

    public static ObservableList<Service> getServicesCatalog() {
        ObservableList<Service> services = FXCollections.observableArrayList();
        String query = "SELECT id, name, category, duration, price, service_id, description FROM services ORDER BY category, name";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Добавляем каждую услугу в список
                Service service = new Service(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("duration"),
                        rs.getDouble("price"),
                        rs.getString("description")
                );
                service.setServiceId(rs.getInt("service_id"));
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            services.clear(); // Если ошибка чистим каталог
        }

        return services;
    }

    public static ObservableList<Service> getServicesWithEmployeeAbilities() {
        ObservableList<Service> services = FXCollections.observableArrayList();

        String query = "SELECT DISTINCT id, name, category, duration, price, service_id, description FROM services ORDER BY category, name";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Service service = new Service(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("duration"),
                        rs.getDouble("price"),
                        rs.getString("description")
                );
                service.setServiceId(rs.getInt("service_id"));
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            services.clear();
        }

        return services;
    }

    public static ObservableList<String> getSpecialistsByServiceId(int serviceId) {
        ObservableList<String> specialists = FXCollections.observableArrayList();

        String query = "SELECT full_name FROM employees WHERE service_id = ? ORDER BY full_name";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, serviceId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                specialists.add(rs.getString("full_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            specialists.clear();
        }

        return specialists;
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
            pstmt.setDouble(5, getPriceWithCurrentClientDiscount(service.getPrice()));
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

    // Получить всех сотрудников из БД
    public static ObservableList<Employer> getAllEmployees() {
        ObservableList<Employer> employers = FXCollections.observableArrayList();
        String query = "SELECT id, full_name, birth_date, phone, details, position, service_id FROM employees ORDER BY id";

        try (Connection conn = DriverManager.getConnection(database.url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String birthDateStr = rs.getString("birth_date");
                LocalDate birthDate;

                // Пробуем разные форматы даты
                try {
                    // Сначала пробуем стандартный формат YYYY-MM-DD
                    birthDate = LocalDate.parse(birthDateStr);
                } catch (java.time.format.DateTimeParseException e1) {
                    try {
                        // Пробуем формат с точками DD.MM.YYYY
                        java.time.format.DateTimeFormatter formatter =
                                java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy");
                        birthDate = LocalDate.parse(birthDateStr, formatter);
                    } catch (java.time.format.DateTimeParseException e2) {
                        // Если оба формата не подходят, выводим ошибку и пропускаем запись
                        System.err.println("Не удалось распарсить дату: " + birthDateStr);
                        continue;
                    }
                }

                Employer employer = new Employer(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        birthDate,
                        rs.getString("phone"),
                        rs.getString("details"),
                        rs.getString("position")
                );
                employer.setServiceId(rs.getInt("service_id"));
                employers.add(employer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employers;
    }

    // Добавить нового сотрудника
    public static boolean addEmployee(Employer employer) {
        String query = "INSERT INTO employees (full_name, birth_date, phone, details, position, service_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, employer.getFullName());
            // Сохраняем дату в формате YYYY-MM-DD
            pstmt.setString(2, employer.getBirthDate().toString()); // Это даст формат YYYY-MM-DD
            pstmt.setString(3, employer.getPhone());
            pstmt.setString(4, employer.getDetails());
            pstmt.setString(5, employer.getPosition());
            pstmt.setInt(6, employer.getServiceId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    employer.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Обновить данные сотрудника
    public static boolean updateEmployee(Employer employer) {
        String query = "UPDATE employees SET full_name = ?, birth_date = ?, phone = ?, details = ?, position = ?, service_id = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, employer.getFullName());
            // Сохраняем дату в формате YYYY-MM-DD
            pstmt.setString(2, employer.getBirthDate().toString());
            pstmt.setString(3, employer.getPhone());
            pstmt.setString(4, employer.getDetails());
            pstmt.setString(5, employer.getPosition());
            pstmt.setInt(6, employer.getServiceId());
            pstmt.setInt(7, employer.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Удалить сотрудника
    public static boolean deleteEmployee(int employerId) {
        String query = "DELETE FROM employees WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, employerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Получить всех клиентов из БД
    public static ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        String query = "SELECT id, last_name, first_name, patronymic, phone, email, login, gender, password FROM clients ORDER BY id";

        try (Connection conn = DriverManager.getConnection(database.url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("id"),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getString("patronymic"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("gender"), // Здесь будет "M" или "F" из базы
                        rs.getString("password")
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    // Добавить нового клиента
    public static boolean addCustomer(Customer customer) {
        String query = "INSERT INTO clients (last_name, first_name, patronymic, phone, email, login, gender, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customer.getLastName());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getPatronymic());
            pstmt.setString(4, customer.getPhone());
            pstmt.setString(5, customer.getEmail());
            pstmt.setString(6, customer.getLogin());
            pstmt.setString(7, customer.getGender());
            pstmt.setString(8, customer.getPassword());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    customer.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Обновить данные клиента
    public static boolean updateCustomer(Customer customer) {
        String query = "UPDATE clients SET last_name = ?, first_name = ?, patronymic = ?, phone = ?, email = ?, login = ?, gender = ?, password = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customer.getLastName());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getPatronymic());
            pstmt.setString(4, customer.getPhone());
            pstmt.setString(5, customer.getEmail());
            pstmt.setString(6, customer.getLogin());
            pstmt.setString(7, customer.getGender());
            pstmt.setString(8, customer.getPassword());
            pstmt.setInt(9, customer.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Удалить клиента
    public static boolean deleteCustomer(int customerId) {
        String query = "DELETE FROM clients WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, customerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Получить все услуги из БД
    public static ObservableList<Service> getAllServices() {
        ObservableList<Service> services = FXCollections.observableArrayList();
        String query = "SELECT id, name, category, duration, price, service_id, description FROM services ORDER BY id";

        try (Connection conn = DriverManager.getConnection(database.url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Service service = new Service(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("duration"),
                        rs.getDouble("price"),
                        rs.getString("description")
                );
                service.setServiceId(rs.getInt("service_id"));
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    // Добавить новую услугу
    public static boolean addService(Service service) {
        String query = "INSERT INTO services (name, category, duration, price, service_id, description) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, service.getName());
            pstmt.setString(2, service.getCategory());
            pstmt.setString(3, service.getDuration());
            pstmt.setDouble(4, service.getPrice());
            pstmt.setInt(5, service.getServiceId());
            pstmt.setString(6, service.getDescription());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    service.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Обновить данные услуги
    public static boolean updateService(Service service) {
        String query = "UPDATE services SET name = ?, category = ?, duration = ?, price = ?, service_id = ?, description = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, service.getName());
            pstmt.setString(2, service.getCategory());
            pstmt.setString(3, service.getDuration());
            pstmt.setDouble(4, service.getPrice());
            pstmt.setInt(5, service.getServiceId());
            pstmt.setString(6, service.getDescription());
            pstmt.setInt(7, service.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Удалить услугу
    public static boolean deleteService(int serviceId) {
        String query = "DELETE FROM services WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, serviceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Получить все записи
    public static ObservableList<Appointment> getAllAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        String query = "SELECT id, client_login, service_id, service_name, appointment_date, specialist, price, status " +
                "FROM appointments ORDER BY appointment_date DESC, id DESC";

        try (Connection conn = DriverManager.getConnection(database.url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String appointmentDate = rs.getString("appointment_date");
                String status = rs.getString("status");

                // Проверяем, прошла ли дата и статус не "отменено"
                if (isDatePassed(appointmentDate) && !"отменено".equals(status)) {
                    status = "исполнено";
                }

                appointments.add(new Appointment(
                        rs.getInt("id"),
                        rs.getString("client_login"),
                        rs.getInt("service_id"),
                        rs.getString("service_name"),
                        appointmentDate,
                        rs.getString("specialist"),
                        rs.getDouble("price"),
                        status
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    // Вспомогательный метод для проверки даты
    private static boolean isDatePassed(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return false;

        try {
            LocalDate appointmentDate = LocalDate.parse(dateStr); // Формат YYYY-MM-DD
            LocalDate today = LocalDate.now();
            return appointmentDate.isBefore(today);
        } catch (Exception e) {
            return false;
        }
    }

    // Добавить новую запись
    public static boolean addAppointment(Appointment appointment) {
        String query = "INSERT INTO appointments (client_login, service_id, service_name, appointment_date, specialist, price, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, appointment.getClientLogin());
            pstmt.setInt(2, appointment.getServiceId());
            pstmt.setString(3, appointment.getServiceName());
            pstmt.setString(4, appointment.getAppointmentDate());
            pstmt.setString(5, appointment.getSpecialist());
            pstmt.setDouble(6, appointment.getPrice());
            pstmt.setString(7, appointment.getStatus());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    appointment.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Редактировать запись
    public static boolean updateAppointment(Appointment appointment) {
        String query = "UPDATE appointments SET client_login = ?, service_id = ?, service_name = ?, appointment_date = ?, " +
                "specialist = ?, price = ?, status = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, appointment.getClientLogin());
            pstmt.setInt(2, appointment.getServiceId());
            pstmt.setString(3, appointment.getServiceName());
            pstmt.setString(4, appointment.getAppointmentDate());
            pstmt.setString(5, appointment.getSpecialist());
            pstmt.setDouble(6, appointment.getPrice());
            pstmt.setString(7, appointment.getStatus());
            pstmt.setInt(8, appointment.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Удалить запись
    public static boolean deleteAppointment(int appointmentId) {
        String query = "DELETE FROM appointments WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, appointmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
