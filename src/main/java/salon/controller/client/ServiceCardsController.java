package salon.controller.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import salon.controller.BaseController;
import salon.ServiceCard; // Импортируем наш новый расширенный класс
import salon.db.database; // Предполагаем, что здесь лежит логика подключения к БД

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCardsController extends BaseController {

    @FXML
    private VBox servicesContainer;

    @FXML
    private void initialize() {
        loadServiceCards();
    }

    private void loadServiceCards() {
        List<ServiceCard> cards = new ArrayList<>();

        // Используем URL вашей базы данных SQLite
        String url = "jdbc:sqlite:salon.db";
        String query = "SELECT id, name, category, duration, price, description, recommendations, image_path FROM services";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Создаем объект ServiceCard, который наследует поля от Service
                cards.add(new ServiceCard(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("duration"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getString("recommendations"),
                        rs.getString("image_path")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке карточек услуг: " + e.getMessage());
            e.printStackTrace();
        }

        // Очищаем контейнер перед добавлением (на всякий случай)
        servicesContainer.getChildren().clear();

        for (ServiceCard card : cards) {
            servicesContainer.getChildren().add(createUIFullCard(card));
        }
    }

    private VBox createUIFullCard(ServiceCard service) {
        // Главный контейнер карточки (Горизонтальный)
        HBox cardRow = new HBox(25);
        cardRow.setPadding(new Insets(20));
        cardRow.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; " +
                "-fx-border-color: #E8C0C5; -fx-border-radius: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        cardRow.setAlignment(Pos.CENTER_LEFT);

        // --- ЛЕВАЯ КОЛОНКА (Инфо) ---
        VBox infoCol = new VBox(12);
        HBox.setHgrow(infoCol, Priority.ALWAYS);
        infoCol.setMinWidth(400);

        Label descHeader = new Label("ОПИСАНИЕ УСЛУГИ");
        descHeader.setFont(Font.font("System", FontWeight.BOLD, 12));
        descHeader.setTextFill(javafx.scene.paint.Color.web("#BDBDBD"));

        Label description = new Label(service.getDescription());
        description.setWrapText(true);
        description.setFont(Font.font("System", 14));

        Label details = new Label("Длительность: " + service.getDuration() + " | Цена: " + service.getPrice() + " ₽");
        details.setFont(Font.font("System", FontWeight.BOLD, 14));
        details.setTextFill(javafx.scene.paint.Color.web("#758952"));

        VBox recBox = new VBox(5);
        Label recHeader = new Label("Рекомендации:");
        recHeader.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label recText = new Label(service.getRecommendations());
        recText.setWrapText(true);
        recText.setStyle("-fx-text-fill: #828282; -fx-font-style: italic;");
        recBox.getChildren().addAll(recHeader, recText);

        Button btnBook = new Button("ЗАПИСАТЬСЯ");
        btnBook.setPrefWidth(200);
        btnBook.setStyle("-fx-background-color: #758952; -fx-text-fill: white; -fx-background-radius: 25; -fx-font-weight: bold;");
        btnBook.setPadding(new Insets(10));
        btnBook.setOnAction(event -> {
            try {
                changeWindow(event, "/fxml/client/new_client_appointment.fxml", "Новая запись");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        infoCol.getChildren().addAll(descHeader, description, details, recBox, btnBook);

        // --- ПРАВАЯ КОЛОНКА (Название + Картинка) ---
        VBox visualCol = new VBox(10);
        visualCol.setAlignment(Pos.TOP_CENTER);
        visualCol.setMinWidth(220);

        Label nameLabel = new Label(service.getName().toUpperCase());
        nameLabel.setFont(Font.font("Serif", FontWeight.BOLD, 18));
        nameLabel.setTextFill(javafx.scene.paint.Color.web("#758952"));
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setWrapText(true);

        ImageView imgView = new ImageView();
        try {
            // Загрузка картинки. Путь в БД должен быть вида: /images/service1.jpg
            Image image = new Image(getClass().getResourceAsStream(service.getImagePath()));
            imgView.setImage(image);
        } catch (Exception e) {
            System.err.println("Не удалось загрузить изображение для: " + service.getName());
        }
        imgView.setFitWidth(200);
        imgView.setPreserveRatio(true);

        visualCol.getChildren().addAll(nameLabel, imgView);

        cardRow.getChildren().addAll(infoCol, visualCol);

        // Обертка для отступа между карточками
        VBox wrapper = new VBox(cardRow);
        wrapper.setPadding(new Insets(0, 0, 20, 0));
        return wrapper;
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/client/client_menu.fxml", "Личный кабинет");
    }
}