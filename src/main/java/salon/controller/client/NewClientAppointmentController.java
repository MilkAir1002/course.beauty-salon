package salon.controller.client;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import salon.controller.BaseController;
import salon.db.database;
import salon.Service;

import java.io.IOException;

public class NewClientAppointmentController extends BaseController {
    @FXML
    private ComboBox<Service> serviceComboBox;
    @FXML
    private TextField priceField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> specialistComboBox;

    @FXML
    private void initialize() {
        ObservableList<Service> services = database.getServicesWithEmployeeAbilities();
        serviceComboBox.setItems(services);
        specialistComboBox.setDisable(true);

        serviceComboBox.setConverter(new StringConverter<Service>() {
            @Override
            public String toString(Service service) {
                return service == null ? "" : service.getName();
            }

            @Override
            public Service fromString(String string) {
                return null;
            }
        });

        serviceComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            specialistComboBox.getSelectionModel().clearSelection();
            specialistComboBox.getItems().clear();

            if (newVal != null) {
                priceField.setText(newVal.getPrice() + " руб.");
                specialistComboBox.setItems(database.getSpecialistsByServiceId(newVal.getId()));
                specialistComboBox.setDisable(specialistComboBox.getItems().isEmpty());
            } else {
                priceField.clear();
                specialistComboBox.setDisable(true);
            }
        });
    }

    @FXML
    private void saveAppointment() {
        Service selectedService = serviceComboBox.getValue();
        if (selectedService == null || specialistComboBox.getValue() == null || datePicker.getValue() == null) {
            showError("Выберите услугу, специалиста и дату.");
            return;
        }

        boolean saved = database.addCurrentClientAppointment(
                selectedService,
                datePicker.getValue().toString(),
                specialistComboBox.getValue()
        );

        if (saved) {
            serviceComboBox.getSelectionModel().clearSelection();
            specialistComboBox.getSelectionModel().clearSelection();
            datePicker.setValue(null);
            priceField.clear();
            showInfo("Запись создана.");
        } else {
            showError("Не удалось создать запись.");
        }
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/client/client_menu.fxml", "Личный кабинет");
    }
}
