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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        // Создаём серию для данных
        revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Доход компании");

        revenueChart.getData().clear();
        revenueChart.getData().add(revenueSeries);
    }

    private void loadRevenueData() {
        // Получаем данные за последние 3 месяца
        Map<String, Double> monthlyData = getLastThreeMonthsRevenue();

        // Обновляем график
        revenueSeries.getData().clear();

        for (Map.Entry<String, Double> entry : monthlyData.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            revenueSeries.getData().add(data);
            // Добавляем всплывающую подсказку
            Platform.runLater(() -> {
                if (data.getNode() != null) {
                    Tooltip tooltip = new Tooltip(String.format("Доход: %.2f ₽", entry.getValue()));
                    // Настройка внешнего вида подсказки
                    tooltip.setStyle(
                            "-fx-font-size: 14px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-color: #ffffff;" +
                                    "-fx-text-fill: #333333;" +
                                    "-fx-padding: 8px;" +
                                    "-fx-border-radius: 5px;" +
                                    "-fx-background-radius: 5px;"
                    );
                    Tooltip.install(data.getNode(), tooltip);
                }
            });
        }
        // Обновляем общую сумму
        updateTotalRevenue(monthlyData);
    }

    // Получение данных из базы данных за последние 3 месяца
    private Map<String, Double> getLastThreeMonthsRevenue() {
        Map<String, Double> data = new LinkedHashMap<>();

        String query = "SELECT strftime('%Y-%m', appointment_date) as month, " +
                "COALESCE(SUM(price), 0) as total " +
                "FROM appointments " +
                "WHERE status != 'отменено' " +
                "AND date(appointment_date) >= date('now', '-3 months') " +
                "GROUP BY strftime('%Y-%m', appointment_date) " +
                "ORDER BY month ASC";

        try (Connection conn = DriverManager.getConnection(database.url);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String yearMonth = rs.getString("month");
                double revenue = rs.getDouble("total");
                String monthName = formatMonthName(yearMonth);
                data.put(monthName, revenue);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    // Форматирование названия месяца
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

    // Обновление общей суммы
    private void updateTotalRevenue(Map<String, Double> monthlyData) {
        double total = 0;
        for (double revenue : monthlyData.values()) {
            total += revenue;
        }
        totalRevenueLabel.setText(String.format("%.2f ₽", total));
    }

    // Навигация
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