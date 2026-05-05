package salon.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import salon.Employer;

import java.io.IOException;
import java.util.Optional;

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

    // МЕТОД ДЛЯ УДАЛЕНИЯ
    @FXML
    private void deleteEmployer(ActionEvent event) {
        Employer selectedEmployer = tableView.getSelectionModel().getSelectedItem();

        if (selectedEmployer == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Нет выбора");
            alert.setHeaderText("Сотрудник не выбран");
            alert.setContentText("Пожалуйста, выберите сотрудника для удаления.");
            alert.showAndWait();
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Подтверждение удаления");
        confirmationAlert.setHeaderText("Удаление сотрудника");
        confirmationAlert.setContentText("Вы уверены, что хотите удалить сотрудника \"" +
                selectedEmployer.getFullName() + "\"?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            employersList.remove(selectedEmployer);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Успешно");
            successAlert.setHeaderText("Сотрудник удален");
            successAlert.setContentText("Сотрудник был успешно удален.");
            successAlert.showAndWait();
        }
    }

    // НОВЫЙ БЛОК: МЕТОД ДЛЯ РЕДАКТИРОВАНИЯ
    @FXML
    private void editEmployer(ActionEvent event) {
        // Получаем выбранного сотрудника
        Employer selectedEmployer = tableView.getSelectionModel().getSelectedItem();

        // Проверяем, выбран ли сотрудник
        if (selectedEmployer == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Нет выбора");
            alert.setHeaderText("Сотрудник не выбран");
            alert.setContentText("Пожалуйста, выберите сотрудника для редактирования.");
            alert.showAndWait();
            return;
        }

        try {
            // Загружаем FXML окна добавления
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/new_employer.fxml"));
            Parent root = loader.load();

            // Получаем контроллер
            NewEmployerController controller = loader.getController();

            // Устанавливаем слушатель для обновления
            controller.setEmployerUpdateListener((oldEmployer, updatedEmployer) -> {
                int index = employersList.indexOf(oldEmployer);
                if (index != -1) {
                    employersList.set(index, updatedEmployer);
                    tableView.refresh();
                }
            });

            // Загружаем данные выбранного сотрудника в форму
            controller.setEmployerForEdit(selectedEmployer);

            // Создаем и показываем окно
            Stage stage = new Stage();
            stage.setTitle("Редактирование сотрудника");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось открыть окно редактирования");
            alert.setContentText("Ошибка: " + e.getMessage());
            alert.showAndWait();
        }
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/new_employer.fxml"));
        Parent root = loader.load();

        NewEmployerController controller = loader.getController();
        controller.setEmployerSaveListener(employer -> employersList.add(employer));

        Stage stage = new Stage();
        stage.setTitle("Добавление сотрудника");
        stage.setScene(new Scene(root));
        stage.show();
    }
}