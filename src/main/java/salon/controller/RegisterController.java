package salon.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;

public class RegisterController extends BaseController {
    @FXML
    private TextField firstName, lastName, middleName, phone, email, login;
    @FXML private PasswordField password;
    @FXML private ToggleGroup genderGroup;

    @FXML
    private void registerAction(ActionEvent event) throws IOException {
        String gender = (genderGroup.getSelectedToggle() != null)
                ? genderGroup.getSelectedToggle().getUserData().toString()
                : "Не выбран";

        if (isBlank(lastName.getText()) || isBlank(firstName.getText()) || isBlank(phone.getText())
                || isBlank(email.getText()) || isBlank(login.getText()) || isBlank(password.getText())) {
            showError("Заполните фамилию, имя, телефон, почту, логин и пароль.");
            return;
        }

        System.out.println("Регистрация: " + lastName.getText() + " " + firstName.getText()
                + ", логин: " + login.getText() + ", пол: " + gender);
        showInfo("Регистрация выполнена.");
        changeWindow(event, "/fxml/role_selector.fxml", "Салон красоты");
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/role_selector.fxml", "Салон красоты");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
