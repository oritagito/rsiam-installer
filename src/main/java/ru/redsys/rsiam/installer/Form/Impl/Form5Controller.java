package ru.redsys.rsiam.installer.Form.Impl;

import com.google.common.base.Strings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import ru.redsys.rsiam.installer.Utils.Configuration;
import ru.redsys.rsiam.installer.Utils.NewStage;
import ru.redsys.rsiam.installer.Utils.SaveStage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Form5Controller extends AbstractFormController {

    public TextField textField_1_0;
    public TextField textField1_1_0;
    public TextField textField1_1_1;
    public TextField textField1_3_0;
    public TextField textField1_3_1;

    public TextArea textArea2;
    public TextArea textArea3;

    public CheckBox checkBox;
    public TabPane tabPane;

    public GridPane gridPane;
    public GridPane gridPane2;

    public Label label1;
    public Tooltip toolTip1;

    public Label label2;
    public Tooltip toolTip2;

    private NewStage newStage = new NewStage();
    private SaveStage saveStage = new SaveStage();
    private Logger logger = Logger.getLogger(getClass().getName());

    private ValidationSupport support = new ValidationSupport();
    private ValidationSupport supportCheckBox = new ValidationSupport();

    @FXML
    public void handleOnNextButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Next - %s", event.getSource()));

        Configuration.textFieldTrimAndLower(gridPane, true);
        Configuration.textFieldTrimAndLower(gridPane2, true);

        if (checkBox.isSelected()) {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            options.setLineBreak(DumperOptions.LineBreak.UNIX);

            Yaml yamlArea = new Yaml(options);
            Configuration.webgateTargetSystem = yamlArea.load(textArea2.getText());
            Configuration.webgateFilters = yamlArea.load(textArea3.getText());
        }

        if (validate()) {
            Configuration.buildConfigFileMap.put(Configuration.Params.APP_RKASV_FQDN, Strings.isNullOrEmpty(textField_1_0.getText()) ? "" : textField_1_0.getText());
            Configuration.buildConfigFileMap.put(Configuration.Params.FED_SPE_VIP_FQDN, (checkBox.isSelected() ? textField1_1_0.getText() : Configuration.getConfig().getProperty(Configuration.Params.FED_SPE_VIP_FQDN)));
            Configuration.buildConfigFileMap.put(Configuration.Params.FED_SPE_PORT, (checkBox.isSelected() ? textField1_1_1.getText() : Configuration.getConfig().getProperty(Configuration.Params.FED_SPE_PORT)));
            Configuration.buildConfigFileMap.put(Configuration.Params.FED_SOE_VIP_FQDN, (checkBox.isSelected() ? textField1_3_0.getText() : Configuration.getConfig().getProperty(Configuration.Params.FED_SOE_VIP_FQDN)));
            Configuration.buildConfigFileMap.put(Configuration.Params.FED_SOE_PORT, (checkBox.isSelected() ? textField1_3_1.getText() : Configuration.getConfig().getProperty(Configuration.Params.FED_SOE_PORT)));

            saveStage.setForm6(newStage.openNewStage("/fxml/Form6.fxml", saveStage.getForm6(), saveStage.getForm5()));

            saveStage.getForm6().show();
            saveStage.getForm5().hide();
        }
    }

    @FXML
    public void handleOnBackButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Back - %s", event.getSource()));

        saveStage.getForm4().setX(saveStage.getForm5().getX());
        saveStage.getForm4().setY(saveStage.getForm5().getY());
        saveStage.getForm4().show();

        saveStage.getForm5().hide();
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

    @FXML
    public void onMouseEntered(MouseEvent mouseEvent) {
        logger.log(Level.INFO, () -> String.format("onMouseEntered - %s", mouseEvent.getSource()));
        toolTip1.show(saveStage.getForm5());
    }

    @FXML
    public void onMouseEntered2(MouseEvent mouseEvent) {
        logger.log(Level.INFO, () -> String.format("onMouseEntered2 - %s", mouseEvent.getSource()));
        toolTip2.show(saveStage.getForm5());
    }

    @FXML
    public void onMouseExited(MouseEvent mouseEvent) {
        logger.log(Level.INFO, () -> String.format("onMouseExited - %s", mouseEvent.getSource()));
        toolTip1.hide();
        toolTip2.hide();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Configuration.openConfigFileProperties != null) {
            textField_1_0.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.APP_RKASV_FQDN));
            textField1_1_0.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.FED_SPE_VIP_FQDN));
            textField1_1_1.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.FED_SPE_PORT));
            textField1_3_0.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.FED_SOE_VIP_FQDN));
            textField1_3_1.setText(Configuration.openConfigFileProperties.getProperty(Configuration.Params.FED_SOE_PORT));
        }

        Image image = new Image(getClass().getResourceAsStream("/images/information-icon.png"), 20, 20, true, true);
        label1.setGraphic(new ImageView(image));
        label2.setGraphic(new ImageView(image));

        support.registerValidator(textField_1_0, Validator.createEmptyValidator("Обязательное поле"));

        supportCheckBox.registerValidator(textField1_1_0, Configuration.ipValidatorNotNull);
        supportCheckBox.registerValidator(textField1_3_0, Configuration.ipValidatorNotNull);
        supportCheckBox.registerValidator(textField1_1_1, Validator.createEmptyValidator("Обязательное поле"));
        supportCheckBox.registerValidator(textField1_3_1, Validator.createEmptyValidator("Обязательное поле"));
        supportCheckBox.registerValidator(textArea2, Validator.createEmptyValidator("Обязательное поле"));
        supportCheckBox.registerValidator(textArea3, Validator.createEmptyValidator("Обязательное поле"));
    }
}