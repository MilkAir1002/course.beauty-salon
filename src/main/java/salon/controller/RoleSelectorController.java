package salon.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import salon.db.database;

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
        String login = loginField.getText().trim();
        String password = passwordField.getText();
        if (login.isEmpty() || password.isEmpty()) {
            showError("Введите логин и пароль.");
            return;
        }

        if ("ADMIN".equals(role)) {
//            if(!database.loginAdminDB(login, password)){
//                showError("Что-то пошло не так, проверьте данные");
//                return;
//            }
            database.curLog = login;
            System.out.println(database.curLog);
            System.out.println("Вход: " + login + ", роль: " + role);
            changeWindow(event, "/fxml/admin/admin_employers.fxml", "Панель администратора: сотрудники");

        }
        else {
            if(!database.loginClientDB(login, password)){
                showError("Что-то пошло не так, проверьте данные");
                return;
            }
            database.curLog = login;
            System.out.println(database.curLog);
            System.out.println("Вход: " + login + ", роль: " + role);
            changeWindow(event, "/fxml/client/client_menu.fxml", "Личный кабинет");
        }
    }

    @FXML
    private void switchToRegister(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/client/register.fxml", "Регистрация");
    }
}
