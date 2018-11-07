package ru.redsys.rsiam.installer.Form.Impl;

import com.google.common.io.Files;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import ru.redsys.rsiam.installer.Utils.*;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Form0Controller extends AbstractFormController {

    private NewStage newStage = new NewStage();
    private SaveStage saveStage = new SaveStage();
    private Logger logger = Logger.getLogger(getClass().getName());

    @FXML
    public void handleOnNextButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Next - %s", event.getSource()));

        saveStage.setForm1(newStage.openNewStage("/fxml/Form1.fxml", saveStage.getForm1(), saveStage.getForm0()));

        Alerts.informationDialog(null, "Запущена проверка системы, пожалуйста подождите окончания процесса!");

        try {
            Configuration.programPath = new File(".").getCanonicalPath();
            logger.log(Level.INFO, () -> String.format("Program path: %s", Configuration.programPath));

            Configuration.tempPath = Files.createTempDir().getAbsolutePath();
            logger.log(Level.INFO, () -> String.format("Temp path: %s", Configuration.tempPath));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error in getting path", ex.getMessage());
        }

        Utils.javaHome = System.getenv("JAVA_HOME");
        logger.log(Level.INFO, () -> String.format("JAVA_HOME: %s", Utils.javaHome));

        if (Utils.javaHome == null) {
            Alerts.errorDialog("Ошибка!", "Не прописана системная переменная JAVA_HOME" +
                " Пропишите системную переменную и повторно запустите программу");
            Platform.exit();
        }

        Alerts.informationDialog(null, "Проверка системы завершена успешно!");

        saveStage.getForm1().show();
        saveStage.getForm0().hide();
    }
}