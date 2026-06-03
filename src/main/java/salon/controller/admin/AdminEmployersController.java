package salon.controller.admin;

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
import salon.controller.BaseController;
import salon.db.database;

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
    @FXML
    private TableColumn<Employer, String> detailsColumn;

    // Список сотрудников. Таблица обновляется сама
    private ObservableList<Employer> employersList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Настройка колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedBirthDate"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        // Связываем таблицу со списком
        tableView.setItems(employersList);
        // Загружаем данные из базы данных
        loadEmployersFromDatabase();
    }

    // Загрузка сотрудников из базы данных
    private void loadEmployersFromDatabase() {
        employersList.clear();
        employersList.addAll(database.getAllEmployees());
        tableView.refresh();
    }

    // Добавление сотрудника
    @FXML
    private void addEmployer() throws IOException { // Нажатие на кнопку "Добавить"
        // Открываем окно с формой
        Employer result = showEmployerDialog(null); // Открытие пустой формы
        if (result != null) { // Если пользователь нажал "Сохранить"
            // Сохраняем в базу данных
            if (database.addEmployee(result)) {
                loadEmployersFromDatabase(); // Перезагружаем таблицу
                showInfoAlert("Сотрудник успешно добавлен");
            } else {
                showErrorAlert("Ошибка при добавлении сотрудника");
            }
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
            // Обновляем в базе данных
            if (database.updateEmployee(result)) {
                loadEmployersFromDatabase(); // Перезагружаем таблицу
                showInfoAlert("Сотрудник успешно обновлен");
            } else {
                showErrorAlert("Ошибка при обновлении сотрудника");
            }
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
        confirmation.setHeaderText(null);
        confirmation.setContentText("Удалить " + selected.getFullName() + "?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) { // Если нажата "OK"
            // Удаляем из базы данных
            if (database.deleteEmployee(selected.getId())) {
                loadEmployersFromDatabase(); // Перезагружаем таблицу
                showInfoAlert("Сотрудник успешно удален");
            } else {
                showErrorAlert("Ошибка при удалении сотрудника");
            }
        }
    }

    // Метод для показа диалога с формой
    private Employer showEmployerDialog(Employer existingEmployer) throws IOException {
        // Загружаем FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/employer_form.fxml"));
        Parent root = loader.load();

        // Получаем элементы управления из формы
        TextField fullNameField = (TextField) root.lookup("#fullName");
        DatePicker birthDatePicker = (DatePicker) root.lookup("#birthDate");
        TextField phoneField = (TextField) root.lookup("#phone");
        TextArea detailsField = (TextArea) root.lookup("#details");
        ComboBox<String> postCombo = (ComboBox<String>) root.lookup("#post");

        Button saveButton = (Button) root.lookup("#saveAppointmentButton");

        // Настраиваем выпадающий список
        postCombo.getItems().addAll("Парикмахер", "Мастер маникюра", "Косметолог", "Визажист", "Массажист");

        boolean isEdit = existingEmployer != null; // Определяем режим (Добавление или редактирование)
        // Если редактируем - заполняем поля
        if (isEdit) { // Вставляем данные в поля формы
            fullNameField.setText(existingEmployer.getFullName());
            birthDatePicker.setValue(existingEmployer.getBirthDate());
            phoneField.setText(existingEmployer.getPhone());
            detailsField.setText(existingEmployer.getDetails());
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

            String phone = phoneField.getText();
            if (!validatePhone(phone)) {
                return;
            }

            String formattedPhone = formatPhoneForSave(phone);

            // Создаем сотрудника
            if (isEdit) { // Если редактируем
                result[0] = new Employer(
                        existingEmployer.getId(), // берем id существующего сотрудника
                        fullNameField.getText(),
                        birthDatePicker.getValue(),
                        formattedPhone,
                        detailsField.getText(),
                        postCombo.getValue()
                );
            } else {
                result[0] = new Employer(
                        fullNameField.getText(),
                        birthDatePicker.getValue(),
                        formattedPhone,
                        detailsField.getText(),
                        postCombo.getValue()
                );
            }

            dialog.close(); // Закрытие окна
        });

        dialog.showAndWait(); // Показать окно и ждать пока пользователь не закончит работу
        return result[0]; // возвращаем сотрудника
    }

    private boolean validatePhone(String phone) {
        // Удаляем все нецифровые символы
        String digits = phone.replaceAll("\\D", "");

        // Проверяем, что 11 цифр и начинается с 7 или 8
        if (digits.length() != 11) {
            showAlert("Телефон должен содержать 11 цифр");
            return false;
        }

        if (!digits.startsWith("7") && !digits.startsWith("8")) {
            showAlert("Телефон должен начинаться с 7 или 8");
            return false;
        }

        return true;
    }

    // Метод для форматирования телефона в вид +7 (***) ***-**-**
    private String formatPhoneForSave(String phone) {
        // Удаляем все нецифровые символы
        String digits = phone.replaceAll("\\D", "");

        // Если первая цифра 8, заменяем на 7
        if (digits.startsWith("8")) {
            digits = "7" + digits.substring(1);
        }

        // Форматируем в +7 (***) ***-**-**
        return String.format("+%s (%s) %s-%s-%s",
                digits.charAt(0),
                digits.substring(1, 4),
                digits.substring(4, 7),
                digits.substring(7, 9),
                digits.substring(9, 11)
        );
    }

    // Навигация
    @FXML
    private void logout(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/role_selector.fxml", "Салон красоты");
    }

    @FXML
    private void clients(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin/admin_customers.fxml", "Панель администратора: клиенты");
    }

    @FXML
    private void appointments(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin/admin_appointments.fxml", "Панель администратора: записи");
    }

    @FXML
    private void services(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin/admin_services.fxml", "Панель администратора: услуги");
    }
}