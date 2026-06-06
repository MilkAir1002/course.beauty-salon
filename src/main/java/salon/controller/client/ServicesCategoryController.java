package salon.controller.client;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import salon.controller.BaseController;
import salon.db.database;
import salon.ServiceTableRow;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServicesCategoryController extends BaseController {

    @FXML private Label titleLabel;
    @FXML private TableView<ServiceTableRow> tableView;
    @FXML private TableColumn<ServiceTableRow, String> nameColumn;
    @FXML private TableColumn<ServiceTableRow, String> descriptionColumn;
    @FXML private TableColumn<ServiceTableRow, String> priceColumn;
    @FXML private TableColumn<ServiceTableRow, String> durationColumn;
    @FXML private TableColumn<ServiceTableRow, ServiceTableRow> bookColumn;

    private Stage mainStage;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        descriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());
        priceColumn.setCellValueFactory(data -> data.getValue().priceProperty());
        durationColumn.setCellValueFactory(data -> data.getValue().durationProperty());
        bookColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()));

        descriptionColumn.setCellFactory(tc -> new TableCell<>() {
            private final Text textNode = new Text();
            {
                textNode.wrappingWidthProperty().bind(widthProperty().subtract(15));
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
                    setPrefHeight(javafx.scene.control.Control.USE_COMPUTED_SIZE);
                }
            }
        });

        bookColumn.setCellFactory(column -> new TableCell<>() {
            private final Button button = new Button("Записаться");
            {
                button.setStyle("-fx-background-color: #758952; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-weight: bold;");
                button.setOnAction(event -> {
                    try {
                        ServiceTableRow row = getTableView().getItems().get(getIndex());
                        NewClientAppointmentController.setInitialServiceId(row.getServiceId());

                        // Закрываем текущее модальное окно
                        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

                        // Меняем окно на главной сцене
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
    }

    public void initData(int serviceId, String categoryName, Stage mainStage) {
        this.mainStage = mainStage;
        this.titleLabel.setText(categoryName);
        this.tableView.setItems(loadServicesByCategory(serviceId));
    }

    private ObservableList<ServiceTableRow> loadServicesByCategory(int serviceId) {
        ObservableList<ServiceTableRow> services = FXCollections.observableArrayList();
        String query = "SELECT id, name, duration, price, description FROM services WHERE service_id = ? ORDER BY name";

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
}