package salon;

// Наследуемся от вашего базового класса
public class ServiceCard extends Service {
    private String description;
    private String recommendations;
    private String imagePath;

    // Конструктор
    public ServiceCard(int id, String name, String category, String duration, double price,
                       String description, String recommendations, String imagePath) {
        // Вызываем конструктор родителя (Service)
        super(id, name, category, duration, price);
        this.description = description;
        this.recommendations = recommendations;
        this.imagePath = imagePath;
    }

    // Дополнительные геттеры
    public String getDescription() { return description; }
    public String getRecommendations() { return recommendations; }
    public String getImagePath() { return imagePath; }
}