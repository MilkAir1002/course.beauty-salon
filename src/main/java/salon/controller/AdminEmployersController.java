package salon.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import salon.Employer;

import java.io.IOException;

public class AdminEmployersController extends BaseController {
    @FXML
    private TableView<Employer> tableView;
    @FXML
    private TableColumn<Employer, Integer> idColumn;
    @FXML
    private TableColumn<Employer, String> fullNameColumn;
    @FXML
    private TableColumn<Employer, String> birthDateColumn;
    @FXML
    private TableColumn<Employer, String> positionColumn;
    @FXML
    private TableColumn<Employer, String> phoneColumn;

    private ObservableList<Employer> employersList = javafx.collections.FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedBirthDate"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tableView.setItems(employersList);
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/role_selector.fxml", "Салон красоты");
    }

    @FXML
    private void clients(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin_customers.fxml", "Панель администратора: клиенты");
    }

    @FXML
    private void appointments(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin_appointments.fxml", "Панель администратора: записи");
    }

    @FXML
    private void services(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin_services.fxml", "Панель администратора: услуги");
    }

    @FXML
    private void addEmployer(ActionEvent event) throws IOException {
        // Загружаем FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/new_employer.fxml"));
        Parent root = loader.load();

        // Получаем контроллер и устанавливаем слушатель
        NewEmployerController controller = loader.getController();
        // Используем полный путь к вложенному интерфейсу
        controller.setEmployerSaveListener(new NewEmployerController.EmployerSaveListener() {
            @Override
            public void onEmployerSaved(Employer employer) {
                employersList.add(employer);
            }
        });

        // Создаем и показываем окно
        Stage stage = new Stage();
        stage.setTitle("Добавление сотрудника");
        stage.setScene(new Scene(root));
        stage.show();
    }
}