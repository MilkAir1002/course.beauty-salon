package salon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Employer {
    private int id;
    private String fullName;
    private LocalDate birthDate;
    private String phone;
    private String position;
    private String details;
    private int serviceId;

    // Конструктор для создания нового сотрудника
    public Employer(String fullName, LocalDate birthDate, String phone, String details, String position) {
        this.id = -1;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.details = details;
        this.position = position;
        this.serviceId = getServiceIdByPosition(position);
    }
    // Конструктор для редактирования сотрудника (сохраняется старый id)
    public Employer(int id, String fullName, LocalDate birthDate, String phone, String details, String position) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.details = details;
        this.position = position;
        this.serviceId = getServiceIdByPosition(position);
    }

    // Вспомогательный метод для определения service_id по должности
    private int getServiceIdByPosition(String position) {
        switch (position) {
            case "Парикмахер":
                return 1;
            case "Мастер маникюра":
                return 2;
            case "Косметолог":
                return 3;
            case "Массажист":
                return 4;
            case "Визажист":
                return 5;
            default:
                return 0;
        }
    }

    // Геттеры
    public int getId() {return id;}
    public String getFullName() {return fullName;}
    public LocalDate getBirthDate() {return birthDate;}
    public String getPhone() {return phone;}
    public String getDetails() {return details;}
    public String getPosition() {return position;}
    public int getServiceId() {return serviceId;}

    // Сеттеры
    public void setId(int id) {this.id = id;}
    public void setFullName(String fullName) {this.fullName = fullName;}
    public void setBirthDate(LocalDate birthDate) {this.birthDate = birthDate;}
    public void setPhone(String phone) {this.phone = phone;}
    public void setDetails(String details) {this.phone = details;}
    public void setPosition(String position) {
        this.position = position;
        this.serviceId = getServiceIdByPosition(position); // Обновляем serviceId при смене должности
    }
    public void setServiceId(int serviceId) {this.serviceId = serviceId;}

    // Форматированная дата для отображения в таблице
    public String getFormattedBirthDate() {
        return birthDate != null ? birthDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "";
    }

    // Переопределяем equals и hashCode для корректного поиска в списке
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employer employer = (Employer) obj;
        return id == employer.id && id != -1; // Сравниваем по ID, если он не временный
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return fullName + " (" + position + ")";
    }
}