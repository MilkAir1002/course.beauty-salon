package salon.controller.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import salon.controller.BaseController;
import salon.db.database;

import java.io.IOException;

public class RegisterController extends BaseController {
    @FXML
    private TextField firstName, lastName, patronymic, phone, email, login;
    @FXML private PasswordField password;
    @FXML private ToggleGroup genderGroup;

    @FXML
    private void registerAction(ActionEvent event) throws IOException {
        String gender = "M";
        if (genderGroup.getSelectedToggle() != null) {
            RadioButton selectedBtn = (RadioButton) genderGroup.getSelectedToggle();
            gender = selectedBtn.getText().startsWith("М") ? "M" : "F";
        }

        if (isBlank(lastName.getText()) || isBlank(firstName.getText()) || isBlank(phone.getText())
                || isBlank(email.getText()) || isBlank(login.getText()) || isBlank(password.getText())) {
            showError("Заполните все обязательные поля!");
            return;
        }

        database db = new database();
        boolean isSuccess = db.registerUser(
                firstName.getText(),
                lastName.getText(),
                patronymic.getText(),
                phone.getText(),
                email.getText(),
                login.getText(),
                gender,
                password.getText()
        );

        if (isSuccess) {
            showInfo("Регистрация успешно выполнена!");
            changeWindow(event, "/fxml/role_selector.fxml", "Салон красоты");
        } else {
            showError("Ошибка регистрации. Возможно, такой логин уже существует.");
        }
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/role_selector.fxml", "Салон красоты");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}