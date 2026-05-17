package salon.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import salon.Appointment;
import salon.controller.BaseController;
import salon.db.database;

import java.io.IOException;
import java.util.Optional;

public class AdminAppointmentsController extends BaseController {
    @FXML private TableView<Appointment> tableView;
    @FXML private TableColumn<Appointment, Integer> idColumn;
    @FXML private TableColumn<Appointment, String> dateColumn;
    @FXML private TableColumn<Appointment, String> clientColumn;
    @FXML private TableColumn<Appointment, String> masterColumn;
    @FXML private TableColumn<Appointment, String> servicesColumn;
    @FXML private TableColumn<Appointment, Double> costColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;

    private ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Настройка привязки колонок к полям класса Appointment
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("clientLogin"));
        masterColumn.setCellValueFactory(new PropertyValueFactory<>("specialist"));
        servicesColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Форматирование колонки цены
        costColumn.setCellFactory(column -> new TableCell<Appointment, Double>() {
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

        // Форматирование статуса с цветом
        statusColumn.setCellFactory(column -> new TableCell<Appointment, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("назначена".equals(item)) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if ("отменено".equals(item)) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        tableView.setItems(appointmentsList);
        loadAppointmentsFromDatabase();
    }

    private void loadAppointmentsFromDatabase() {
        appointmentsList.clear();
        appointmentsList.addAll(database.getAllAppointmentsForAdmin());
        tableView.refresh();
    }

    @FXML
    private void addAppointment(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin/add_appointment.fxml", "Добавление записи");
    }

    @FXML
    private void editAppointment() {
        Appointment selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Выберите запись для редактирования");
            return;
        }
        showInfoAlert("Функция редактирования записи будет реализована позже");
    }

    @FXML
    private void deleteAppointment() {
        Appointment selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Выберите запись для удаления");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Удалить запись #" + selected.getId() + "?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (database.deleteAppointment(selected.getId())) {
                loadAppointmentsFromDatabase();
                showInfoAlert("Запись успешно удалена");
            } else {
                showErrorAlert("Ошибка при удалении записи");
            }
        }
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
    private void services(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin/admin_services.fxml", "Панель администратора: услуги");
    }
}