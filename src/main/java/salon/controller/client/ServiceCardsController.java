package salon.controller.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import salon.controller.BaseController;
import salon.db.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/services_category_window.fxml"));
            Parent root = loader.load();

            ServicesCategoryController controller = loader.getController();
            controller.initData(tile.serviceId(), tile.name(), mainStage);

            Stage stage = new Stage();
            stage.setTitle("Услуги: " + tile.name());
            stage.setScene(new Scene(root, 920, 560));
            stage.show();

        } catch (IOException e) {
            showError("Не удалось открыть окно категории.");
            e.printStackTrace();
        }
    }

    private Stage getStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
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
}