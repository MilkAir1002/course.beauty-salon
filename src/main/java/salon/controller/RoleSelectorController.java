package salon.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RoleSelectorController {
    @FXML
    private void client(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/login_client.fxml", "Вход для клиента");
    }

    @FXML
    private void admin(ActionEvent event) {
    }

    private void changeWindow(ActionEvent event, String fxmlPath, String title) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}