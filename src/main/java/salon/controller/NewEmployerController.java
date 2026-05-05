package salon.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import salon.Employer;

import java.time.LocalDate;

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

    public interface EmployerSaveListener {
        void onEmployerSaved(Employer employer);
    }

    // НОВЫЙ БЛОК: Интерфейс для обновления сотрудника
    public interface EmployerUpdateListener {
        void onEmployerUpdated(Employer oldEmployer, Employer updatedEmployer);
    }

    private EmployerSaveListener saveListener;

    // НОВЫЙ БЛОК: Поля для режима редактирования
    private EmployerUpdateListener updateListener;
    private Employer editingEmployer; // Сотрудник, которого редактируем
    private boolean isEditMode = false;

    public void setEmployerSaveListener(EmployerSaveListener listener) {
        this.saveListener = listener;
    }

    // НОВЫЙ БЛОК: Сеттер для слушателя обновления
    public void setEmployerUpdateListener(EmployerUpdateListener listener) {
        this.updateListener = listener;
    }

    // НОВЫЙ БЛОК: Метод для загрузки данных сотрудника в форму (режим редактирования)
    public void setEmployerForEdit(Employer employer) {
        this.isEditMode = true;
        this.editingEmployer = employer;

        // Заполняем поля существующими данными
        fullName.setText(employer.getFullName());
        birthDate.setValue(employer.getBirthDate());
        phone.setText(employer.getPhone());
        post.setValue(employer.getPosition());

        // Меняем текст кнопки
        saveAppointmentButton.setText("Сохранить");
    }

    // НОВЫЙ БЛОК: Метод для обновления заголовка окна (вызывать после показа окна)
    public void updateWindowTitle() {
        if (isEditMode) {
            Stage stage = (Stage) saveAppointmentButton.getScene().getWindow();
            if (stage != null) {
                stage.setTitle("Редактирование сотрудника");
            }
        }
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

        // НОВЫЙ БЛОК: Создаем сотрудника (нового или обновленного)
        Employer employer;

        if (isEditMode && editingEmployer != null) {
            // Режим редактирования - создаем сотрудника с тем же ID
            employer = new Employer(
                    editingEmployer.getId(),
                    fullName.getText(),
                    birthDate.getValue(),
                    phone.getText(),
                    post.getValue()
            );

            if (updateListener != null) {
                updateListener.onEmployerUpdated(editingEmployer, employer);
            }
            showAlert("Успех", "Сотрудник успешно обновлен!");
        } else {
            // Режим добавления - создаем нового сотрудника
            employer = new Employer(
                    fullName.getText(),
                    birthDate.getValue(),
                    phone.getText(),
                    post.getValue()
            );

            if (saveListener != null) {
                saveListener.onEmployerSaved(employer);
            }
            showAlert("Успех", "Сотрудник успешно добавлен!");
        }

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