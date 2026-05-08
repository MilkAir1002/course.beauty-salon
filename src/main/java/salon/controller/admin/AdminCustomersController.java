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
import salon.Customer;
import salon.controller.BaseController;

import java.io.IOException;

public class AdminCustomersController extends BaseController {
    // Элементы таблицы
    @FXML
    private TableView<Customer> tableView;
    @FXML
    private TableColumn<Customer, Integer> idColumn;
    @FXML
    private TableColumn<Customer, String> fullNameColumn;
    @FXML
    private TableColumn<Customer, String> birthDateColumn;
    @FXML
    private TableColumn<Customer, String> phoneColumn;
    @FXML
    private TableColumn<Customer, String> additionalInfoColumn;

    // Список клиентов. Таблица обновляется сама
    private ObservableList<Customer> customersList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedBirthDate"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        additionalInfoColumn.setCellValueFactory(new PropertyValueFactory<>("additionalInfo"));
        // Связываем таблицу со списком
        tableView.setItems(customersList);
    }

    // Добавление клиента
    @FXML
    private void addCustomer() throws IOException { // Нажатие на кнопку "Добавить"
        // Открываем окно с формой
        Customer result = showCustomerDialog(null); // Открытие пустой формы
        if (result != null) { // Если пользователь нажал "Сохранить"
            customersList.add(result); // Добавление в список
        }
    }

    // Редактирование клиента
    @FXML
    private void editCustomer() throws IOException {
        Customer selected = tableView.getSelectionModel().getSelectedItem(); // Получение выбранной строки из таблицы
        if (selected == null) { // Если запись в таблице не выбрана
            showAlert("Выберите клиента для редактирования");
            return;
        }

        Customer result = showCustomerDialog(selected); // Открытие заполненной формы
        if (result != null) { // Если пользователь нажал "Сохранить"
            int index = customersList.indexOf(selected); // Находим индекс старого
            customersList.set(index, result); // Заменяем
            tableView.refresh(); // Обновляем таблицу
        }
    }

    // Удаление сотрудника
    @FXML
    private void deleteCustomer() {
        Customer selected = tableView.getSelectionModel().getSelectedItem(); // Получение выбранной строки из таблицы
        if (selected == null) { // Если запись в таблице не выбрана
            showAlert("Выберите сотрудника для удаления");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION); // Создаем всплывающее окно подтверждения (OK/CANCEL)
        confirmation.setTitle("Подтверждение");
        confirmation.setContentText("Удалить " + selected.getFullName() + "?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) { // Если нажата "OK"
            customersList.remove(selected); // Удаляем из списка
        }
    }

    // Метод для показа диалога с формой
    private Customer showCustomerDialog(Customer existingCustomer) throws IOException {
        // Загружаем FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/customer_form.fxml"));
        Parent root = loader.load();

        // Получаем элементы управления из формы
        TextField fullNameField = (TextField) root.lookup("#fullName");
        DatePicker birthDatePicker = (DatePicker) root.lookup("#birthDate");
        TextField phoneField = (TextField) root.lookup("#phone");
        TextField additionalInfoField = (TextField) root.lookup("#additionalInfo");
        Button saveButton = (Button) root.lookup("#saveAppointmentButton");

        boolean isEdit = existingCustomer != null; // Определяем режим (Добавление или редактирование)
        // Если редактируем - заполняем поля
        if (isEdit) { // Вставляем данные в поля формы
            fullNameField.setText(existingCustomer.getFullName());
            birthDatePicker.setValue(existingCustomer.getBirthDate());
            phoneField.setText(existingCustomer.getPhone());
            additionalInfoField.setText(existingCustomer.getAdditionalInfo());
            saveButton.setText("Сохранить");
        }

        // Создаем окно
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL); // Определяем тип модальности. Блокирует все окна приложения
        dialog.setTitle(isEdit ? "Редактирование сотрудника" : "Добавление сотрудника");
        dialog.setScene(new Scene(root)); // Создаем и помещаем в сцену VBOX с формой (root)

        // Массив для хранения результата
        final Customer[] result = {null};

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

            // Создаем клиента
            if (isEdit) { // Если редактируем
                result[0] = new Customer(
                        existingCustomer.getId(), // берем id существующего сотрудника
                        fullNameField.getText(),
                        birthDatePicker.getValue(),
                        phoneField.getText(),
                        additionalInfoField.getText()
                );
            } else {
                result[0] = new Customer(
                        fullNameField.getText(),
                        birthDatePicker.getValue(),
                        phoneField.getText(),
                        additionalInfoField.getText()
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
    private void employers(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin/admin_employers.fxml", "Панель администратора: сотрудники");
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
