package salon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Customer {
    private int id;
    private String fullName;
    private LocalDate birthDate;
    private String phone;
    private String password;

    // Конструктор для создания нового сотрудника
    public Customer(String fullName, LocalDate birthDate, String phone, String password) {
        this.id = -1;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.password = password;
    }
    // Конструктор для редактирования сотрудника (сохраняется старый id)
    public Customer(int id, String fullName, LocalDate birthDate, String phone, String password) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.password = password;
    }

    // Геттеры
    public int getId() {return id;}
    public String getFullName() {return fullName;}
    public LocalDate getBirthDate() {return birthDate;}
    public String getPhone() {return phone;}
    public String getPassword() {return password;}
    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }

    // Форматированная дата для отображения в таблице
    public String getFormattedBirthDate() {
        return birthDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    // Переопределяем equals и hashCode для корректного поиска в списке
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return id == customer.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
