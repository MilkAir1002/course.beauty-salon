package salon.controller.admin;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import salon.controller.BaseController;
import salon.db.database;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminStatisticsController extends BaseController {
    @FXML private BarChart<String, Number> revenueChart;
    @FXML private Label totalRevenueLabel;

    private XYChart.Series<String, Number> revenueSeries;

    @FXML
    private void initialize() {
        setupRevenueChart();
        loadRevenueData();
    }

    private void setupRevenueChart() {
        revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Доход компании");

        revenueChart.getData().clear();
        revenueChart.getData().add(revenueSeries);

        revenueChart.setTitle("Доход за последние 3 месяца");
        revenueChart.setLegendVisible(true);
    }

    private void loadRevenueData() {
        // Получаем данные из database
        Map<String, Double> monthlyData = database.getLastThreeMonthsRevenue();

        // Преобразуем для отображения на графике
        Map<String, Double> formattedData = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : monthlyData.entrySet()) {
            String monthName = formatMonthName(entry.getKey());
            formattedData.put(monthName, entry.getValue());
        }

        revenueSeries.getData().clear();

        for (Map.Entry<String, Double> entry : formattedData.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            revenueSeries.getData().add(data);

            // Добавляем всплывающую подсказку
            Platform.runLater(() -> {
                if (data.getNode() != null) {
                    Tooltip tooltip = new Tooltip(String.format("Доход: %.2f ₽", entry.getValue()));
                    tooltip.setStyle(
                            "-fx-font-size: 14px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-color: #333333;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-padding: 8px;" +
                                    "-fx-border-radius: 5px;" +
                                    "-fx-background-radius: 5px;"
                    );
                    Tooltip.install(data.getNode(), tooltip);
                }
            });
        }

        updateTotalRevenue(formattedData);
    }

    private String formatMonthName(String yearMonth) {
        if (yearMonth == null || yearMonth.length() < 7) {
            return yearMonth;
        }

        try {
            int year = Integer.parseInt(yearMonth.substring(0, 4));
            int month = Integer.parseInt(yearMonth.substring(5, 7));
            String[] months = {
                    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
            };
            return months[month - 1] + " " + year;
        } catch (Exception e) {
            return yearMonth;
        }
    }

    private void updateTotalRevenue(Map<String, Double> monthlyData) {
        double total = 0;
        for (double revenue : monthlyData.values()) {
            total += revenue;
        }
        totalRevenueLabel.setText(String.format("%.2f ₽", total));
    }

    @FXML private void logout(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/role_selector.fxml", "Салон красоты");
    }

    @FXML private void employers(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin/admin_employers.fxml", "Панель администратора: сотрудники");
    }

    @FXML private void clients(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin/admin_customers.fxml", "Панель администратора: клиенты");
    }

    @FXML private void appointments(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin/admin_appointments.fxml", "Панель администратора: записи");
    }

    @FXML private void services(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/admin/admin_services.fxml", "Панель администратора: услуги");
    }
}