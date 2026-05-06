package salon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Customer {
    private static int nextId = 1;
    private int id;
    private String fullName;
    private LocalDate birthDate;
    private String phone;
    private String additionalInfo;

    // Конструктор для создания нового сотрудника
    public Customer(String fullName, LocalDate birthDate, String phone, String additionalInfo) {
        this.id = nextId++;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.additionalInfo = additionalInfo;
    }
    // Конструктор для редактирования сотрудника (сохраняется старый id)
    public Customer(int id, String fullName, LocalDate birthDate, String phone, String additionalInfo) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.additionalInfo = additionalInfo;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

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
