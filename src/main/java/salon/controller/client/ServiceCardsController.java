package salon.controller.client;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import salon.controller.BaseController;
import salon.db.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ServiceCardsController extends BaseController {

    @FXML
    private void initialize() {
        ensureServiceCardsTable();
    }

    @FXML
    private void openHairServices(ActionEvent event) {
        openCategoryWindow(new CategoryTile(1, "Парикмахерский зал"), getStage(event));
    }

    @FXML
    private void openNailsServices(ActionEvent event) {
        openCategoryWindow(new CategoryTile(2, "Ногтевой сервис"), getStage(event));
    }

    @FXML
    private void openCosmetologyServices(ActionEvent event) {
        openCategoryWindow(new CategoryTile(3, "Косметология"), getStage(event));
    }

    @FXML
    private void openMassageServices(ActionEvent event) {
        openCategoryWindow(new CategoryTile(4, "Массаж"), getStage(event));
    }

    @FXML
    private void openMakeupServices(ActionEvent event) {
        openCategoryWindow(new CategoryTile(5, "Услуги визажиста"), getStage(event));
    }

    private void openCategoryWindow(CategoryTile tile, Stage mainStage) {
        TableView<ServiceTableRow> tableView = new TableView<>();
        tableView.setItems(loadServicesByCategory(tile.serviceId()));
        tableView.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-font-size: 14;");

        TableColumn<ServiceTableRow, String> nameColumn = new TableColumn<>("Название услуги");
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        nameColumn.setPrefWidth(190);

        TableColumn<ServiceTableRow, String> descriptionColumn = new TableColumn<>("Описание");
        descriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());
        descriptionColumn.setPrefWidth(330);

        // --- ОБНОВЛЕННАЯ ФАБРИКА ДЛЯ АВТОПЕРЕНОСА ТЕКСТА И ВЫСОТЫ СТРОК ---
        descriptionColumn.setCellFactory(tc -> new TableCell<>() {
            private final Text textNode = new Text();

            {
                // Привязываем ширину переноса текста к ширине колонки с небольшим отступом
                textNode.wrappingWidthProperty().bind(widthProperty().subtract(15));
                // Наследуем стили шрифта, чтобы текст выглядел аккуратно
                textNode.styleProperty().bind(styleProperty());
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    textNode.setText(item);
                    setGraphic(textNode);
                    // Заставляем ячейку автоматически рассчитывать высоту под текст
                    setPrefHeight(javafx.scene.control.Control.USE_COMPUTED_SIZE);
                }
            }
        });
        // ------------------------------------------------------------------

        TableColumn<ServiceTableRow, String> priceColumn = new TableColumn<>("Цена");
        priceColumn.setCellValueFactory(data -> data.getValue().priceProperty());
        priceColumn.setPrefWidth(110);

        TableColumn<ServiceTableRow, String> durationColumn = new TableColumn<>("Длительность");
        durationColumn.setCellValueFactory(data -> data.getValue().durationProperty());
        durationColumn.setPrefWidth(130);

        TableColumn<ServiceTableRow, ServiceTableRow> bookColumn = new TableColumn<>("Записаться");
        bookColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()));
        bookColumn.setPrefWidth(130);
        bookColumn.setCellFactory(column -> new TableCell<>() {
            private final Button button = new Button("Записаться");

            {
                button.setStyle("-fx-background-color: #758952; -fx-text-fill: white; "
                        + "-fx-background-radius: 12; -fx-font-weight: bold;");
                button.setOnAction(event -> {
                    try {
                        ServiceTableRow row = getTableView().getItems().get(getIndex());
                        NewClientAppointmentController.setInitialServiceId(row.getServiceId());
                        closeCurrentStage(event);
                        changeWindow(mainStage, "/fxml/client/new_client_appointment.fxml", "Новая запись");
                    } catch (IOException e) {
                        showError("Не удалось открыть окно записи.");
                    }
                });
            }

            @Override
            protected void updateItem(ServiceTableRow item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : button);
                setAlignment(Pos.CENTER);
            }
        });

        tableView.getColumns().addAll(nameColumn, descriptionColumn, priceColumn, durationColumn, bookColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label title = new Label(tile.name());
        title.setTextFill(Color.web("#758952"));
        title.setFont(Font.font("Serif", FontWeight.BOLD, 34));

        VBox content = new VBox(20, title, tableView);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color:  rgba(255, 255, 255, 0.78);");
        VBox.setVgrow(tableView, Priority.ALWAYS);

        ImageView background = new ImageView(new Image(getClass().getResourceAsStream("/fxml/FON1.png")));
        background.setFitWidth(920);
        background.setFitHeight(560);
        background.setPreserveRatio(false);

        StackPane root = new StackPane(background, content);

        Stage stage = new Stage();
        stage.setTitle("Услуги: " + tile.name());
        stage.setScene(new Scene(root, 920, 560));
        stage.show();
    }

    private Stage getStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    private void closeCurrentStage(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    private ObservableList<ServiceTableRow> loadServicesByCategory(int serviceId) {
        ObservableList<ServiceTableRow> services = FXCollections.observableArrayList();
        String query = """
                SELECT s.id, s.name, s.duration, s.price, sc.description
                FROM services s
                LEFT JOIN service_cards sc ON sc.service_id = s.id
                WHERE s.service_id = ?
                ORDER BY s.name
                """;

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, serviceId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                services.add(new ServiceTableRow(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("duration")
                ));
            }
        } catch (SQLException e) {
            showError("Не удалось загрузить услуги категории.");
            e.printStackTrace();
        }

        return services;
    }

    private void ensureServiceCardsTable() {
        String query = """
                CREATE TABLE IF NOT EXISTS service_cards (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    description TEXT,
                    service_id INTEGER NOT NULL,
                    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE,
                    UNIQUE(service_id)
                )
                """;

        try (Connection conn = DriverManager.getConnection(database.url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            System.err.println("Не удалось проверить таблицу service_cards: " + e.getMessage());
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/client/client_menu.fxml", "Личный кабинет");
    }

    private record CategoryTile(int serviceId, String name) {
    }

    private static class ServiceTableRow {
        private final int serviceId;
        private final SimpleStringProperty name;
        private final SimpleStringProperty description;
        private final SimpleStringProperty price;
        private final SimpleStringProperty duration;

        private ServiceTableRow(int serviceId, String name, String description, double price, String duration) {
            this.serviceId = serviceId;
            this.name = new SimpleStringProperty(name);
            this.description = new SimpleStringProperty(
                    description == null || description.isBlank() ? "Описание не добавлено" : description
            );
            this.price = new SimpleStringProperty(String.format("%.2f руб.", price));
            this.duration = new SimpleStringProperty(duration);
        }

        private int getServiceId() {
            return serviceId;
        }

        private SimpleStringProperty nameProperty() {
            return name;
        }

        private SimpleStringProperty descriptionProperty() {
            return description;
        }

        private SimpleStringProperty priceProperty() {
            return price;
        }

        private SimpleStringProperty durationProperty() {
            return duration;
        }
    }
}