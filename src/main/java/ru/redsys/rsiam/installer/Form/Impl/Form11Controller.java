package ru.redsys.rsiam.installer.Form.Impl;

import com.google.common.base.Strings;
import com.profesorfalken.jpowershell.PowerShellResponse;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import ru.redsys.rsiam.installer.Utils.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.redsys.rsiam.installer.Utils.PowerShellUtils.*;

public class Form11Controller extends AbstractFormController {

    public ComboBox<String> comboBox_1_1;
    public ComboBox<String> comboBox_1_2;
    public ComboBox<String> comboBox_1_3;

    public TextField textField_3_2;

    public Button button1;

    private Map<String, String> pathBuilder = new HashMap<>();

    private NewStage newStage = new NewStage();
    private SaveStage saveStage = new SaveStage();
    private Logger logger = Logger.getLogger(getClass().getName());

    @FXML
    public void handleOnNextButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Next - %s", event.getSource()));

        button1.setDisable(true);

        Alerts.informationDialog("Запущен процесс загрузки ISO образа!", "Пожалуйста, подождите завершения процесса.");

        saveStage.getForm11().getScene().setCursor(Cursor.WAIT);

        Utils.datastoreIsoPath = "[" + comboBox_1_2.getValue() + "] " + comboBox_1_3.getValue() + "\\" + Utils.targetIsoFileName;

        Task<PowerShellResponse> task = new Task<PowerShellResponse>() {
            @Override
            protected PowerShellResponse call() {
                String args = "-localPath" + " " + "\"" + Utils.targetIsoPath + "\"" + " " +
                    "-remotePath" + " " + "\"" + textField_3_2.getText() + "\"";

                return executePowerShellScript(VMCopyISO, args);
            }
        };

        task.setOnSucceeded(e -> {
            saveStage.getForm11().getScene().setCursor(Cursor.DEFAULT);

            if (task.getValue() == null) {
                logger.log(Level.SEVERE, "Creation error");
                Alerts.errorDialog("Ошибка при загрузке ISO образа", "Не удалось загрузить ISO образ");
                return;
            }

            PowerShellResponse result = task.getValue();

            if (result.getCommandOutput().contains("True") && !result.isError()) {
                Alerts.informationDialog("Загрузка ISO образа завершена успешно!", "");

                String macDB = Configuration.networkMacAddress.get(1).get(Configuration.networkMacAddress.get(1).size() - 1).trim();
                String macApp1 = Configuration.networkMacAddress.get(2).get(Configuration.networkMacAddress.get(2).size() - 1).trim();
                String macApp2 = Configuration.networkMacAddress.get(3).get(Configuration.networkMacAddress.get(3).size() - 1).trim();

                logger.log(Level.INFO, "DB MAC: " + macDB);
                logger.log(Level.INFO, "APP1 MAC: " + macApp1);
                logger.log(Level.INFO, "APP2 MAC: " + macApp2);

                String args = "-macDb" + " " + "\"" + macDB + "\"" + " " +
                    "-macApp1" + " " + "\"" + macApp1 + "\"" + " " +
                    "-macApp2" + " " + "\"" + macApp2 + "\"" + " " +
                    "-isoPath" + " " + "\"" + Utils.datastoreIsoPath + "\"";

                result = executePowerShellScript(VMMountISO, args);

                if (result == null) {
                    logger.log(Level.SEVERE, "Mouning error");
                    Alerts.errorDialog("Ошибка при монтировании ISO образа", "Не удалось примонтировать ISO образ");
                    return;
                }

                saveStage.setFinalForm(newStage.openNewStage("/fxml/FinalForm.fxml", saveStage.getFinalForm(), saveStage.getForm11()));

                saveStage.getFinalForm().show();
                saveStage.getForm11().hide();
            } else {
                logger.log(Level.SEVERE, "Creation error", result);
                Alerts.errorDialog("Ошибка при загрузке ISO образа", "Не удалось загрузить ISO образ. Описание ошибки: " + result.getCommandOutput());
            }
        });

        new Thread(task).start();
    }

    @FXML
    public void handleOnBackButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Back - %s", event.getSource()));

        saveStage.getForm10().setX(saveStage.getForm11().getX());
        saveStage.getForm10().setY(saveStage.getForm11().getY());
        saveStage.getForm10().show();

        saveStage.getForm11().hide();
    }

    @FXML
    public void handleOnComboBox_1_1TextChanged(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("comboBox_1_1 text changed - %s", event.getSource()));

        String args = "-Folder" + " " + "\"" + "vmstore:" + "\\" + comboBox_1_1.getValue() + "\"";

        PowerShellResponse result = executePowerShellScript(VMMoveLocationChildren, args);

        if (result == null) {
            logger.log(Level.SEVERE, "Move error to: ", args);
            return;
        }

        if (comboBox_1_2.isDisable()) {
            comboBox_1_2.setDisable(false);
        }

        comboBox_1_2.getItems().clear();
        comboBox_1_2.getItems().addAll(powerShellResponseToList(executePowerShellScript(VMGetLocationChildren, "")));

        pathBuilder.remove("datacenter");
        pathBuilder.put("datacenter", comboBox_1_1.getValue() + "\\");
        updateTextField();
    }

    @FXML
    public void handleOnComboBox_1_2TextChanged(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("comboBox_1_2 text changed - %s", event.getSource()));

        String args = "-Folder" + " " + "\"" + "vmstore:" + "\\" + comboBox_1_1.getValue() + "\\" + comboBox_1_2.getValue() + "\"";

        PowerShellResponse result = executePowerShellScript(VMMoveLocationChildren, args);

        if (result == null) {
            logger.log(Level.SEVERE, "Move error to: ", args);
            return;
        }

        if (comboBox_1_3.isDisable()) {
            comboBox_1_3.setDisable(false);
        }

        comboBox_1_3.getItems().clear();
        comboBox_1_3.getItems().addAll(powerShellResponseToList(executePowerShellScript(VMGetLocationChildren, "")));

        pathBuilder.remove("datastore");
        pathBuilder.put("datastore", comboBox_1_2.getValue() + "\\");
        updateTextField();
    }

    @FXML
    public void handleOnComboBox_1_3TextChanged(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("comboBox_1_3 text changed - %s", event.getSource()));
        pathBuilder.remove("folder");
        pathBuilder.put("folder", comboBox_1_3.getValue());
        updateTextField();

        button1.setDisable(false);
    }

    private void updateTextField() {
        logger.log(Level.INFO, "updateTextField");
        String result = pathBuilder.get("entry") +
            (Strings.isNullOrEmpty(pathBuilder.get("datacenter")) ? "" : pathBuilder.get("datacenter")) +
            (Strings.isNullOrEmpty(pathBuilder.get("datastore")) ? "" : pathBuilder.get("datastore")) +
            (Strings.isNullOrEmpty(pathBuilder.get("folder")) ? "" : pathBuilder.get("folder"));

        textField_3_2.setEditable(true);
        textField_3_2.setText(result);
        textField_3_2.setEditable(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executePowerShellScript(VMSetLocation, "");
        comboBox_1_1.getItems().addAll(powerShellResponseToList(executePowerShellScript(VMGetLocationChildren, "")));

        pathBuilder.put("entry", "vmstore:\\");
    }
}