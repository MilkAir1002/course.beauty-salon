package salon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Image icon = new Image(getClass().getResourceAsStream("/fxml/logo1.png"));

        // Устанавливаем иконку для окна приложения
        primaryStage.getIcons().add(icon);

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/role_selector.fxml"));
        primaryStage.setTitle("Салон красоты");
        primaryStage.setScene(new Scene(root, 1030, 831));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
