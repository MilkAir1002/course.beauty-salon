package salon.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class ClientAppointmentsController extends BaseController {
    @FXML
    private void back(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/client_menu.fxml", "Личный кабинет");
    }
}
