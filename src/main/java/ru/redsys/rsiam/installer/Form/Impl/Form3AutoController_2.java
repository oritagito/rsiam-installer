package ru.redsys.rsiam.installer.Form.Impl;

import com.google.common.base.Strings;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import ru.redsys.rsiam.installer.Utils.Alerts;
import ru.redsys.rsiam.installer.Utils.Configuration;
import ru.redsys.rsiam.installer.Utils.NewStage;
import ru.redsys.rsiam.installer.Utils.SaveStage;
import ru.redsys.rsiam.installer.WorkIndicatorDialogs.MultiPowerShellScripts;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.redsys.rsiam.installer.Utils.PowerShellUtils.*;

// TODO Как-нибудь переписать данное
public class Form3AutoController_2 extends AbstractFormController {

    public ComboBox<String> comboBox_1_1;
    public ComboBox<String> comboBox_1_2;
    public ComboBox<String> comboBox_1_3;
    public ComboBox<String> comboBox_1_4;
    public ComboBox<String> comboBox_1_5;
    public ComboBox<String> comboBox_1_6;
    public ComboBox<String> comboBox_1_7;
    public ComboBox<String> comboBox_1_8;
    public ComboBox<String> comboBox_1_9;

    public ComboBox<String> comboBox2_1_1;
    public ComboBox<String> comboBox2_1_2;
    public ComboBox<String> comboBox2_1_3;
    public ComboBox<String> comboBox2_1_4;
    public ComboBox<String> comboBox2_1_5;
    public ComboBox<String> comboBox2_1_6;
    public ComboBox<String> comboBox2_1_7;
    public ComboBox<String> comboBox2_1_8;
    public ComboBox<String> comboBox2_1_9;

    public ComboBox<String> comboBox3_1_1;
    public ComboBox<String> comboBox3_1_2;
    public ComboBox<String> comboBox3_1_3;
    public ComboBox<String> comboBox3_1_4;
    public ComboBox<String> comboBox3_1_5;
    public ComboBox<String> comboBox3_1_6;
    public ComboBox<String> comboBox3_1_7;
    public ComboBox<String> comboBox3_1_8;
    public ComboBox<String> comboBox3_1_9;

    public CheckBox checkBox;
    public CheckBox checkBox_2;

    public CheckBox checkBox2;
    public CheckBox checkBox2_2;

    public CheckBox checkBox3;
    public CheckBox checkBox3_2;

    public TextField textField_3_1;
    public TextField textField_3_2;
    public TextField textField_3_3;
    public TextField textField_3_4;
    public TextField textField_3_6;
    public TextField textField_3_7;

    public TextField textField2_3_1;
    public TextField textField2_3_2;
    public TextField textField2_3_3;
    public TextField textField2_3_4;
    public TextField textField2_3_6;
    public TextField textField2_3_7;

    public TextField textField3_3_1;
    public TextField textField3_3_2;
    public TextField textField3_3_3;
    public TextField textField3_3_4;
    public TextField textField3_3_6;
    public TextField textField3_3_7;

    public Tab tab;
    public Tab tab2;
    public Tab tab3;

    public Button button1;
    public Button button2;

    public TabPane tabPane;

    private MultiPowerShellScripts wid = null;

    private NewStage newStage = new NewStage();
    private SaveStage saveStage = new SaveStage();
    private Logger logger = Logger.getLogger(getClass().getName());

    @FXML
    public void handleOnNextButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Next - %s", event.getSource()));

        saveStage.setForm3_manual(newStage.openNewStage("/fxml/Form3Manual.fxml", saveStage.getForm3_manual(), saveStage.getForm2()));

        saveStage.getForm3_manual().show();
        saveStage.getForm3_auto_2().hide();
    }

    @FXML
    private void handleOnCreateButtonAction(ActionEvent event) {
        logger.log(Level.INFO, "Create button. " + event.getSource());

        if (onValidate(comboBox_1_1, comboBox_1_2, comboBox_1_3, comboBox_1_4, comboBox_1_5, comboBox_1_6, comboBox_1_7, comboBox_1_8, textField_3_1, textField_3_2, textField_3_3, textField_3_4)) {

            saveStage.getForm3_auto_2().getScene().setCursor(Cursor.WAIT);

            Task<List<String>> task = new Task<List<String>>() {
                @Override
                protected List<String> call() {

                    String args = argsBuilderForPowerShellScript(comboBox_1_1, comboBox_1_2, comboBox_1_4, comboBox_1_7,
                        comboBox_1_3, comboBox_1_5, comboBox_1_6, comboBox_1_9, comboBox_1_8, textField_3_1,
                        textField_3_2, textField_3_3, textField_3_4, textField_3_6, textField_3_7);

                    return powerShellResponseToList(executePowerShellScript(VMNewVM, args));
                }
            };

            task.setOnSucceeded(e -> {
                if (task.getValue().size() != 0) {
                    if (Configuration.macValidatorNotNull(task.getValue().get(task.getValue().size() - 1))) {
                        Configuration.networkMacAddress.remove(1);
                        Configuration.networkMacAddress.put(1, task.getValue());

                        Configuration.isTwoNetworksDb = checkBox.isSelected();

                        Alerts.informationDialog("Виртуальная машина БД создана успешно!", "Теперь необходимо заполнить параметры для создания виртуальной машины сервера приложений");
                        tab2.setDisable(false);
                        tab.setDisable(true);

                        tabPane.getSelectionModel().select(tab2);

                        if (checkBox_2.isSelected()) {
                            comboBox2_1_5.getItems().addAll(powerShellResponseToList(executePowerShellScript(VMGetFolder, "")));
                        } else {
                            comboBox2_1_1.setValue(comboBox_1_1.getValue());
                            comboBox2_1_2.setValue(comboBox_1_2.getValue());
                            comboBox2_1_3.setValue(comboBox_1_3.getValue());
                            comboBox2_1_4.setValue(comboBox_1_4.getValue());
                            comboBox2_1_5.setValue(comboBox_1_5.getValue());
                            comboBox2_1_6.setValue(comboBox_1_6.getValue());
                            comboBox2_1_7.setValue(comboBox_1_7.getValue());
                            comboBox2_1_8.setValue(comboBox_1_8.getValue());
                        }
                    } else {
                        StringBuilder message = new StringBuilder();
                        for (String line : task.getValue()) {
                            message.append(line).append("\n");
                        }
                        Alerts.exeptionDialog("Ошибка!", "Не удалось создать виртуальную машину с заданными параметрами.", message.toString());
                    }
                } else {
                    Alerts.errorDialog("Ошибка!", "Не удалось создать виртуальную машину с заданными параметрами.");
                }

                saveStage.getForm3_auto_2().getScene().setCursor(Cursor.DEFAULT);
            });

            new Thread(task).start();
        }
    }

    @FXML
    private void handleOnCreateButton2Action(ActionEvent event) {
        logger.log(Level.INFO, "Create button2. " + event.getSource());

        if (onValidate(comboBox2_1_1, comboBox2_1_2, comboBox2_1_3, comboBox2_1_4, comboBox2_1_5, comboBox2_1_6, comboBox2_1_7, comboBox2_1_8, textField2_3_1, textField2_3_2, textField2_3_3, textField2_3_4)) {

            saveStage.getForm3_auto_2().getScene().setCursor(Cursor.WAIT);

            Task<List<String>> task = new Task<List<String>>() {
                @Override
                protected List<String> call() {

                    String args = argsBuilderForPowerShellScript(comboBox2_1_1, comboBox2_1_2, comboBox2_1_4, comboBox2_1_7,
                        comboBox2_1_3, comboBox2_1_5, comboBox2_1_6, comboBox2_1_9, comboBox2_1_8, textField2_3_1,
                        textField2_3_2, textField2_3_3, textField2_3_4, textField2_3_6, textField2_3_7);

                    return powerShellResponseToList(executePowerShellScript(VMNewVM, args));
                }
            };

            task.setOnSucceeded(e -> {
                if (task.getValue().size() != 0) {
                    if (Configuration.macValidatorNotNull(task.getValue().get(task.getValue().size() - 1))) {
                        Configuration.networkMacAddress.remove(2);
                        Configuration.networkMacAddress.put(2, task.getValue());

                        Configuration.isTwoNetworksApp1 = checkBox2.isSelected();

                        Alerts.informationDialog("Виртуальная машина сервера приложения 1 создана успешно!", "Теперь необходимо заполнить параметры для создания виртуальной машины сервера приложений 2");
                        tab3.setDisable(false);
                        tab2.setDisable(true);

                        tabPane.getSelectionModel().select(tab3);

                        if (checkBox2_2.isSelected()) {
                            comboBox3_1_5.getItems().addAll(powerShellResponseToList(executePowerShellScript(VMGetFolder, "")));
                        } else {
                            comboBox3_1_1.setValue(comboBox2_1_1.getValue());
                            comboBox3_1_2.setValue(comboBox2_1_2.getValue());
                            comboBox3_1_3.setValue(comboBox2_1_3.getValue());
                            comboBox3_1_4.setValue(comboBox2_1_4.getValue());
                            comboBox3_1_5.setValue(comboBox2_1_5.getValue());
                            comboBox3_1_6.setValue(comboBox2_1_6.getValue());
                            comboBox3_1_7.setValue(comboBox2_1_7.getValue());
                            comboBox3_1_8.setValue(comboBox2_1_8.getValue());
                        }
                    } else {
                        StringBuilder message = new StringBuilder();
                        for (String line : task.getValue()) {
                            message.append(line).append("\n");
                        }
                        Alerts.exeptionDialog("Ошибка!", "Не удалось создать виртуальную машину с заданными параметрами.", message.toString());
                    }
                } else {
                    Alerts.errorDialog("Ошибка!", "Не удалось создать виртуальную машину с заданными параметрами.");
                }

                saveStage.getForm3_auto_2().getScene().setCursor(Cursor.DEFAULT);
            });

            new Thread(task).start();
        }
    }

    @FXML
    private void handleOnCreateButton3Action(ActionEvent event) {
        logger.log(Level.INFO, "Create button3. " + event.getSource());

        if (onValidate(comboBox3_1_1, comboBox3_1_2, comboBox3_1_3, comboBox3_1_4, comboBox3_1_5, comboBox3_1_6, comboBox3_1_7, comboBox3_1_8, textField3_3_1, textField3_3_2, textField3_3_3, textField3_3_4)) {

            saveStage.getForm3_auto_2().getScene().setCursor(Cursor.WAIT);

            Task<List<String>> task = new Task<List<String>>() {
                @Override
                protected List<String> call() {

                    String args = argsBuilderForPowerShellScript(comboBox3_1_1, comboBox3_1_2, comboBox3_1_4, comboBox3_1_7,
                        comboBox3_1_3, comboBox3_1_5, comboBox3_1_6, comboBox3_1_9, comboBox3_1_8, textField3_3_1,
                        textField3_3_2, textField3_3_3, textField3_3_4, textField3_3_6, textField3_3_7);

                    return powerShellResponseToList(executePowerShellScript(VMNewVM, args));
                }
            };

            task.setOnSucceeded(e -> {
                if (task.getValue().size() != 0) {
                    if (Configuration.macValidatorNotNull(task.getValue().get(task.getValue().size() - 1))) {
                        Configuration.networkMacAddress.remove(3);
                        Configuration.networkMacAddress.put(3, task.getValue());

                        Configuration.isTwoNetworksApp2 = checkBox3.isSelected();

                        Alerts.informationDialog("Виртуальная машина сервера приложения 2 создана успешно!", "Для продолжения установки нажмите кнопку \"Далее\"");
                        button1.setDisable(false);
                        tab3.setDisable(true);
                    } else {
                        StringBuilder message = new StringBuilder();
                        for (String line : task.getValue()) {
                            message.append(line).append("\n");
                        }
                        Alerts.exeptionDialog("Ошибка!", "Не удалось создать виртуальную машину с заданными параметрами.", message.toString());
                    }
                } else {
                    Alerts.errorDialog("Ошибка!", "Не удалось создать виртуальную машину с заданными параметрами.");
                }

                saveStage.getForm3_auto_2().getScene().setCursor(Cursor.DEFAULT);
            });

            new Thread(task).start();
        }
    }

    @FXML
    private void handleOnCheckBoxAction(ActionEvent event) {
        if (checkBox.isSelected()) {
            comboBox_1_9.setDisable(false);
            logger.log(Level.INFO, "checkBox is selected. " + event.getSource());
        } else {
            comboBox_1_9.setDisable(true);
        }
    }

    @FXML
    private void handleOnCheckBox_2Action(ActionEvent event) {
        if (!checkBox_2.isSelected()) {
            textField_3_6.setDisable(true);
            textField_3_7.setDisable(true);
            logger.log(Level.INFO, "checkBox_2 is selected. " + event.getSource());
        } else {
            textField_3_6.setDisable(false);
            textField_3_7.setDisable(false);
        }
    }

    @FXML
    private void handleOnCheckBox2Action(ActionEvent event) {
        if (checkBox2.isSelected()) {
            comboBox2_1_9.setDisable(false);
            logger.log(Level.INFO, "checkBox2 is selected. " + event.getSource());
        } else {
            comboBox2_1_9.setDisable(true);
        }
    }

    @FXML
    private void handleOnCheckBox2_2Action(ActionEvent event) {
        if (!checkBox2_2.isSelected()) {
            textField2_3_6.setDisable(true);
            textField2_3_7.setDisable(true);
            logger.log(Level.INFO, "checkBox2_2 is selected. " + event.getSource());
        } else {
            textField2_3_6.setDisable(false);
            textField2_3_7.setDisable(false);
        }
    }

    @FXML
    private void handleOnCheckBox3Action(ActionEvent event) {
        if (checkBox3.isSelected()) {
            comboBox3_1_9.setDisable(false);
            logger.log(Level.INFO, "checkBox3 is selected. " + event.getSource());
        } else {
            comboBox3_1_9.setDisable(true);
        }
    }

    @FXML
    private void handleOnCheckBox3_2Action(ActionEvent event) {
        if (!checkBox3_2.isSelected()) {
            textField3_3_6.setDisable(true);
            textField3_3_7.setDisable(true);
            logger.log(Level.INFO, "checkBox3_2 is selected. " + event.getSource());
        } else {
            textField3_3_6.setDisable(false);
            textField3_3_7.setDisable(false);
        }
    }

    @FXML
    private void handleOnComboBox_1_1TextChanged(ActionEvent event) {
        logger.log(Level.INFO, "comboBox_1_1 text changed. " + event.getSource());
        isComboBoxTextChangedExecutePowerShellScript(comboBox_1_2, comboBox_1_1, VMGetCluster);
    }

    @FXML
    public void handleOnComboBox2_1_1TextChanged(ActionEvent event) {
        logger.log(Level.INFO, "comboBox2_1_1 text changed. " + event.getSource());
        isComboBoxTextChangedExecutePowerShellScript(comboBox2_1_2, comboBox2_1_1, VMGetCluster);
    }

    @FXML
    public void handleOnComboBox3_1_1TextChanged(ActionEvent event) {
        logger.log(Level.INFO, "comboBox3_1_1 text changed. " + event.getSource());
        isComboBoxTextChangedExecutePowerShellScript(comboBox3_1_2, comboBox3_1_1, VMGetCluster);
    }

    @FXML
    private void handleOnComboBox_1_2TextChanged(ActionEvent event) {
        logger.log(Level.INFO, "comboBox_1_2 text changed. " + event.getSource());
        isComboBoxTextChangedExecutePowerShellScript(comboBox_1_3, comboBox_1_2, VMGetResourcePool);
    }

    @FXML
    private void handleOnComboBox2_1_2TextChanged(ActionEvent event) {
        logger.log(Level.INFO, "comboBox2_1_2 text changed. " + event.getSource());
        isComboBoxTextChangedExecutePowerShellScript(comboBox2_1_3, comboBox2_1_2, VMGetResourcePool);
    }

    @FXML
    private void handleOnComboBox3_1_2TextChanged(ActionEvent event) {
        logger.log(Level.INFO, "comboBox3_1_2 text changed. " + event.getSource());
        isComboBoxTextChangedExecutePowerShellScript(comboBox3_1_3, comboBox3_1_2, VMGetResourcePool);
    }

    @FXML
    private void handleOnComboBox_1_3TextChanged(ActionEvent event) {
        logger.log(Level.INFO, "comboBox_1_3 text changed. " + event.getSource());
        isComboBoxTextChangedEnableComboBox(comboBox_1_4, comboBox_1_5, comboBox_1_6, comboBox_1_7, comboBox_1_8);
    }

    @FXML
    private void handleOnComboBox2_1_3TextChanged(ActionEvent event) {
        logger.log(Level.INFO, "comboBox2_1_3 text changed. " + event.getSource());
        isComboBoxTextChangedEnableComboBox(comboBox2_1_4, comboBox2_1_5, comboBox2_1_6, comboBox2_1_7, comboBox2_1_8);
    }

    @FXML
    private void handleOnComboBox3_1_3TextChanged(ActionEvent event) {
        logger.log(Level.INFO, "comboBox3_1_3 text changed. " + event.getSource());
        isComboBoxTextChangedEnableComboBox(comboBox3_1_4, comboBox3_1_5, comboBox3_1_6, comboBox3_1_7, comboBox3_1_8);
    }

    @FXML
    private void handleOnDigitKeyTyped(KeyEvent event) {
        String key = event.getCharacter();
        if (Configuration.isInteger(key)) {
            event.consume();
        }
    }

    private void isComboBoxTextChangedExecutePowerShellScript(ComboBox<String> cb1, ComboBox<String> cb2, String script) {
        if (cb1.isDisable()) {
            cb1.setDisable(false);
        }

        cb1.getItems().clear();
        cb1.getItems().addAll(powerShellResponseToList(executePowerShellScript(script, cb2.getValue())));
    }

    private void isComboBoxTextChangedEnableComboBox(ComboBox<String> cb1, ComboBox<String> cb2, ComboBox<String> cb3, ComboBox<String> cb4, ComboBox<String> cb5) {
        if (cb1.isDisable()) {
            cb1.setDisable(false);
        }
        if (cb2.isDisable()) {
            cb2.setDisable(false);
        }
        if (cb3.isDisable()) {
            cb3.setDisable(false);
        }
        if (cb4.isDisable()) {
            cb4.setDisable(false);
        }
        if (cb5.isDisable()) {
            cb5.setDisable(false);
        }
    }

    private String argsBuilderForPowerShellScript(ComboBox<String> cb1, ComboBox<String> cb2, ComboBox<String> cb3, ComboBox<String> cb4, ComboBox<String> cb5, ComboBox<String> cb6, ComboBox<String> cb7, ComboBox<String> cb8, ComboBox<String> cb9, TextField tf1, TextField tf2, TextField tf3, TextField tf4, TextField tf5, TextField tf6) {
        StringBuilder argsBuilder = new StringBuilder();
        argsBuilder.append("-datacenter \"").append(cb1.getValue()).append("\"");
        argsBuilder.append(" -cluster \"").append(cb2.getValue()).append("\"");
        argsBuilder.append(" -datastore \"").append(cb3.getValue()).append("\"");
        argsBuilder.append(" -vmHost \"").append(cb4.getValue()).append("\"");
        argsBuilder.append(" -resourcePool \"").append(cb5.getValue().split("/")[1]).append("\"");
        argsBuilder.append(" -parentResourcePool \"").append(cb5.getValue().split("/")[0]).append("\"");
        argsBuilder.append(" -folderName \"").append(cb6.getValue().split("/")[1]).append("\"");
        argsBuilder.append(" -parentFolderName \"").append(cb6.getValue().split("/")[0]).append("\"");
        argsBuilder.append(" -guestId \"").append(cb7.getValue()).append("\"");
        if (cb8.isDisable()) {
            argsBuilder.append(" -networkName \"").append(cb9.getValue()).append("\"");
        } else {
            argsBuilder.append(" -networkName \"").append(cb9.getValue()).append(",").append(cb8.getValue()).append("\"");
        }
        argsBuilder.append(" -name \"").append(tf1.getText()).append("\"");
        argsBuilder.append(" -numCpu \"").append(tf2.getText()).append("\"");
        argsBuilder.append(" -diskGB \"").append(tf3.getText()).append("\"");
        argsBuilder.append(" -memoryGB \"").append(tf4.getText()).append("\"");
        argsBuilder.append(" -newResourcePool \"").append(tf5.isDisable() ? "" : tf5.getText()).append("\"");
        argsBuilder.append(" -newFolder \"").append(tf6.isDisable() ? "" : tf6.getText()).append("\"");

        return argsBuilder.toString();
    }

    private boolean onValidate(ComboBox<String> cb1, ComboBox<String> cb2, ComboBox<String> cb3, ComboBox<String> cb4, ComboBox<String> cb5, ComboBox<String> cb6, ComboBox<String> cb7, ComboBox<String> cb8, TextField tf1, TextField tf2, TextField tf3, TextField tf4) {
        ValidationSupport support = new ValidationSupport();

        support.registerValidator(cb1, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(cb2, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(cb3, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(cb4, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(cb5, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(cb6, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(cb7, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(cb8, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(tf1, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(tf2, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(tf3, Validator.createEmptyValidator("Обязательное поле"));
        support.registerValidator(tf4, Validator.createEmptyValidator("Обязательное поле"));

        return onValidateResult(cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8, tf1, tf2, tf3, tf4);
    }

    private boolean onValidateResult(ComboBox<String> cb1, ComboBox<String> cb2, ComboBox<String> cb3, ComboBox<String> cb4, ComboBox<String> cb5, ComboBox<String> cb6, ComboBox<String> cb7, ComboBox<String> cb8, TextField tf1, TextField tf2, TextField tf3, TextField tf4) {
        boolean result = !Strings.isNullOrEmpty(cb1.getValue()) &&
            !Strings.isNullOrEmpty(cb2.getValue()) &&
            !Strings.isNullOrEmpty(cb3.getValue()) &&
            !Strings.isNullOrEmpty(cb4.getValue()) &&
            !Strings.isNullOrEmpty(cb5.getValue()) &&
            !Strings.isNullOrEmpty(cb6.getValue()) &&
            !Strings.isNullOrEmpty(cb7.getValue()) &&
            !Strings.isNullOrEmpty(cb8.getValue()) &&
            !Strings.isNullOrEmpty(tf1.getText()) &&
            !Strings.isNullOrEmpty(tf2.getText()) &&
            !Strings.isNullOrEmpty(tf3.getText()) &&
            !Strings.isNullOrEmpty(tf4.getText());

        logger.log(Level.INFO, "Validate status: " + result);
        return result;
    }

    private void loadingParameters() {
        wid = new MultiPowerShellScripts(saveStage.getForm3_auto_2().getScene().getWindow(), "Пожалуйста подождите, параметры загружаются...");
        wid.addTaskEndNotification(result -> {

            comboBox_1_1.getItems().addAll(result.get("datacenter"));
            comboBox2_1_1.getItems().addAll(result.get("datacenter"));
            comboBox3_1_1.getItems().addAll(result.get("datacenter"));

            comboBox_1_4.getItems().addAll(result.get("datastore"));
            comboBox2_1_4.getItems().addAll(result.get("datastore"));
            comboBox3_1_4.getItems().addAll(result.get("datastore"));

            comboBox_1_5.getItems().addAll(result.get("folder"));

            comboBox_1_6.getItems().addAll(result.get("guestOS"));
            comboBox2_1_6.getItems().addAll(result.get("guestOS"));
            comboBox3_1_6.getItems().addAll(result.get("guestOS"));

            comboBox_1_7.getItems().addAll(result.get("vmHost"));
            comboBox2_1_7.getItems().addAll(result.get("vmHost"));
            comboBox3_1_7.getItems().addAll(result.get("vmHost"));

            comboBox_1_8.getItems().addAll(result.get("vdPortgroup"));
            comboBox_1_9.getItems().addAll(result.get("vdPortgroup"));
            comboBox2_1_8.getItems().addAll(result.get("vdPortgroup"));
            comboBox2_1_9.getItems().addAll(result.get("vdPortgroup"));
            comboBox3_1_8.getItems().addAll(result.get("vdPortgroup"));
            comboBox3_1_9.getItems().addAll(result.get("vdPortgroup"));

            wid = null;
        });
        wid.exec();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(this::loadingParameters);
    }
}
