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
import salon.db.database;

import java.io.IOException;
import java.time.LocalDate;

public class AdminCustomersController extends BaseController {
    @FXML private TableView<Customer> tableView;
    @FXML private TableColumn<Customer, Integer> idColumn;
    @FXML private TableColumn<Customer, String> fullNameColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> passwordColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> loginColumn;
    @FXML private TableColumn<Customer, String> genderColumn;

    private ObservableList<Customer> customersList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        tableView.setItems(customersList);
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        loadCustomersFromDatabase();
    }

    private void loadCustomersFromDatabase() {
        customersList.clear();
        customersList.addAll(database.getAllCustomers());
    }

    @FXML
    private void addCustomer() {
        try {
            Customer result = showCustomerDialog(null);
            if (result != null) {
                if (database.addCustomer(result)) {
                    customersList.add(result);
                    showInfoAlert("Клиент успешно добавлен");
                } else {
                    showErrorAlert("Ошибка при добавлении клиента");
                }
            }
        } catch (IOException e) {
            showErrorAlert("Ошибка загрузки формы: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void editCustomer() {
        Customer selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Выберите клиента для редактирования");
            return;
        }

        try {
            Customer result = showCustomerDialog(selected);
            if (result != null) {
                if (database.updateCustomer(result)) {
                    int index = customersList.indexOf(selected);
                    customersList.set(index, result);
                    tableView.refresh();
                    showInfoAlert("Клиент успешно обновлен");
                } else {
                    showErrorAlert("Ошибка при обновлении клиента");
                }
            }
        } catch (IOException e) {
            showErrorAlert("Ошибка загрузки формы: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteCustomer() {
        Customer selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Выберите клиента для удаления");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Удалить клиента " + selected.getFullName() + "?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (database.deleteCustomer(selected.getId())) {
                customersList.remove(selected);
                showInfoAlert("Клиент успешно удален");
            } else {
                showErrorAlert("Ошибка при удалении клиента");
            }
        }
    }

    private Customer showCustomerDialog(Customer existingCustomer) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/customer_form.fxml"));
        Parent root = loader.load();

        // Получаем элементы формы
        TextField lastNameField = (TextField) root.lookup("#lastName");
        TextField firstNameField = (TextField) root.lookup("#firstName");
        TextField patronymicField = (TextField) root.lookup("#patronymic");
        TextField phoneField = (TextField) root.lookup("#phone");
        TextField emailField = (TextField) root.lookup("#email");
        TextField loginField = (TextField) root.lookup("#login");
        ComboBox<String> genderComboBox = (ComboBox<String>) root.lookup("#gender");
        TextField passwordField = (TextField) root.lookup("#password");
        Button saveButton = (Button) root.lookup("#saveAppointmentButton");

        // Добавляем значения в ComboBox программно (для отображения пользователю)
        genderComboBox.getItems().addAll("Мужской", "Женский");

        // Проверка, что все элементы найдены
        if (lastNameField == null || firstNameField == null || patronymicField == null ||
                phoneField == null || emailField == null || loginField == null ||
                genderComboBox == null || passwordField == null || saveButton == null) {
            throw new IOException("Не удалось найти элементы формы. Проверьте id в FXML файле.");
        }

        boolean isEdit = existingCustomer != null;

        if (isEdit) {
            lastNameField.setText(existingCustomer.getLastName());
            firstNameField.setText(existingCustomer.getFirstName());
            patronymicField.setText(existingCustomer.getPatronymic());
            phoneField.setText(existingCustomer.getPhone());
            emailField.setText(existingCustomer.getEmail());
            loginField.setText(existingCustomer.getLogin());
            // Преобразуем значение из базы (M/F) в отображаемое (Мужской/Женский)
            if ("M".equals(existingCustomer.getGender())) {
                genderComboBox.setValue("Мужской");
            } else if ("F".equals(existingCustomer.getGender())) {
                genderComboBox.setValue("Женский");
            }
            passwordField.setText(existingCustomer.getPassword());
            saveButton.setText("Сохранить");
        }

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(isEdit ? "Редактирование клиента" : "Добавление клиента");
        dialog.setScene(new Scene(root));
        dialog.setResizable(false);

        final Customer[] result = {null};

        saveButton.setOnAction(e -> {
            // Валидация
            if (lastNameField.getText().trim().isEmpty()) {
                showAlert("Введите фамилию");
                return;
            }
            if (firstNameField.getText().trim().isEmpty()) {
                showAlert("Введите имя");
                return;
            }
            if (phoneField.getText().trim().isEmpty()) {
                showAlert("Введите телефон");
                return;
            }
            if (emailField.getText().trim().isEmpty()) {
                showAlert("Введите email");
                return;
            }
            if (loginField.getText().trim().isEmpty()) {
                showAlert("Введите логин");
                return;
            }
            if (genderComboBox.getValue() == null) {
                showAlert("Выберите пол");
                return;
            }
            if (passwordField.getText().trim().isEmpty()) {
                showAlert("Введите пароль");
                return;
            }

            // Преобразуем пол из текста в значение для базы данных
            String genderForDb;
            if ("Мужской".equals(genderComboBox.getValue())) {
                genderForDb = "M";
            } else {
                genderForDb = "F";
            }

            // Создаем клиента
            if (isEdit) {
                result[0] = new Customer(
                        existingCustomer.getId(),
                        lastNameField.getText().trim(),
                        firstNameField.getText().trim(),
                        patronymicField.getText().trim(),
                        phoneField.getText().trim(),
                        emailField.getText().trim(),
                        loginField.getText().trim(),
                        genderForDb,  // Отправляем "M" или "F" в базу данных
                        passwordField.getText().trim()
                );
            } else {
                result[0] = new Customer(
                        lastNameField.getText().trim(),
                        firstNameField.getText().trim(),
                        patronymicField.getText().trim(),
                        phoneField.getText().trim(),
                        emailField.getText().trim(),
                        loginField.getText().trim(),
                        genderForDb,  // Отправляем "M" или "F" в базу данных
                        passwordField.getText().trim()
                );
            }
            dialog.close();
        });

        dialog.showAndWait();
        return result[0];
    }

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