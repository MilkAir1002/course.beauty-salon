package salon.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import salon.Employer;

public class NewEmployerController {
    @FXML
    private TextField fullName;

    @FXML
    private DatePicker birthDate;

    @FXML
    private TextField phone;

    @FXML
    private ComboBox<String> post;

    @FXML
    private Button saveAppointmentButton;

    // ДОБАВЬТЕ ЭТОТ ИНТЕРФЕЙС
    public interface EmployerSaveListener {
        void onEmployerSaved(Employer employer);
    }

    private EmployerSaveListener listener;

    public void setEmployerSaveListener(EmployerSaveListener listener) {
        this.listener = listener;
    }

    @FXML
    private void initialize() {
        post.getItems().addAll(
                "Парикмахер",
                "Мастер маникюра",
                "Косметолог",
                "Визажист",
                "Массажист",
                "Администратор"
        );

        birthDate.setPromptText("ДД.ММ.ГГГГ");
    }

    @FXML
    private void saveAppointmentButton(ActionEvent event) {
        if (fullName.getText().isEmpty()) {
            showAlert("Ошибка", "Введите ФИО сотрудника");
            return;
        }

        if (birthDate.getValue() == null) {
            showAlert("Ошибка", "Выберите дату рождения");
            return;
        }

        if (phone.getText().isEmpty()) {
            showAlert("Ошибка", "Введите номер телефона");
            return;
        }

        if (post.getValue() == null) {
            showAlert("Ошибка", "Выберите должность");
            return;
        }

        Employer employer = new Employer(
                fullName.getText(),
                birthDate.getValue(),
                phone.getText(),
                post.getValue()
        );

        if (listener != null) {
            listener.onEmployerSaved(employer);
        }

        showAlert("Успех", "Сотрудник успешно добавлен!");
        closeWindow(event);
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}