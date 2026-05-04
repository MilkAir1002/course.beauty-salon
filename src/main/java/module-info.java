module ru.bayazitova.maven.coursebeautysalon {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.desktop;


    opens salon.controller to javafx.fxml;
    exports salon;
}
