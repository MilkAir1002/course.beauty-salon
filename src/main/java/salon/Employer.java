package salon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Employer {
    private static int nextId = 1;
    private int id;
    private String fullName;
    private LocalDate birthDate;
    private String phone;
    private String position;

    // НОВЫЙ БЛОК: Конструктор для создания нового сотрудника
    public Employer(String fullName, LocalDate birthDate, String phone, String position) {
        this.id = nextId++;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.position = position;
    }

    // НОВЫЙ БЛОК: Конструктор для редактирования сотрудника (с указанием ID)
    public Employer(int id, String fullName, LocalDate birthDate, String phone, String position) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.position = position;
        // Обновляем nextId, если нужно (чтобы новые сотрудники получали следующий ID)
        if (id >= nextId) {
            nextId = id + 1;
        }
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

    public String getPosition() {
        return position;
    }

    // Форматированная дата для отображения в таблице
    public String getFormattedBirthDate() {
        return birthDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    // НОВЫЙ БЛОК: Переопределяем equals и hashCode для корректного поиска в списке
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employer employer = (Employer) obj;
        return id == employer.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}