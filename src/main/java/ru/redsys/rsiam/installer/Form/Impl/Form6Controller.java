package ru.redsys.rsiam.installer.Form.Impl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import ru.redsys.rsiam.installer.Utils.Configuration;
import ru.redsys.rsiam.installer.Utils.NewStage;
import ru.redsys.rsiam.installer.Utils.SaveStage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Form6Controller extends AbstractFormController {

    public TextField textField_1_0;
    public TextField textField_1_1;
    public TextField textField_3_0;
    public TextField textField_3_1;
    public TextField textField_1_4;
    public TextField textField_1_6;
    public TextField textField_2_4;
    public TextField textField_2_6;
    public TextField textField_3_4;
    public TextField textField_3_6;
    public PasswordField textPasswordField_1_5;
    public PasswordField textPasswordField_2_5;
    public PasswordField textPasswordField_3_5;

    public CheckBox checkBox;
    public TabPane tabPane;

    public GridPane gridPane;
    public GridPane gridPane2;
    public GridPane gridPane3;
    public GridPane gridPane4;

    private NewStage newStage = new NewStage();
    private SaveStage saveStage = new SaveStage();
    private Logger logger = Logger.getLogger(getClass().getName());

    private ValidationSupport support = new ValidationSupport();
    private ValidationSupport supportCheckBox = new ValidationSupport();

    @FXML
    public void handleOnNextButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Next - %s", event.getSource()));

        Configuration.textFieldTrimAndLower(gridPane, false);
        Configuration.textFieldTrimAndLower(gridPane2, false);
        Configuration.textFieldTrimAndLower(gridPane3, false);
        Configuration.textFieldTrimAndLower(gridPane4, false);

        if (validate()) {
            Configuration.buildConfigFileMap.put(Configuration.Params.OIM_DB_IP, textField_1_0.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.OIM_DB_SHORT_FQDN, textField_1_1.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.OIM_DB_FULL_FQDN, textField_1_1.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.OIM_DB_USER, Configuration.getConfig().getProperty("oimSchemaName"));
            Configuration.buildConfigFileMap.put(Configuration.Params.OES_DB_USER, Configuration.getConfig().getProperty("oesSchemaName"));
            Configuration.buildConfigFileMap.put(Configuration.Params.SOA_DB_USER, Configuration.getConfig().getProperty("soaSchemaName"));
            Configuration.buildConfigFileMap.put(Configuration.Params.OIM_APP_IP, textField_3_0.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.OIM_APP_FQDN, textField_3_1.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.OIM_DB_NAME, (checkBox.isSelected() ? textField_1_4.getText() : Configuration.getConfig().getProperty("oimDatabaseName")));
            Configuration.buildConfigFileMap.put(Configuration.Params.OIM_DB_PASS, (checkBox.isSelected() ? textPasswordField_1_5.getText() : Configuration.getConfig().getProperty("oimSchemaPassword")));
            Configuration.buildConfigFileMap.put(Configuration.Params.OIM_DB_PORT, (checkBox.isSelected() ? textField_1_6.getText() : Configuration.getConfig().getProperty("oimDatabasePort")));
            Configuration.buildConfigFileMap.put(Configuration.Params.OES_DB_NAME, (checkBox.isSelected() ? textField_2_4.getText() : Configuration.getConfig().getProperty("oesDatabaseName")));
            Configuration.buildConfigFileMap.put(Configuration.Params.OES_DB_PASS, (checkBox.isSelected() ? textPasswordField_2_5.getText() : Configuration.getConfig().getProperty("oesSchemaPassword")));
            Configuration.buildConfigFileMap.put(Configuration.Params.OES_DB_PORT, (checkBox.isSelected() ? textField_2_6.getText() : Configuration.getConfig().getProperty("oesDatabasePort")));
            Configuration.buildConfigFileMap.put(Configuration.Params.OIM_APP_USER, (checkBox.isSelected() ? textField_3_4.getText() : Configuration.getConfig().getProperty("oimUser")));
            Configuration.buildConfigFileMap.put(Configuration.Params.OIM_APP_PASS, (checkBox.isSelected() ? textPasswordField_3_5.getText() : Configuration.getConfig().getProperty("oimPassword")));
            Configuration.buildConfigFileMap.put(Configuration.Params.OIM_APP_PORT, (checkBox.isSelected() ? textField_3_6.getText() : Configuration.getConfig().getProperty("oimPort")));

            saveStage.setForm7(newStage.openNewStage("/fxml/Form7.fxml", saveStage.getForm7(), saveStage.getForm6()));

            saveStage.getForm7().show();
            saveStage.getForm6().hide();
        }
    }

    @FXML
    public void handleOnBackButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Back - %s", event.getSource()));

        saveStage.getForm5().setX(saveStage.getForm6().getX());
        saveStage.getForm5().setY(saveStage.getForm6().getY());
        saveStage.getForm5().show();

        saveStage.getForm6().hide();
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

    @FXML
    private void handleOnCheckBoxAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("handleOnCheckBoxAction - %s", event.getSource()));
        if (checkBox.isSelected()) {
            tabPane.setDisable(false);
        } else {
            tabPane.setDisable(true);
        }
    }

    private boolean validate() {
        if (checkBox.isSelected()) {
            logger.log(Level.INFO, () -> String.format("Is valid: %s", (!support.isInvalid() && !supportCheckBox.isInvalid())));
            return (!support.isInvalid() && !supportCheckBox.isInvalid());
        }
        logger.log(Level.INFO, () -> String.format("Is valid: %s", !support.isInvalid()));
        return !support.isInvalid();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Configuration.openConfigFileProperties != null) {
            textField_1_0.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.OIM_DB_IP));
            textField_1_1.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.OIM_DB_SHORT_FQDN));
            textField_1_4.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.OIM_DB_NAME));
            textPasswordField_1_5.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.OIM_DB_PASS));
            textField_1_6.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.OIM_DB_PORT));
            textField_3_0.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.OIM_APP_IP));
            textField_3_1.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.OIM_APP_FQDN));
            textField_2_4.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.OES_DB_NAME));
            textPasswordField_2_5.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.OES_DB_PASS));
            textField_2_6.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.OES_DB_PORT));
            textField_3_4.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.OIM_APP_USER));
            textPasswordField_3_5.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.OIM_APP_PASS));
            textField_3_6.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.OIM_APP_PORT));
        }

        support.registerValidator(textField_1_0, Configuration.ipValidatorNotNull);
        support.registerValidator(textField_1_1, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(textField_3_0, Configuration.ipValidatorNotNull);
        support.registerValidator(textField_3_1, Validator.createEmptyValidator("Обязательное поле"));

        supportCheckBox.registerValidator(textField_1_4, Validator.createEmptyValidator("Обязательное поле"));
        supportCheckBox.registerValidator(textPasswordField_1_5, Validator.createEmptyValidator("Обязательное поле"));
        supportCheckBox.registerValidator(textField_1_6, Validator.createEmptyValidator("Обязательное поле"));
        supportCheckBox.registerValidator(textField_2_4, Validator.createEmptyValidator("Обязательное поле"));
        supportCheckBox.registerValidator(textPasswordField_2_5, Validator.createEmptyValidator("Обязательное поле"));
        supportCheckBox.registerValidator(textField_2_6, Validator.createEmptyValidator("Обязательное поле"));
        supportCheckBox.registerValidator(textField_3_4, Validator.createEmptyValidator("Обязательное поле"));
        supportCheckBox.registerValidator(textPasswordField_3_5, Validator.createEmptyValidator("Обязательное поле"));
        supportCheckBox.registerValidator(textField_3_6, Validator.createEmptyValidator("Обязательное поле"));
    }
}