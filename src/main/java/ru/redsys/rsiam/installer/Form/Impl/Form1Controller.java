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

public class Form1Controller extends AbstractFormController {

    public RadioButton radioButton3;
    public RadioButton radioButton5;
    public RadioButton radioButton7;

    private NewStage newStage = new NewStage();
    private SaveStage saveStage = new SaveStage();
    private Logger logger = Logger.getLogger(getClass().getName());

    @FXML
    public void handleOnNextButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Next - %s", event.getSource()));

        saveStage.setForm2(newStage.openNewStage("/fxml/Form2.fxml", saveStage.getForm2(), saveStage.getForm1()));

        saveStage.getForm2().show();
        saveStage.getForm1().hide();
    }

    @FXML
    public void handleOnBackButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Back - %s", event.getSource()));

        saveStage.getForm0().setX(saveStage.getForm1().getX());
        saveStage.getForm0().setY(saveStage.getForm1().getY());
        saveStage.getForm0().show();

        saveStage.getForm1().hide();
    }

    @FXML
    private void handleOnRadioButton3Action(ActionEvent event) {
        if (radioButton3.isSelected()) {
            Configuration.numOfServers = 3;
            logger.log(Level.INFO, () -> String.format("radioButton3 - %s", event.getSource()));
        }
    }

    //TODO Добавить поддержку конфигурации из 5 серверов
    @FXML
    private void handleOnRadioButton5Action(ActionEvent event) {
        if (radioButton5.isSelected()) {
            Configuration.numOfServers = 5;
            logger.log(Level.INFO, () -> String.format("radioButton5 - %s", event.getSource()));
        }
    }

    //TODO Добавить поддержку конфигурации из 7 серверов
    @FXML
    private void handleOnRadioButton7Action(ActionEvent event) {
        if (radioButton7.isSelected()) {
            Configuration.numOfServers = 7;
            logger.log(Level.INFO, () -> String.format("radioButton7 - %s", event.getSource()));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup group = new ToggleGroup();
        radioButton3.setToggleGroup(group);
        radioButton5.setToggleGroup(group);
        radioButton7.setToggleGroup(group);
    }
}