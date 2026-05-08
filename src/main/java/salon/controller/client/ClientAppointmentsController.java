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

    @FXML
    private void initialize() {
        serviceColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getServiceName()));
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAppointmentDate()));
        specialistColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSpecialist()));
        priceColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.0f руб.", data.getValue().getPrice())));
        statusColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        statusColumn.setCellFactory(column -> new TableCell<>() {
            private final Label statusLabel = new Label("назначена");
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
                } else if ("назначена".equalsIgnoreCase(appointment.getStatus())) {
                    setText(null);
                    statusLabel.setText(appointment.getStatus());
                    setGraphic(statusBox);
                } else {
                    setText(appointment.getStatus());
                    setGraphic(null);
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
        if (appointment == null) {
            return;
        }

        if (database.cancelCurrentClientAppointment(appointment.getId())) {
            loadAppointments();
            showInfo("Запись отменена.");
        } else {
            showError("Не удалось отменить запись.");
        }
    }
}
