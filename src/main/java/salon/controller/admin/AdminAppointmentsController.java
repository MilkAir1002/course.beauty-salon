package salon.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import salon.Appointment;
import salon.Service;
import salon.controller.BaseController;
import salon.db.database;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;

public class AdminAppointmentsController extends BaseController {

    // Элементы таблицы
    @FXML
    private TableView<Appointment> tableView;
    @FXML
    private TableColumn<Appointment, Integer> idColumn;
    @FXML
    private TableColumn<Appointment, String> dateColumn;
    @FXML
    private TableColumn<Appointment, String> clientColumn;
    @FXML
    private TableColumn<Appointment, String> masterColumn;
    @FXML
    private TableColumn<Appointment, String> servicesColumn;
    @FXML
    private TableColumn<Appointment, Double> costColumn;
    @FXML
    private TableColumn<Appointment, String> statusColumn;

    // Список записей. Таблица обновляется сама
    private ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Настройка колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("clientLogin"));
        masterColumn.setCellValueFactory(new PropertyValueFactory<>("specialist"));
        servicesColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Форматирование колонки цены
        costColumn.setCellFactory(column -> new TableCell<Appointment, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f ₽", item));
                }
            }
        });

        // Форматирование статуса с цветом
        statusColumn.setCellFactory(column -> new TableCell<Appointment, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("назначена".equals(item)) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if ("отменено".equals(item)) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // Связываем таблицу со списком
        tableView.setItems(appointmentsList);
        // Загружаем данные из базы данных
        loadAppointmentsFromDatabase();
    }

    // Загрузка записей из базы данных
    private void loadAppointmentsFromDatabase() {
        appointmentsList.clear();
        appointmentsList.addAll(database.getAllAppointments());
        tableView.refresh();
    }

    // Добавление записи
    @FXML
    private void addAppointment() throws IOException {
        // Открываем окно с формой
        Appointment result = showAppointmentDialog(null); // Открытие пустой формы
        if (result != null) { // Если пользователь нажал "Сохранить"
            // Сохраняем в базу данных
            if (database.addAppointment(result)) {
                loadAppointmentsFromDatabase(); // Перезагружаем таблицу
                showInfoAlert("Запись успешно добавлена");
            } else {
                showErrorAlert("Ошибка при добавлении записи");
            }
        }
    }

    // Редактирование записи
    @FXML
    private void editAppointment() throws IOException {
        Appointment selected = tableView.getSelectionModel().getSelectedItem(); // Получение выбранной строки из таблицы
        if (selected == null) { // Если запись в таблице не выбрана
            showAlert("Выберите запись для редактирования");
            return;
        }

        Appointment result = showAppointmentDialog(selected); // Открытие заполненной формы
        if (result != null) { // Если пользователь нажал "Сохранить"
            // Обновляем в базе данных
            if (database.updateAppointment(result)) {
                loadAppointmentsFromDatabase(); // Перезагружаем таблицу
                showInfoAlert("Запись успешно обновлена");
            } else {
                showErrorAlert("Ошибка при обновлении записи");
            }
        }
    }

    // Удаление записи
    @FXML
    private void deleteAppointment() {
        Appointment selected = tableView.getSelectionModel().getSelectedItem(); // Получение выбранной строки из таблицы
        if (selected == null) { // Если запись в таблице не выбрана
            showAlert("Выберите запись для удаления");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION); // Создаем всплывающее окно подтверждения (OK/CANCEL)
        confirmation.setTitle("Подтверждение");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Удалить запись #" + selected.getId() + " (клиент: " + selected.getClientLogin() + ")?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) { // Если нажата "OK"
            // Удаляем из базы данных
            if (database.deleteAppointment(selected.getId())) {
                loadAppointmentsFromDatabase(); // Перезагружаем таблицу
                showInfoAlert("Запись успешно удалена");
            } else {
                showErrorAlert("Ошибка при удалении записи");
            }
        }
    }

    // Метод для показа диалога с формой (добавление/редактирование)
    private Appointment showAppointmentDialog(Appointment existingAppointment) throws IOException {
        // Загружаем FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/appointment_form.fxml"));
        Parent root = loader.load();

        // Получаем элементы управления из формы
        DatePicker appointmentDatePicker = (DatePicker) root.lookup("#appointmentDate");
        ComboBox<String> clientCombo = (ComboBox<String>) root.lookup("#clientCombo");
        ComboBox<Service> serviceCombo = (ComboBox<Service>) root.lookup("#serviceCombo");
        ComboBox<String> specialistCombo = (ComboBox<String>) root.lookup("#specialistCombo");
        TextField priceField = (TextField) root.lookup("#priceField");
        Button saveButton = (Button) root.lookup("#saveButton");

        // Блокируем поле ввода цены (только для отображения)
        priceField.setEditable(false);

        // Блокируем выбор мастера до выбора услуги
        specialistCombo.setDisable(true);

        // Загружаем список клиентов из БД (логины клиентов)
        ObservableList<String> clients = FXCollections.observableArrayList();
        for (salon.Customer customer : database.getAllCustomers()) {
            clients.add(customer.getLogin());
        }
        clientCombo.setItems(clients);

        // Загружаем список услуг из БД
        ObservableList<Service> services = database.getServicesCatalog();
        serviceCombo.setItems(services);

        // Настраиваем отображение услуг в ComboBox (показываем только название)
        serviceCombo.setConverter(new StringConverter<Service>() {
            @Override
            public String toString(Service service) {
                return service == null ? "" : service.getName();
            }

            @Override
            public Service fromString(String string) {
                return null;
            }
        });

        // Слушатель выбора услуги: подгружаем мастеров и цену
        serviceCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            specialistCombo.getItems().clear();
            specialistCombo.getSelectionModel().clearSelection();

            if (newVal != null) {
                // Устанавливаем цену
                priceField.setText(String.format("%.2f ₽", newVal.getPrice()));

                ObservableList<String> specialists = database.getSpecialistsByServiceId(newVal.getServiceId());
                specialistCombo.setItems(specialists);
                specialistCombo.setDisable(specialists.isEmpty());
            } else {
                priceField.clear();
                specialistCombo.setDisable(true);
            }
        });
        boolean isEdit = existingAppointment != null; // Определяем режим (Добавление или редактирование)

        // Если редактируем - заполняем поля
        if (isEdit) {
            // Устанавливаем дату
            try {
                LocalDate date = LocalDate.parse(existingAppointment.getAppointmentDate());
                appointmentDatePicker.setValue(date);
            } catch (Exception e) {
                appointmentDatePicker.setValue(null);
            }

            // Устанавливаем клиента
            clientCombo.setValue(existingAppointment.getClientLogin());

            // Находим и устанавливаем нужную услугу по названию
            for (Service service : services) {
                if (service.getName().equals(existingAppointment.getServiceName())) {
                    serviceCombo.setValue(service);
                    break;
                }
            }

            // Устанавливаем мастера (после того как загрузится список мастеров)
            // Используем небольшой таймер, чтобы список мастеров успел загрузиться
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(100));
            pause.setOnFinished(e -> {
                specialistCombo.setValue(existingAppointment.getSpecialist());
                priceField.setText(String.format("%.2f ₽", existingAppointment.getPrice()));
            });
            pause.play();

            saveButton.setText("Сохранить");
        } else {
            saveButton.setText("Добавить");
        }

        // Создаем окно
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL); // Определяем тип модальности. Блокирует все окна приложения
        dialog.setTitle(isEdit ? "Редактирование записи" : "Добавление записи");
        dialog.setScene(new Scene(root)); // Создаем и помещаем в сцену VBOX с формой (root)
        dialog.setResizable(false);

        // Массив для хранения результата
        final Appointment[] result = {null};

        // Обработчик кнопки сохранить
        saveButton.setOnAction(e -> {
            // Валидация (проверка ввода)
            if (appointmentDatePicker.getValue() == null) {
                showAlert("Выберите дату записи");
                return;
            }
            if (clientCombo.getValue() == null) {
                showAlert("Выберите клиента");
                return;
            }
            if (serviceCombo.getValue() == null) {
                showAlert("Выберите услугу");
                return;
            }
            if (specialistCombo.getValue() == null) {
                showAlert("Выберите мастера");
                return;
            }

            // Форматируем дату в строку YYYY-MM-DD
            String formattedDate = appointmentDatePicker.getValue().toString();
            Service selectedService = serviceCombo.getValue();

            // Создаем запись
            if (isEdit) { // Если редактируем
                result[0] = new Appointment(
                        existingAppointment.getId(), // берем id существующей записи
                        clientCombo.getValue(),
                        selectedService.getServiceId(),
                        selectedService.getName(),
                        formattedDate,
                        specialistCombo.getValue(),
                        selectedService.getPrice(),
                        "назначена"
                );
            } else { // Если добавляем новую
                result[0] = new Appointment(
                        clientCombo.getValue(),
                        selectedService.getServiceId(),
                        selectedService.getName(),
                        formattedDate,
                        specialistCombo.getValue(),
                        selectedService.getPrice(),
                        "назначена"
                );
            }

            dialog.close(); // Закрытие окна
        });

        dialog.showAndWait(); // Показать окно и ждать пока пользователь не закончит работу
        return result[0]; // возвращаем запись
    }

    // Навигация
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