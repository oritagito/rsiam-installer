package ru.redsys.rsiam.installer.Form.Impl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import ru.redsys.rsiam.installer.Utils.Configuration;
import ru.redsys.rsiam.installer.Utils.NewStage;
import ru.redsys.rsiam.installer.Utils.SaveStage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Form2Controller extends AbstractFormController {

    public RadioButton radioButtonAuto;
    public RadioButton radioButtonManual;

    private NewStage newStage = new NewStage();
    private SaveStage saveStage = new SaveStage();
    private Logger logger = Logger.getLogger(getClass().getName());

    @FXML
    public void handleOnNextButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Next - %s", event.getSource()));

        if ("auto".equals(Configuration.typeOfInstall)) {
            saveStage.setForm3_auto(newStage.openNewStage("/fxml/Form3Auto.fxml", saveStage.getForm3_auto(), saveStage.getForm2()));
            saveStage.getForm3_auto().show();
        } else {
            saveStage.setForm3_manual(newStage.openNewStage("/fxml/Form3Manual.fxml", saveStage.getForm3_manual(), saveStage.getForm2()));
            saveStage.getForm3_manual().show();
        }

        saveStage.getForm2().hide();
    }

    @FXML
    public void handleOnBackButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Back - %s", event.getSource()));

        saveStage.getForm1().setX(saveStage.getForm2().getX());
        saveStage.getForm1().setY(saveStage.getForm2().getY());
        saveStage.getForm1().show();

        saveStage.getForm2().hide();
    }

    @FXML
    private void handleOnRadioButtonAutoAction(ActionEvent event) {
        if (radioButtonAuto.isSelected()) {
            Configuration.typeOfInstall = "auto";
            logger.log(Level.INFO, () -> String.format("radioButtonAuto - %s", event.getSource()));
        }
    }

    @FXML
    private void handleOnRadioButtonManualAction(ActionEvent event) {
        if (radioButtonManual.isSelected()) {
            Configuration.typeOfInstall = "manual";
            logger.log(Level.INFO, () -> String.format("radioButtonManual - %s", event.getSource()));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup group = new ToggleGroup();
        radioButtonAuto.setToggleGroup(group);
        radioButtonManual.setToggleGroup(group);
    }
}