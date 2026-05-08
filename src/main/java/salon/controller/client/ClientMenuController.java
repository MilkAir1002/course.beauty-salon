package salon.controller.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import salon.controller.BaseController;
import salon.db.database;

import java.io.IOException;

public class ClientMenuController extends BaseController {
    @FXML
    private Label welcomeLabel;

    @FXML
    private void initialize() {
        updateWelcomeText();
    }

    private void updateWelcomeText() {
        String firstName = database.getCurrentClientFirstName();
        if (firstName == null || firstName.isBlank()) {
            firstName = "клиент";
        }
        welcomeLabel.setText("Здравствуйте, " + firstName);
    }

    @FXML
    private void openAppointments(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/client/client_appointments.fxml", "Мои записи");
    }

    @FXML
    private void openNewAppointment(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/client/new_client_appointment.fxml", "Новая запись");
    }

    @FXML
    private void openPriceList(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/client/client_price_list.fxml", "Прайс-лист");
    }

    @FXML
    private void openEditProfile() throws IOException {
        Stage stage = openNewWindow("/fxml/client/edit_client.fxml", "Редактирование данных клиента");
        stage.setOnHidden(event -> updateWelcomeText());
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        database.curLog = null;
        changeWindow(event, "/fxml/role_selector.fxml", "Салон красоты");
    }
}
