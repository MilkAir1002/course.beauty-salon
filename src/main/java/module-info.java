module ru.bayazitova.maven.coursebeautysalon {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;

    opens salon.controller to javafx.fxml;
    exports salon;
    opens salon.controller.client to javafx.fxml;
}
