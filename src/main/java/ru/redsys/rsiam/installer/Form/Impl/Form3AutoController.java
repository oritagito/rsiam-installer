package ru.redsys.rsiam.installer.Form.Impl;

import com.google.common.base.Strings;
import com.profesorfalken.jpowershell.PowerShellResponse;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import ru.redsys.rsiam.installer.Utils.Alerts;
import ru.redsys.rsiam.installer.Utils.NewStage;
import ru.redsys.rsiam.installer.Utils.SaveStage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.redsys.rsiam.installer.Utils.PowerShellUtils.*;

public class Form3AutoController extends AbstractFormController {

    public TextField textField_1_1;
    public TextField textField_1_2;
    public PasswordField textPasswordField_1_3;
    public Button button1;
    public AnchorPane anchorPane;

    private NewStage newStage = new NewStage();
    private SaveStage saveStage = new SaveStage();
    private Logger logger = Logger.getLogger(getClass().getName());

    @FXML
    public void handleOnNextButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Next - %s", event.getSource()));

        saveStage.setForm3_auto_2(newStage.openNewStage("/fxml/Form3Auto_2.fxml", saveStage.getForm3_auto_2(), saveStage.getForm3_auto()));

        saveStage.getForm3_auto_2().show();
        saveStage.getForm3_auto().hide();
    }

    @FXML
    public void handleOnBackButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Back - %s", event.getSource()));

        saveStage.getForm2().setX(saveStage.getForm3_auto().getX());
        saveStage.getForm2().setY(saveStage.getForm3_auto().getY());
        saveStage.getForm2().show();

        saveStage.getForm3_auto().hide();
    }

    @FXML
    private void handleOnTestConnectionButtonAction(ActionEvent event) {
        saveStage.getForm3_auto().getScene().setCursor(Cursor.WAIT);

        server = Strings.isNullOrEmpty(textField_1_1.getText()) ? "" : textField_1_1.getText().trim();
        user = Strings.isNullOrEmpty(textField_1_2.getText()) ? "" : textField_1_2.getText().trim();
        password = Strings.isNullOrEmpty(textPasswordField_1_3.getText()) ? "" : textPasswordField_1_3.getText();

        Task<PowerShellResponse> task = new Task<PowerShellResponse>() {
            @Override
            protected PowerShellResponse call() {
                return getConnection();
            }
        };

        task.setOnSucceeded(e -> {
            saveStage.getForm3_auto().getScene().setCursor(Cursor.DEFAULT);

            if (task.getValue() == null) {
                logger.log(Level.SEVERE, "Connection error");
                Alerts.errorDialog("Ошибка при установке соединения", "Не удалось установить соединение с введенными параметрами");
                return;
            }

            PowerShellResponse result = task.getValue();

            if (result.getCommandOutput().contains("True") && !result.isError()) {
                Alerts.informationDialog(null, "Соединение установлено успешно!");
                button1.setDisable(false);
            } else {
                logger.log(Level.SEVERE, "Connection error", result);
                Alerts.exeptionDialog("Ошибка при установке соединения", "Не удалось установить соединение", result.getCommandOutput());
            }
            logger.log(Level.INFO, () -> String.format("Test connection result - %s", result.getCommandOutput()));
        });

        task.setOnFailed(e -> {
            saveStage.getForm3_auto().getScene().setCursor(Cursor.DEFAULT);

            logger.log(Level.SEVERE, "Connection error");
            Alerts.errorDialog("Ошибка при установке соединения", "Не удалось установить соединение с введенными параметрами");
        });

        new Thread(task).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}