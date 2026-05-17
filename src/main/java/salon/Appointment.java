package salon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Appointment {
    private int id;
    private String clientLogin;
    private String serviceName;
    private String appointmentDate;
    private String specialist;
    private double price;
    private String status;

    // Конструктор для создания новой записи
    public Appointment(String clientLogin, String serviceName, String appointmentDate, String specialist, double price, String status) {
        this.id = -1;
        this.clientLogin = clientLogin;
        this.serviceName = serviceName;
        this.appointmentDate = appointmentDate;
        this.specialist = specialist;
        this.price = price;
        this.status = status;
    }

    // Конструктор для редактирования записи
    public Appointment(int id, String clientLogin, String serviceName, String appointmentDate, String specialist, double price, String status) {
        this.id = id;
        this.clientLogin = clientLogin;
        this.serviceName = serviceName;
        this.appointmentDate = appointmentDate;
        this.specialist = specialist;
        this.price = price;
        this.status = status;
    }

    // Геттеры
    public int getId() { return id; }
    public String getClientLogin() { return clientLogin; }
    public String getServiceName() { return serviceName; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getSpecialist() { return specialist; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }

    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setClientLogin(String clientLogin) { this.clientLogin = clientLogin; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }
    public void setSpecialist(String specialist) { this.specialist = specialist; }
    public void setPrice(double price) { this.price = price; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return serviceName + " - " + appointmentDate + " - " + specialist;
    }
}