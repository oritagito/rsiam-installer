package ru.redsys.rsiam.installer.Form.Impl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.redsys.rsiam.installer.Utils.*;
import ru.redsys.rsiam.installer.WorkIndicatorDialogs.SingleCmdScript;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Form10Controller extends AbstractFormController {

    public TextField textField;
    public Button button1;

    private NewStage newStage = new NewStage();
    private SaveStage saveStage = new SaveStage();
    private Logger logger = Logger.getLogger(getClass().getName());

    private SingleCmdScript<String, String> wids = null;

    @FXML
    public void handleOnNextButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Next - %s", event.getSource()));

        Alerts.informationDialog("Запущено создание ISO образа!", "Пожалуйста, подождите завершения процесса.");

        wids = new SingleCmdScript<>(saveStage.getForm10().getScene().getWindow(), "Пожалуйста, подождите...");
        wids.addTaskEndNotification(result -> {
            logger.log(Level.INFO, "ISO ready!");
            Alerts.informationDialog("Создание ISO образа завершено успешно!", "");

            if ("auto".equals(Configuration.typeOfInstall)) {
                saveStage.setForm11(newStage.openNewStage("/fxml/Form11.fxml", saveStage.getForm11(), saveStage.getForm10()));

                saveStage.getForm11().show();
                saveStage.getForm10().hide();
            } else {
                saveStage.setFinalForm(newStage.openNewStage("/fxml/FinalForm.fxml", saveStage.getFinalForm(), saveStage.getForm10()));

                saveStage.getFinalForm().show();
                saveStage.getForm10().hide();
            }

            wids = null;
        });

        wids.exec(Configuration.programPath + File.separator + Utils.defaultFolder + File.separator + Utils.mkisofs,
            " " + "-o" + " " + Utils.targetIsoPath + " " +
                "-r -J -no-emul-boot -boot-load-size 4 -boot-info-table -R -b isolinux/isolinux.bin" + " " +
                "-c" + " " + Configuration.programPath + File.separator + Configuration.getConfig().getProperty("defaultName") + File.separator + "isolinux" + File.separator + "boot.cat" + " " +
                Configuration.programPath + File.separator + Configuration.getConfig().getProperty("defaultName"));
    }

    @FXML
    public void handleOnBackButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Back - %s", event.getSource()));

        saveStage.getForm9().setX(saveStage.getForm10().getX());
        saveStage.getForm9().setY(saveStage.getForm10().getY());
        saveStage.getForm9().show();

        saveStage.getForm10().hide();
    }

    @FXML
    private void handleOnOverviewButtonAction(ActionEvent event) {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddhhmmss");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save .iso");
        fileChooser.setInitialFileName("rsiam-" + dateFormatter.format(date));
        fileChooser.setInitialDirectory(new File(Configuration.programPath));
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("ISO files *.iso", "*.iso"),
            new FileChooser.ExtensionFilter("All files *.*", "*.*")
        );

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            textField.setText(file.getAbsolutePath().endsWith(".iso") ? file.getAbsolutePath() : file.getAbsolutePath() + ".iso");
            Utils.targetIsoFileName = file.getAbsoluteFile().getName();
            Utils.targetIsoPath = file.getAbsolutePath().endsWith(".iso") ? file.getAbsolutePath() : file.getAbsolutePath() + ".iso";

            button1.setDisable(false);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}