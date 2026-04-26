module ru.bayazitova.maven.coursebeautysalon {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;


    opens salon.controller to javafx.fxml;
    exports salon;
}