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
import salon.Service;
import salon.controller.BaseController;

import java.io.IOException;

public class AdminServicesController extends BaseController {

    // Элементы таблицы
    @FXML
    private TableView<Service> tableView;
    @FXML
    private TableColumn<Service, Integer> idColumn;
    @FXML
    private TableColumn<Service, String> nameColumn;
    @FXML
    private TableColumn<Service, String> categoryColumn;
    @FXML
    private TableColumn<Service, String> durationColumn;
    @FXML
    private TableColumn<Service, Double> priceColumn;

    // Список услуг. Таблица обновляется сама
    private ObservableList<Service> servicesList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Форматируем колонку цены
        priceColumn.setCellFactory(column -> new TableCell<Service, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f ₽", item));
                }
            }
        });

        // Связываем таблицу со списком
        tableView.setItems(servicesList);
    }

    // Добавление услуги
    @FXML
    private void addService() throws IOException {
        // Открываем окно с формой
        Service result = showServiceDialog(null);
        if (result != null) {
            servicesList.add(result);
        }
    }

    // Редактирование услуги
    @FXML
    private void editService() throws IOException {
        Service selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Выберите услугу для редактирования");
            return;
        }

        Service result = showServiceDialog(selected);
        if (result != null) {
            int index = servicesList.indexOf(selected);
            servicesList.set(index, result);
            tableView.refresh();
        }
    }

    // Удаление услуги
    @FXML
    private void deleteService() {
        Service selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Выберите услугу для удаления");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Удалить услугу \"" + selected.getName() + "\"?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            servicesList.remove(selected);
            showInfo("Услуга успешно удалена");
        }
    }

    // Метод для показа диалога с формой
    private Service showServiceDialog(Service existingService) throws IOException {
        // Загружаем FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/service_form.fxml"));
        Parent root = loader.load();

        // Получаем элементы управления из формы
        TextField nameField = (TextField) root.lookup("#nameField");
        ComboBox<String> categoryCombo = (ComboBox<String>) root.lookup("#categoryCombo");
        TextField durationField = (TextField) root.lookup("#durationField");
        TextField priceField = (TextField) root.lookup("#priceField");
        Button saveButton = (Button) root.lookup("#saveButton"); // Теперь ищем saveButton, а не saveAppointmentButton

        // Настраиваем выпадающий список категорий
        categoryCombo.getItems().addAll(
                "Парикмахерские услуги",
                "Ногтевой сервис",
                "Косметология",
                "Массаж",
                "Визаж",
                "Другое"
        );

        boolean isEdit = existingService != null;

        // Если редактируем - заполняем поля
        if (isEdit) {
            nameField.setText(existingService.getName());
            categoryCombo.setValue(existingService.getCategory());
            durationField.setText(existingService.getDuration());
            priceField.setText(String.valueOf(existingService.getPrice()));
            saveButton.setText("Сохранить");
        }

        // Создаем окно
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(isEdit ? "Редактирование услуги" : "Добавление услуги");
        dialog.setScene(new Scene(root));
        dialog.setResizable(false);

        // Массив для хранения результата
        final Service[] result = {null};

        // Обработчик кнопки сохранить
        saveButton.setOnAction(e -> {
            // Валидация
            if (nameField.getText().isEmpty()) {
                showAlert("Введите название услуги");
                return;
            }
            if (categoryCombo.getValue() == null) {
                showAlert("Выберите категорию");
                return;
            }
            if (durationField.getText().isEmpty()) {
                showAlert("Введите длительность");
                return;
            }
            if (priceField.getText().isEmpty()) {
                showAlert("Введите стоимость");
                return;
            }

            // Проверка корректности цены
            double price;
            try {
                price = Double.parseDouble(priceField.getText());
                if (price <= 0) {
                    showAlert("Стоимость должна быть положительным числом");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Введите корректное число для стоимости");
                return;
            }

            // Создаем услугу
            if (isEdit) {
                result[0] = new Service(
                        existingService.getId(),
                        nameField.getText(),
                        categoryCombo.getValue(),
                        durationField.getText(),
                        price
                );
            } else {
                result[0] = new Service(
                        nameField.getText(),
                        categoryCombo.getValue(),
                        durationField.getText(),
                        price
                );
            }

            dialog.close();
        });

        dialog.showAndWait();
        return result[0];
    }

    // Всплывающее окно с предупреждением
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
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
    private void clients(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin/admin_customers.fxml", "Панель администратора: клиенты");
    }

    @FXML
    private void appointments(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin/admin_appointments.fxml", "Панель администратора: записи");
    }
}