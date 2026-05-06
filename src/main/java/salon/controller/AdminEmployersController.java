package salon.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import salon.Employer;

import java.io.IOException;

public class AdminEmployersController extends BaseController {

    // Элементы таблицы
    @FXML
    private TableView<Employer> tableView;
    @FXML
    private TableColumn<Employer, Integer> idColumn;
    @FXML
    private TableColumn<Employer, String> fullNameColumn;
    @FXML
    private TableColumn<Employer, String> birthDateColumn;
    @FXML
    private TableColumn<Employer, String> positionColumn;
    @FXML
    private TableColumn<Employer, String> phoneColumn;

    // Список сотрудников. Таблица обновляется сама
    private ObservableList<Employer> employersList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedBirthDate"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        // Связываем таблицу со списком
        tableView.setItems(employersList);
    }

    // Добавление сотрудника
    @FXML
    private void addEmployer() throws IOException { // Нажатие на кнопку "Добавить"
        // Открываем окно с формой
        Employer result = showEmployerDialog(null); // Открытие пустой формы
        if (result != null) { // Если пользователь нажал "Сохранить"
            employersList.add(result); // Добавление в список
        }
    }

    // Редактирование сотрудника
    @FXML
    private void editEmployer() throws IOException {
        Employer selected = tableView.getSelectionModel().getSelectedItem(); // Получение выбранной строки из таблицы
        if (selected == null) { // Если запись в таблице не выбрана
            showAlert("Выберите сотрудника для редактирования");
            return;
        }

        Employer result = showEmployerDialog(selected); // Открытие заполненной формы
        if (result != null) { // Если пользователь нажал "Сохранить"
            int index = employersList.indexOf(selected); // Находим индекс старого
            employersList.set(index, result); // Заменяем
            tableView.refresh(); // Обновляем таблицу
        }
    }

    // Удаление сотрудника
    @FXML
    private void deleteEmployer() {
        Employer selected = tableView.getSelectionModel().getSelectedItem(); // Получение выбранной строки из таблицы
        if (selected == null) { // Если запись в таблице не выбрана
            showAlert("Выберите сотрудника для удаления");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION); // Создаем всплывающее окно подтверждения (OK/CANCEL)
        confirmation.setTitle("Подтверждение");
        confirmation.setContentText("Удалить " + selected.getFullName() + "?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) { // Если нажата "OK"
            employersList.remove(selected); // Удаляем из списка
        }
    }

    // Метод для показа диалога с формой
    private Employer showEmployerDialog(Employer existingEmployer) throws IOException {
        // Загружаем FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/new_employer.fxml"));
        Parent root = loader.load();

        // Получаем элементы управления из формы
        TextField fullNameField = (TextField) root.lookup("#fullName");
        DatePicker birthDatePicker = (DatePicker) root.lookup("#birthDate");
        TextField phoneField = (TextField) root.lookup("#phone");
        ComboBox<String> postCombo = (ComboBox<String>) root.lookup("#post");
        Button saveButton = (Button) root.lookup("#saveAppointmentButton");

        // Настраиваем выпадающий список
        postCombo.getItems().addAll("Парикмахер", "Мастер маникюра", "Косметолог", "Визажист", "Массажист", "Администратор");


        boolean isEdit = existingEmployer != null; // Определяем режим (Добавление или редактирование)
        // Если редактируем - заполняем поля
        if (isEdit) { // Вставляем данные в поля формы
            fullNameField.setText(existingEmployer.getFullName());
            birthDatePicker.setValue(existingEmployer.getBirthDate());
            phoneField.setText(existingEmployer.getPhone());
            postCombo.setValue(existingEmployer.getPosition());
            saveButton.setText("Сохранить");
        }

        // Создаем окно
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL); // Определяем тип модальности. Блокирует все окна приложения
        dialog.setTitle(isEdit ? "Редактирование сотрудника" : "Добавление сотрудника");
        dialog.setScene(new Scene(root)); // Создаем и помещаем в сцену VBOX с формой (root)

        // Массив для хранения результата
        final Employer[] result = {null};

        // Обработчик кнопки сохранить
        saveButton.setOnAction(e -> { // Действия при нажатии на кнопку
            // Валидация (проверка ввода)
            if (fullNameField.getText().isEmpty()) {
                showAlert("Введите ФИО");
                return;
            }
            if (birthDatePicker.getValue() == null) {
                showAlert("Выберите дату рождения");
                return;
            }
            if (phoneField.getText().isEmpty()) {
                showAlert("Введите телефон");
                return;
            }
            if (postCombo.getValue() == null) {
                showAlert("Выберите должность");
                return;
            }

            // Создаем сотрудника
            if (isEdit) { // Если редактируем
                result[0] = new Employer(
                        existingEmployer.getId(), // берем id существующего сотрудника
                        fullNameField.getText(),
                        birthDatePicker.getValue(),
                        phoneField.getText(),
                        postCombo.getValue()
                );
            } else {
                result[0] = new Employer(
                        fullNameField.getText(),
                        birthDatePicker.getValue(),
                        phoneField.getText(),
                        postCombo.getValue()
                );
            }

            dialog.close(); // Закрытие окна
        });

        dialog.showAndWait(); // Показать окно и ждать пока пользователь не закончит работу
        return result[0]; // возвращаем сотрудника
    }

    // Всплывающее окно с предупреждением
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING); // Создаем окно с предупреждением
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Навигация
    @FXML
    private void logout(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/role_selector.fxml", "Салон красоты");
    }

    @FXML
    private void clients(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin_customers.fxml", "Панель администратора: клиенты");
    }

    @FXML
    private void appointments(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin_appointments.fxml", "Панель администратора: записи");
    }

    @FXML
    private void services(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin_services.fxml", "Панель администратора: услуги");
    }
}