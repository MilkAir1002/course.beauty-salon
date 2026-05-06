package salon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Appointment {
    private static int nextId = 1;
    private int id;
    private LocalDate date;
    private String time;
    private String client;
    private String master;
    private String services;
    private double cost;

    // Конструктор для создания нового сотрудника
    public Appointment(LocalDate date, String time, String client, String master, String services, double cost) {
        this.id = nextId++;
        this.date = date;
        this.time = time;
        this.client = client;
        this.master = master;
        this.services = services;
        this.cost = cost;
    }
    // Конструктор для редактирования сотрудника (сохраняется старый id)
    public Appointment(int id, LocalDate date, String time, String client, String master, String services, double cost) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.client = client;
        this.master = master;
        this.services = services;
        this.cost = cost;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String Time() {
        return time;
    }

    public String getClient() {
        return client;
    }

    public String getMaster() {
        return master;
    }

    public String getServices() {
        return services;
    }

    public double getCost() {
        return cost;
    }

    // Форматированная дата для отображения в таблице
    public String getFormattedBirthDate() {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    // Переопределяем equals и hashCode для корректного поиска в списке
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Appointment appointment = (Appointment) obj;
        return id == appointment.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
