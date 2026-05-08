package salon.controller.client;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import salon.controller.BaseController;
import salon.db.database;

public class EditClientController extends BaseController {
    @FXML
    private TextField lastName;
    @FXML
    private TextField firstName;
    @FXML
    private TextField patronymic;
    @FXML
    private TextField phone;
    @FXML
    private TextField email;
    @FXML
    private TextField login;
    @FXML
    private ComboBox<String> gender;
    @FXML
    private PasswordField password;

    @FXML
    private void initialize() {
        gender.getItems().setAll("M", "F");

        database.ClientProfile profile = database.getCurrentClientProfile();
        if (profile == null) {
            showError("Не удалось загрузить данные клиента.");
            return;
        }

        firstName.setText(profile.getFirstName());
        lastName.setText(profile.getLastName());
        patronymic.setText(profile.getPatronymic());
        phone.setText(profile.getPhone());
        email.setText(profile.getEmail());
        login.setText(profile.getLogin());
        gender.setValue(profile.getGender());
        password.setText(profile.getPassword());
    }

    @FXML
    private void save() {
        if (isBlank(firstName.getText()) || isBlank(lastName.getText())
                || isBlank(email.getText()) || isBlank(login.getText()) || isBlank(password.getText())) {
            showError("Заполните имя, фамилию, почту, логин и пароль.");
            return;
        }

        database.ClientProfile profile = new database.ClientProfile(
                firstName.getText(),
                lastName.getText(),
                patronymic.getText(),
                phone.getText(),
                email.getText(),
                login.getText(),
                gender.getValue(),
                password.getText()
        );

        if (database.updateCurrentClient(profile)) {
            showInfo("Данные клиента обновлены.");
            closeWindow();
        } else {
            showError("Не удалось сохранить данные. Проверьте, что логин и почта не заняты другим клиентом.");
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void closeWindow() {
        Stage stage = (Stage) firstName.getScene().getWindow();
        stage.close();
    }
}
