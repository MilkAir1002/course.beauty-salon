package salon.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;

public class RoleSelectorController extends BaseController {
    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ToggleGroup roleGroup;

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        Toggle selectedRole = roleGroup.getSelectedToggle();
        String role = selectedRole != null ? selectedRole.getUserData().toString() : "CLIENT";

        if (loginField.getText().trim().isEmpty() || passwordField.getText().isEmpty()) {
            showError("Введите логин и пароль.");
            return;
        }

        System.out.println("Вход: " + loginField.getText() + ", роль: " + role);
        passwordField.clear();
        if ("ADMIN".equals(role)) {
            changeWindow(event, "/fxml/admin_menu.fxml", "Панель администратора");
        } else {
            changeWindow(event, "/fxml/client_menu.fxml", "Личный кабинет");
        }
    }

    @FXML
    private void switchToRegister(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/register.fxml", "Регистрация");
    }
}
