package ru.redsys.rsiam.installer.Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.redsys.rsiam.installer.Main;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewStage {

    private Logger logger = Logger.getLogger(getClass().getName());

    public Stage openNewStage(String fxml, Stage newStage, Stage parentStage) {
        logger.log(Level.INFO, () -> String.format("openNewStage - %s", fxml));
        if (newStage == null) {
            newStage = createNewStage(fxml, parentStage);
        } else {
            newStage.setX(parentStage.getX());
            newStage.setY(parentStage.getY());
        }
        return newStage;
    }

    private Stage createNewStage(String fxml, Stage parentStage) {
        Stage stage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxml));
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);
            stage.setX(parentStage.getX());
            stage.setY(parentStage.getY());
            stage.initStyle(StageStyle.DECORATED);
            stage.setResizable(false);
            stage.setTitle("RS: Управление доступом");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/logo.png")));
            stage.setScene(scene);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to create new Window", ex);
        }
        return stage;

    }

    public void openHelpStage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/Help.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 300, 100);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.setResizable(false);
            stage.setTitle("О программе");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/logo.png")));
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to create new Window", ex.getMessage());
        }
    }
}