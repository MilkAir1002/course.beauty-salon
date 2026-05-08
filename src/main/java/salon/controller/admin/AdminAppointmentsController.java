package salon.controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import salon.controller.BaseController;

import java.io.IOException;

public class AdminAppointmentsController  extends BaseController {
    // левая панель
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
