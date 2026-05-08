package salon.controller.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import salon.Service;
import salon.controller.BaseController;
import salon.db.database;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClientPriceListController extends BaseController {
    @FXML
    private TableView<CatalogRow> tableView;
    @FXML
    private TableColumn<CatalogRow, String> categoryColumn;
    @FXML
    private TableColumn<CatalogRow, String> nameColumn;
    @FXML
    private TableColumn<CatalogRow, String> durationColumn;
    @FXML
    private TableColumn<CatalogRow, String> priceColumn;

    @FXML
    private void initialize() {
        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().category()));
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name()));
        durationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().duration()));
        priceColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().price()));

        tableView.setItems(buildGroupedRows(database.getServicesCatalog()));
        tableView.setRowFactory(view -> new TableRow<>() {
            @Override
            protected void updateItem(CatalogRow item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.group()) {
                    setStyle("-fx-background-color: #F4E2E4; -fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        changeWindow(event, "/fxml/client/client_menu.fxml", "Личный кабинет");
    }

    private ObservableList<CatalogRow> buildGroupedRows(List<Service> services) {
        Map<String, List<Service>> grouped = new LinkedHashMap<>();
        for (Service service : services) {
            grouped.computeIfAbsent(service.getCategory(), category -> FXCollections.observableArrayList()).add(service);
        }

        ObservableList<CatalogRow> rows = FXCollections.observableArrayList();
        for (Map.Entry<String, List<Service>> entry : grouped.entrySet()) {
            rows.add(new CatalogRow(entry.getKey(), "", "", "", true));
            for (Service service : entry.getValue()) {
                rows.add(new CatalogRow(
                        "",
                        service.getName(),
                        service.getDuration(),
                        String.format("%.0f руб.", service.getPrice()),
                        false
                ));
            }
        }

        return rows;
    }

    private record CatalogRow(String category, String name, String duration, String price, boolean group) {
    }
}
