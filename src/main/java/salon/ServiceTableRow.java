package salon;

import javafx.beans.property.SimpleStringProperty;

public class ServiceTableRow {
    public final int serviceId;
    public final SimpleStringProperty name;
    public final SimpleStringProperty description;
    public final SimpleStringProperty price;
    public final SimpleStringProperty duration;

    public ServiceTableRow(int serviceId, String name, String description, double price, String duration) {
        this.serviceId = serviceId;
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(
                description == null || description.isBlank() ? "Описание не добавлено" : description
        );
        this.price = new SimpleStringProperty(String.format("%.2f руб.", price));
        this.duration = new SimpleStringProperty(duration);
    }

    public int getServiceId() {
        return serviceId;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public SimpleStringProperty priceProperty() {
        return price;
    }

    public SimpleStringProperty durationProperty() {
        return duration;
    }
}