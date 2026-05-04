package salon.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.io.IOException;

public class NewClientAppointmentController extends BaseController {
    @FXML
    private ComboBox<String> serviceComboBox;
    @FXML
    private TextField priceField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> specialistComboBox;

    @FXML
    private void initialize() {
        serviceComboBox.setItems(FXCollections.observableArrayList(
                "Стрижка женская",
                "Маникюр",
                "Окрашивание"
        ));
        specialistComboBox.setItems(FXCollections.observableArrayList(
                "Анна Смирнова",
                "Мария Иванова",
                "Елена Волкова"
        ));
        serviceComboBox.setOnAction(event -> setPrice());
    }

    @FXML
    private void saveAppointment() {
        if (serviceComboBox.getValue() == null || specialistComboBox.getValue() == null || datePicker.getValue() == null) {
            showError("Выберите услугу, специалиста и дату.");
            return;
        }
        System.out.println("Новая запись: " + serviceComboBox.getValue()
                + ", " + specialistComboBox.getValue()
                + ", " + datePicker.getValue());
        showInfo("Запись создана.");
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/client_menu.fxml", "Личный кабинет");
    }

    private void setPrice() {
        String service = serviceComboBox.getValue();
        if ("Стрижка женская".equals(service)) {
            priceField.setText("1800 руб.");
        } else if ("Маникюр".equals(service)) {
            priceField.setText("1500 руб.");
        } else if ("Окрашивание".equals(service)) {
            priceField.setText("4200 руб.");
        } else {
            priceField.clear();
        }
    }
}
