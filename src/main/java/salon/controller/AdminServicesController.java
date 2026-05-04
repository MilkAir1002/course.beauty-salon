package salon.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class AdminServicesController  extends BaseController {
    // левая панель
    @FXML
    private void logout(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/role_selector.fxml", "Салон красоты");
    }
    @FXML
    private void employers(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin_employers.fxml", "Панель администратора: сотрудники");
    }
    @FXML
    private void clients(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin_customers.fxml", "Панель администратора: клиенты");
    }
    @FXML
    private void appointments(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin_appointments.fxml", "Панель администратора: записи");
    }
}
