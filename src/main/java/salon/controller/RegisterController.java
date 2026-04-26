package salon.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {
    @FXML
    private TextField firstName, lastName, middleName, phone, email, login;
    @FXML private PasswordField password;
    @FXML private ToggleGroup genderGroup;

    @FXML
    private void registerAction(ActionEvent event) {
        String gender = (genderGroup.getSelectedToggle() != null)
                ? genderGroup.getSelectedToggle().getUserData().toString()
                : "Не выбран";

        System.out.println("Регистрация: " + firstName.getText() + " " + login.getText() + ", Пол: " + gender);
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/login_client.fxml", "Вход для клиента");
    }

    private void changeWindow(ActionEvent event, String fxmlPath, String title) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 830, 631));
        stage.show();
    }
}
