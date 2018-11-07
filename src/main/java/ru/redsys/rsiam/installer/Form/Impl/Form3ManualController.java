package ru.redsys.rsiam.installer.Form.Impl;

import com.google.common.base.Strings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.controlsfx.validation.ValidationSupport;
import ru.redsys.rsiam.installer.Utils.Alerts;
import ru.redsys.rsiam.installer.Utils.Configuration;
import ru.redsys.rsiam.installer.Utils.NewStage;
import ru.redsys.rsiam.installer.Utils.SaveStage;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Form3ManualController extends AbstractFormController {

    public TextField textField_1_1;
    public TextField textField_1_2;
    public TextField textField_1_3;
    public TextField textField_1_4;

    public TextField textField_2_1;
    public TextField textField_2_2;
    public TextField textField_2_3;
    public TextField textField_2_4;

    public TextField textField_3_1;
    public TextField textField_3_2;
    public TextField textField_3_3;
    public TextField textField_3_4;

    public Button button1;
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
            Configuration.buildConfigFileMap.put(Configuration.Params.HOSTNAME_DB_0_0, textField_1_1.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.HOSTNAME_APP_0_0, textField_2_1.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.HOSTNAME_APP_1_0, textField_3_1.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.MAC_DB_0_0, textField_1_2.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.MAC_APP_0_0, textField_2_2.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.MAC_APP_1_0, textField_3_2.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.IP_DB_0_0, textField_1_3.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.IP_APP_0_0, textField_2_3.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.IP_APP_1_0, textField_3_3.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.IP_DB_0_1, Strings.isNullOrEmpty(textField_1_4.getText()) || textField_1_4.isDisable() ? textField_1_3.getText() : textField_1_4.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.IP_APP_0_1, Strings.isNullOrEmpty(textField_2_4.getText()) || textField_2_4.isDisable() ? textField_2_3.getText() : textField_2_4.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.IP_APP_1_1, Strings.isNullOrEmpty(textField_3_4.getText()) || textField_3_4.isDisable() ? textField_3_3.getText() : textField_3_4.getText());

            saveStage.setForm4(newStage.openNewStage("/fxml/Form4.fxml", saveStage.getForm4(), saveStage.getForm3_manual()));

            saveStage.getForm4().show();
            saveStage.getForm3_manual().hide();
        }
    }

    @FXML
    public void handleOnBackButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Back - %s", event.getSource()));

        saveStage.getForm2().setX(saveStage.getForm3_manual().getX());
        saveStage.getForm2().setY(saveStage.getForm3_manual().getY());
        saveStage.getForm2().show();

        saveStage.getForm3_manual().hide();
    }

    @FXML
    private void handleOnOpenButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Configuration File");
        fileChooser.setInitialDirectory(new File(Configuration.programPath));
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("conf files *.conf", "*.conf"),
            new FileChooser.ExtensionFilter("All files *.*", "*.*")
        );

        File file = fileChooser.showOpenDialog(saveStage.getForm3_manual());
        if (file != null) {
            logger.log(Level.INFO, "Open Configuration File: " + file.getAbsolutePath());

            if (Configuration.openConfigFileProperties != null) {
                Configuration.openConfigFileProperties.clear();

                if (saveStage.getForm4() != null) {
                    saveStage.getForm4().close();
                }
                if (saveStage.getForm5() != null) {
                    saveStage.getForm5().close();
                }
                if (saveStage.getForm6() != null) {
                    saveStage.getForm6().close();
                }
                if (saveStage.getForm7() != null) {
                    saveStage.getForm7().close();
                }
                if (saveStage.getForm8() != null) {
                    saveStage.getForm8().close();
                }
                if (saveStage.getForm9() != null) {
                    saveStage.getForm9().close();
                }
                if (saveStage.getForm10() != null) {
                    saveStage.getForm10().close();
                }
                if (saveStage.getForm11() != null) {
                    saveStage.getForm11().close();
                }
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String property = reader.lines().collect(Collectors.joining("\n"));
                Configuration.openConfigFileProperties.load(new StringReader(property.replace("\\", "\\\\")));
            } catch (Exception ex) {
                Alerts.exeptionDialog("Ошибка при открытии файла", "Не удалось прочитать файл с диска", ex.getMessage());
                logger.log(Level.SEVERE, "Exception.", ex.getMessage());
            }

            textField_1_1.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.HOSTNAME_DB_0_0));
            textField_2_1.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.HOSTNAME_APP_0_0));
            textField_3_1.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.HOSTNAME_APP_1_0));
            if (!"auto".equals(Configuration.typeOfInstall)) {
                textField_1_2.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.MAC_DB_0_0));
                textField_2_2.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.MAC_APP_0_0));
                textField_3_2.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.MAC_APP_1_0));
            }
            textField_1_3.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.IP_DB_0_0));
            textField_2_3.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.IP_APP_0_0));
            textField_3_3.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.IP_APP_1_0));
            textField_1_4.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.IP_DB_0_1));
            textField_2_4.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.IP_APP_0_1));
            textField_3_4.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.IP_APP_1_1));
        }
    }

    @FXML
    private void handleOnIPAddresKeyTyped(KeyEvent event) {
        String key = event.getCharacter();
        if (Configuration.isInteger(key) && !".".equals(key)) {
            event.consume();
        }
    }

    private boolean validate() {
        logger.log(Level.INFO, () -> String.format("Is valid: %s", !support.isInvalid()));
        return !support.isInvalid();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if ("auto".equals(Configuration.typeOfInstall)) {
            button2.setDisable(true);

            textField_1_2.setText(Configuration.networkMacAddress.get(1).get(Configuration.networkMacAddress.get(1).size() - 1));
            textField_1_2.setEditable(false);

            textField_2_2.setText(Configuration.networkMacAddress.get(2).get(Configuration.networkMacAddress.get(2).size() - 1));
            textField_2_2.setEditable(false);

            textField_3_2.setText(Configuration.networkMacAddress.get(3).get(Configuration.networkMacAddress.get(3).size() - 1));
            textField_3_2.setEditable(false);

            if (!Configuration.isTwoNetworksDb) {
                textField_1_4.setDisable(true);
            }

            if (!Configuration.isTwoNetworksApp1) {
                textField_2_4.setDisable(true);
            }

            if (!Configuration.isTwoNetworksApp2) {
                textField_3_4.setDisable(true);
            }
        }

        support.registerValidator(textField_1_1, Configuration.hostNameValidatorNotNull);
        support.registerValidator(textField_2_1, Configuration.hostNameValidatorNotNull);
        support.registerValidator(textField_3_1, Configuration.hostNameValidatorNotNull);
        support.registerValidator(textField_1_2, Configuration.macValidatorNotNull);
        support.registerValidator(textField_2_2, Configuration.macValidatorNotNull);
        support.registerValidator(textField_3_2, Configuration.macValidatorNotNull);
        support.registerValidator(textField_1_3, Configuration.ipValidatorNotNull);
        support.registerValidator(textField_2_3, Configuration.ipValidatorNotNull);
        support.registerValidator(textField_3_3, Configuration.ipValidatorNotNull);
        support.registerValidator(textField_1_4, Configuration.ipValidator);
        support.registerValidator(textField_2_4, Configuration.ipValidator);
        support.registerValidator(textField_3_4, Configuration.ipValidator);
    }
}
