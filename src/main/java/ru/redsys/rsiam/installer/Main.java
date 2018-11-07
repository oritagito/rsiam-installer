package ru.redsys.rsiam.installer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.redsys.rsiam.installer.Utils.Alerts;
import ru.redsys.rsiam.installer.Utils.PowerShellUtils;
import ru.redsys.rsiam.installer.Utils.SaveStage;

import java.util.Locale;

public class Main extends Application {

    private SaveStage saveStage = new SaveStage();

    public static void main(String[] args) {
        Locale.setDefault(new Locale("ru", "RU"));
        System.setProperty("line.separator", "\n");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Form0.fxml"));
        primaryStage.setTitle("RS: Управление доступом");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/logo.png")));
        primaryStage.setOnCloseRequest(event -> stop());
        primaryStage.show();

        saveStage.setForm0(primaryStage);
    }

    @Override
    public void stop() {
        if (Alerts.confirmDialog()) {
            if (PowerShellUtils.powerShell != null) {
                PowerShellUtils.powerShell.close();
            }
            Platform.exit();
        }
    }
}
