package salon.controller.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import salon.Employer;
import salon.db.database;

import java.io.ByteArrayInputStream;
import java.util.List;

public class MasterInfoController {

    @FXML private ImageView masterPhoto;
    @FXML private Label masterNameLabel;
    @FXML private Label masterPositionLabel;
    @FXML private Label masterDescriptionLabel;
    @FXML private Label pageLabel;

    private List<Employer> masters;
    private int currentIndex = 0;

    public void setMasters(List<Employer> masters) {
        this.masters = masters;
        this.currentIndex = 0;
        showMaster(0);
    }

    private void showMaster(int index) {
        if (masters == null || masters.isEmpty()) return;

        Employer master = masters.get(index);
        masterNameLabel.setText(master.getFullName());
        masterPositionLabel.setText(master.getPosition());
        masterDescriptionLabel.setText(
                        (master.getDetails() != null && !master.getDetails().isBlank()
                                ?    master.getDetails() : "")
        );

        byte[] photoBytes = database.getMasterPhotoById(master.getId());
        if (photoBytes != null && photoBytes.length > 0) {
            try {
                masterPhoto.setImage(new Image(new ByteArrayInputStream(photoBytes)));
            } catch (Exception e) {
                masterPhoto.setImage(null);
            }
        } else {
            masterPhoto.setImage(null);
        }

        pageLabel.setText((index + 1) + " / " + masters.size());
    }

    @FXML
    private void prevMaster() {
        if (currentIndex > 0) {
            currentIndex--;
            showMaster(currentIndex);
        }
    }

    @FXML
    private void nextMaster() {
        if (currentIndex < masters.size() - 1) {
            currentIndex++;
            showMaster(currentIndex);
        }
    }

    @FXML
    private void close() {
        Stage stage = (Stage) masterNameLabel.getScene().getWindow();
        stage.close();
    }
}