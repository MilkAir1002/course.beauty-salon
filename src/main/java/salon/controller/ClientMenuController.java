package salon.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import salon.db.database;

import java.io.IOException;

public class ClientMenuController extends BaseController {
    @FXML
    private void openAppointments(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/client_appointments.fxml", "Мои записи");
    }

    @FXML
    private void openNewAppointment(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/new_client_appointment.fxml", "Новая запись");
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        database.curLog = null;
        changeWindow(event, "/fxml/role_selector.fxml", "Салон красоты");
    }
}
