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

    public Employer(String fullName, LocalDate birthDate, String phone, String position) {
        this.id = nextId++;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.position = position;
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
}