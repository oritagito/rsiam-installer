package ru.redsys.rsiam.installer.Form.Impl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import org.controlsfx.validation.ValidationSupport;
import ru.redsys.rsiam.installer.Utils.Configuration;
import ru.redsys.rsiam.installer.Utils.NewStage;
import ru.redsys.rsiam.installer.Utils.SaveStage;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Form4Controller extends AbstractFormController {

    public TextField textField_0_2;
    public TextField textField_1_2;
    public TextField textField_2_2;
    public TextField textField_0_5;
    public TextField textField_1_5;

    public Button button2;

    public GridPane gridPane;

    private NewStage newStage = new NewStage();
    private SaveStage saveStage = new SaveStage();
    private Logger logger = Logger.getLogger(getClass().getName());

    private ValidationSupport support = new ValidationSupport();

    @FXML
    public void handleOnNextButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Next - %s", event.getSource()));

        Configuration.textFieldTrimAndLower(gridPane, true);

        if (validate()) {
            Configuration.buildConfigFileMap.put(Configuration.Params.GATEWAY, textField_0_2.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.NAMESERVER, textField_1_2.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.NETMASK, textField_2_2.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.APPLICATION_CLUSTER_VIP, textField_0_5.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.APP_CLUSTER_FQDN, textField_1_5.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.SSO_CLUSTER_FQDN, textField_1_5.getText());

            try {
                Configuration.buildConfigFileMap.put(Configuration.Params.KA_SUBNET_PREFIX, Configuration.convertNetmaskToCidr(textField_2_2.getText()));
            } catch (UnknownHostException ex) {
                logger.log(Level.SEVERE, "UnknownHostException: " + textField_2_2.getText(), ex.getMessage());
            }

            saveStage.setForm5(newStage.openNewStage("/fxml/Form5.fxml", saveStage.getForm5(), saveStage.getForm4()));

            saveStage.getForm5().show();
            saveStage.getForm4().hide();
        }
    }

    @FXML
    public void handleOnBackButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Back - %s", event.getSource()));

        saveStage.getForm3_manual().setX(saveStage.getForm4().getX());
        saveStage.getForm3_manual().setY(saveStage.getForm4().getY());
        saveStage.getForm3_manual().show();

        saveStage.getForm4().hide();
    }

    @FXML
    private void handleOnIPAddresKeyTyped(KeyEvent event) {
        String key = event.getCharacter();
        if (Configuration.isInteger(key) && !".".equals(key)) {
            event.consume();
        }
    }

    @FXML
    private void handleOnDigitKeyTyped(KeyEvent event) {
        String key = event.getCharacter();
        if (Configuration.isInteger(key)) {
            event.consume();
        }
    }

    private boolean validate() {
        logger.log(Level.INFO, () -> String.format("Is valid: %s", !support.isInvalid()));
        return !support.isInvalid();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Configuration.openConfigFileProperties != null) {
            textField_0_2.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.GATEWAY));
            textField_1_2.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.NAMESERVER));
            textField_2_2.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.NETMASK));
            textField_0_5.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.APPLICATION_CLUSTER_VIP));
            textField_1_5.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.APP_CLUSTER_FQDN));
        }

        support.registerValidator(textField_0_2, Configuration.ipValidatorNotNull);
        support.registerValidator(textField_1_2, Configuration.ipValidatorNotNull);
        support.registerValidator(textField_2_2, Configuration.ipValidatorNotNull);
        support.registerValidator(textField_0_5, Configuration.ipValidatorNotNull);
        support.registerValidator(textField_1_5, Configuration.hostNameValidatorNotNull);
    }
}