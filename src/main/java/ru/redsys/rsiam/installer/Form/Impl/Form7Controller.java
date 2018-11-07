package ru.redsys.rsiam.installer.Form.Impl;

import com.google.common.base.Strings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Form7Controller extends AbstractFormController {

    public TextField textField_1_0;
    public TextField textField_1_1;
    public TextField textField_1_2;
    public TextField textField_1_3;
    public TextField textField_1_4;
    public TextField textField_1_5;
    public TextField textField_1_6;
    public TextField textField_1_7;

    public GridPane gridPane;

    private NewStage newStage = new NewStage();
    private SaveStage saveStage = new SaveStage();
    private Logger logger = Logger.getLogger(getClass().getName());

    private ValidationSupport support = new ValidationSupport();

    @FXML
    public void handleOnNextButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Next - %s", event.getSource()));

        Configuration.textFieldTrimAndLower(gridPane, false);

        if (validate()) {
            Configuration.buildConfigFileMap.put(Configuration.Params.LOTUS_ADMIN_NAME, textField_1_0.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.LOTUS_MAIL_DOMAIN, textField_1_1.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.LOTUS_INTERNET_ADDRESS_DOMAIN, textField_1_2.getText());

            Pattern pattern = Pattern.compile("\\\\+");
            Matcher matcher = pattern.matcher(textField_1_3.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.LOTUS_CERTIFIERID_FILE, matcher.replaceAll("\\\\\\\\"));
            matcher = pattern.matcher(textField_1_4.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.LOTUS_DEFAULT_DIRECTORY_FOR_ID_FILES, matcher.replaceAll("\\\\\\\\"));

            Configuration.buildConfigFileMap.put(Configuration.Params.MAIL_HOST, Strings.isNullOrEmpty(textField_1_5.getText()) ? "" : textField_1_5.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.MAIL_PORT, Strings.isNullOrEmpty(textField_1_6.getText()) ? "" : textField_1_6.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.MAIL_TO, Strings.isNullOrEmpty(textField_1_7.getText()) ? "" : textField_1_7.getText());

            saveStage.setForm8(newStage.openNewStage("/fxml/Form8.fxml", saveStage.getForm8(), saveStage.getForm7()));

            saveStage.getForm8().show();
            saveStage.getForm7().hide();
        }
    }

    @FXML
    public void handleOnBackButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Back - %s", event.getSource()));

        saveStage.getForm6().setX(saveStage.getForm7().getX());
        saveStage.getForm6().setY(saveStage.getForm7().getY());
        saveStage.getForm6().show();

        saveStage.getForm7().hide();
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
            textField_1_0.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.LOTUS_ADMIN_NAME));
            textField_1_1.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.LOTUS_MAIL_DOMAIN));
            textField_1_2.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.LOTUS_INTERNET_ADDRESS_DOMAIN));
            textField_1_5.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.MAIL_HOST));
            textField_1_6.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.MAIL_PORT));
            textField_1_7.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.MAIL_TO));

            if (!Strings.isNullOrEmpty(Configuration.openConfigFileProperties.getProperty(Configuration.Params.LOTUS_CERTIFIERID_FILE))
                && !Strings.isNullOrEmpty(Configuration.openConfigFileProperties.getProperty(Configuration.Params.LOTUS_DEFAULT_DIRECTORY_FOR_ID_FILES))) {
                Pattern pattern = Pattern.compile("\\\\+");
                Matcher matcher = pattern.matcher(Configuration.openConfigFileProperties.getProperty(Configuration.Params.LOTUS_CERTIFIERID_FILE));
                textField_1_3.setText(matcher.replaceAll("\\\\"));
                matcher = pattern.matcher(Configuration.openConfigFileProperties.getProperty(Configuration.Params.LOTUS_DEFAULT_DIRECTORY_FOR_ID_FILES));
                textField_1_4.setText(matcher.replaceAll("\\\\"));
            }
        }

        support.registerValidator(textField_1_0, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(textField_1_1, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(textField_1_2, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(textField_1_3, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(textField_1_4, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(textField_1_5, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(textField_1_6, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(textField_1_7, Validator.createEmptyValidator("Обязательное поле"));
    }
}