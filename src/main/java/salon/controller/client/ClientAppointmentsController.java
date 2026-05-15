package salon.controller.client;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import salon.controller.BaseController;
import salon.db.database;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ClientAppointmentsController extends BaseController {
    @FXML
    private TableView<database.ClientAppointment> appointmentsTable;
    @FXML
    private TableColumn<database.ClientAppointment, String> serviceColumn;
    @FXML
    private TableColumn<database.ClientAppointment, String> dateColumn;
    @FXML
    private TableColumn<database.ClientAppointment, String> specialistColumn;
    @FXML
    private TableColumn<database.ClientAppointment, String> priceColumn;
    @FXML
    private TableColumn<database.ClientAppointment, database.ClientAppointment> statusColumn;

    // Исправленный форматтер: теперь он соответствует вашим данным 2026-05-22
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    private void initialize() {
        serviceColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getServiceName()));
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAppointmentDate()));
        specialistColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSpecialist()));
        priceColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.0f руб.", data.getValue().getPrice())));

        statusColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        statusColumn.setCellFactory(column -> new TableCell<>() {
            private final Label statusLabel = new Label();
            private final Button cancelButton = new Button("Отменить");
            private final HBox statusBox = new HBox(30, statusLabel, cancelButton);

            {
                statusBox.setStyle("-fx-alignment: center-left;");
                cancelButton.setStyle("-fx-background-color: #FF7777; -fx-background-radius: 8; -fx-text-fill: white;");
                cancelButton.setOnAction(event -> cancelAppointment(getItem()));
            }

            @Override
            protected void updateItem(database.ClientAppointment appointment, boolean empty) {
                super.updateItem(appointment, empty);

                if (empty || appointment == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                String currentStatus = appointment.getStatus();
                boolean isPast = false;

                try {
                    // Используем LocalDate, так как в логах только дата без времени
                    LocalDate appointmentDate = LocalDate.parse(appointment.getAppointmentDate(), formatter);
                    // Сравниваем с текущим днем. Если дата записи Раньше сегодняшней — она прошла.
                    isPast = appointmentDate.isBefore(LocalDate.now());
                } catch (Exception e) {
                    System.err.println("Ошибка парсинга даты: " + appointment.getAppointmentDate());
                }

                if ("назначена".equalsIgnoreCase(currentStatus)) {
                    if (isPast) {
                        setGraphic(null);
                        setText("исполнено");
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    } else {
                        statusLabel.setText(currentStatus);
                        setGraphic(statusBox);
                        setText(null);
                        setStyle("");
                    }
                } else {
                    setGraphic(null);
                    setText(currentStatus);
                    setStyle("-fx-text-fill: #95a5a6;");
                }
            }
        });

        loadAppointments();
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/client/client_menu.fxml", "Личный кабинет");
    }

    private void loadAppointments() {
        appointmentsTable.setItems(database.getCurrentClientAppointments());
    }

    private void cancelAppointment(database.ClientAppointment appointment) {
        if (appointment == null) return;

        if (database.cancelCurrentClientAppointment(appointment.getId())) {
            loadAppointments();
            showInfo("Запись отменена.");
        } else {
            showError("Не удалось отменить запись.");
        }
    }
}