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
import salon.db.database;

import java.io.IOException;

public class AdminServicesController extends BaseController {

    @FXML private TableView<Service> tableView;
    @FXML private TableColumn<Service, Integer> idColumn;
    @FXML private TableColumn<Service, String> nameColumn;
    @FXML private TableColumn<Service, String> categoryColumn;
    @FXML private TableColumn<Service, String> durationColumn;
    @FXML private TableColumn<Service, Double> priceColumn;

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

        tableView.setItems(servicesList);
        loadServicesFromDatabase();
    }

    private void loadServicesFromDatabase() {
        servicesList.clear();
        servicesList.addAll(database.getAllServices());
    }

    @FXML
    private void addService() {
        try {
            Service result = showServiceDialog(null);
            if (result != null) {
                if (database.addService(result)) {
                    servicesList.add(result);
                    showInfoAlert("Услуга успешно добавлена");
                } else {
                    showErrorAlert("Ошибка при добавлении услуги");
                }
            }
        } catch (IOException e) {
            showErrorAlert("Ошибка загрузки формы: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void editService() {
        Service selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Выберите услугу для редактирования");
            return;
        }

        try {
            Service result = showServiceDialog(selected);
            if (result != null) {
                if (database.updateService(result)) {
                    int index = servicesList.indexOf(selected);
                    servicesList.set(index, result);
                    tableView.refresh();
                    showInfoAlert("Услуга успешно обновлена");
                } else {
                    showErrorAlert("Ошибка при обновлении услуги");
                }
            }
        } catch (IOException e) {
            showErrorAlert("Ошибка загрузки формы: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
            if (database.deleteService(selected.getId())) {
                servicesList.remove(selected);
                showInfoAlert("Услуга успешно удалена");
            } else {
                showErrorAlert("Ошибка при удалении услуги");
            }
        }
    }

    private Service showServiceDialog(Service existingService) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/service_form.fxml"));
        Parent root = loader.load();

        TextField nameField = (TextField) root.lookup("#nameField");
        ComboBox<String> categoryCombo = (ComboBox<String>) root.lookup("#categoryCombo");
        TextField durationField = (TextField) root.lookup("#durationField");
        TextField priceField = (TextField) root.lookup("#priceField");
        Button saveButton = (Button) root.lookup("#saveButton");

        if (nameField == null || categoryCombo == null || durationField == null ||
                priceField == null || saveButton == null) {
            throw new IOException("Не удалось найти элементы формы");
        }

        // Настраиваем выпадающий список категорий
        categoryCombo.getItems().addAll(
                "Парикмахерский зал",
                "Ногтевой сервис",
                "Косметология",
                "Массаж",
                "Услуга визажиста"
        );

        boolean isEdit = existingService != null;

        if (isEdit) {
            nameField.setText(existingService.getName());
            categoryCombo.setValue(existingService.getCategory());
            durationField.setText(existingService.getDuration());
            priceField.setText(String.valueOf(existingService.getPrice()));
            saveButton.setText("Сохранить");
        }

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(isEdit ? "Редактирование услуги" : "Добавление услуги");
        dialog.setScene(new Scene(root));
        dialog.setResizable(false);

        final Service[] result = {null};

        saveButton.setOnAction(e -> {
            // Валидация
            if (nameField.getText().trim().isEmpty()) {
                showAlert("Введите название услуги");
                return;
            }
            if (categoryCombo.getValue() == null) {
                showAlert("Выберите категорию");
                return;
            }
            if (durationField.getText().trim().isEmpty()) {
                showAlert("Введите длительность");
                return;
            }
            if (priceField.getText().trim().isEmpty()) {
                showAlert("Введите стоимость");
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceField.getText().trim());
                if (price <= 0) {
                    showAlert("Стоимость должна быть положительным числом");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Введите корректное число для стоимости");
                return;
            }

            if (isEdit) {
                result[0] = new Service(
                        existingService.getId(),
                        nameField.getText().trim(),
                        categoryCombo.getValue(),
                        durationField.getText().trim(),
                        price
                );
            } else {
                result[0] = new Service(
                        nameField.getText().trim(),
                        categoryCombo.getValue(),
                        durationField.getText().trim(),
                        price
                );
            }
            dialog.close();
        });

        dialog.showAndWait();
        return result[0];
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
    private void clients(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin/admin_customers.fxml", "Панель администратора: клиенты");
    }

    @FXML
    private void appointments(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin/admin_appointments.fxml", "Панель администратора: записи");
    }
}