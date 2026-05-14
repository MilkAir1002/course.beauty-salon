package salon;

public class Service {
    private int id;
    private String name;
    private String category;
    private String duration;
    private double price;

    // Конструктор для создания новой услуги
    public Service(String name, String category, String duration, double price) {
        this.id = -1;
        this.name = name;
        this.category = category;
        this.duration = duration;
        this.price = price;
    }

    // Конструктор для редактирования услуги (сохраняется старый id)
    public Service(int id, String name, String category, String duration, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.duration = duration;
        this.price = price;
    }

    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDuration() { return duration; }
    public double getPrice() { return price; }

    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setPrice(double price) { this.price = price; }

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