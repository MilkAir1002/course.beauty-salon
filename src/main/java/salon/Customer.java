package salon;

public class Customer {
    private int id;
    private String lastName;
    private String firstName;
    private String patronymic;
    private String phone;
    private String email;
    private String login;
    private String gender;
    private String password;

    // Конструктор для создания нового клиента
    public Customer(String lastName, String firstName, String patronymic,
                    String phone, String email, String login, String gender, String password) {
        this.id = -1;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.phone = phone;
        this.email = email;
        this.login = login;
        this.gender = gender;
        this.password = password;
    }

    // Конструктор для редактирования клиента (сохраняется старый id)
    public Customer(int id, String lastName, String firstName, String patronymic,
                    String phone, String email, String login, String gender, String password) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.phone = phone;
        this.email = email;
        this.login = login;
        this.gender = gender;
        this.password = password;
    }

    // Геттеры
    public int getId() { return id; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getPatronymic() { return patronymic; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getLogin() { return login; }
    public String getGender() { return gender; }
    public String getPassword() { return password; }

    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setPatronymic(String patronymic) { this.patronymic = patronymic; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setLogin(String login) { this.login = login; }
    public void setGender(String gender) { this.gender = gender; }
    public void setPassword(String password) { this.password = password; }

    // Полное ФИО для отображения
    public String getFullName() {
        return lastName + " " + firstName + " " + patronymic;
    }
}