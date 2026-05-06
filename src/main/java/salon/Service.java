package salon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Service {
    private static int nextId = 1;
    private int id;
    private String name;
    private String category;
    private String duration;
    private double price;

    // Конструктор для создания нового сотрудника
    public Service(String name, String category, String duration, double price) {
        this.id = nextId++;
        this.name = name;
        this.category = category;
        this.duration = duration;
        this.price = price;
    }
    // Конструктор для редактирования сотрудника (сохраняется старый id)
    public Service(int id, String name, String category, String duration, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.duration = duration;
        this.price = price;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDuration() {
        return duration;
    }

    public double getPrice() {
        return price;
    }

    // Переопределяем equals и hashCode для корректного поиска в списке
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Service service = (Service) obj;
        return id == service.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
