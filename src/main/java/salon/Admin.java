package salon;

public class Admin {
    private int id;
    private String login;
    private String fullName;
    private String email;
    private String phone;
    private String createdAt;
    private String lastLogin;

    // Конструктор
    public Admin(int id, String login, String fullName, String email, String phone, String createdAt, String lastLogin) {
        this.id = id;
        this.login = login;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    // Геттеры
    public int getId() { return id; }
    public String getLogin() { return login; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCreatedAt() { return createdAt; }
    public String getLastLogin() { return lastLogin; }

    // Сеттеры (если нужны)
    public void setId(int id) { this.id = id; }
    public void setLogin(String login) { this.login = login; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }
}
