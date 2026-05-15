package salon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Appointment {
    private int id;
    private LocalDate date;
    private String time;
    private String client;
    private String master;
    private String services;
    private double cost;


    // Конструктор для создания новой записи
    public Appointment(LocalDate date, String time, String client, String master, String services, double cost) {
        this.date = date;
        this.time = time;
        this.client = client;
        this.master = master;
        this.services = services;
        this.cost = cost;
    }

    // Конструктор для редактирования записи
    public Appointment(int id, LocalDate date, String time, String client, String master, String services, double cost) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.client = client;
        this.master = master;
        this.services = services;
        this.cost = cost;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    // Форматированная дата для отображения в таблице
    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

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