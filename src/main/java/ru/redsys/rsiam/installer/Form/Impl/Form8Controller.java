package ru.redsys.rsiam.installer.Form.Impl;

import com.google.common.base.Strings;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.springframework.util.FileSystemUtils;
import ru.redsys.rsiam.installer.Utils.*;
import ru.redsys.rsiam.installer.WorkIndicatorDialogs.SinglePowerShellScript;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ru.redsys.rsiam.installer.Utils.PowerShellUtils.*;

public class Form8Controller extends AbstractFormController {

    public Button button1;
    public Button button4;

    public Text bodyLabel;

    private NewStage newStage = new NewStage();
    private SaveStage saveStage = new SaveStage();
    private Logger logger = Logger.getLogger(getClass().getName());

    private SinglePowerShellScript<String, String> wids = null;

    private boolean isError;
    private boolean isExist;

    @FXML
    public void handleOnNextButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Next - %s", event.getSource()));

        Configuration.buildConfigFileMap.put(Configuration.Params.STOREPASS, Configuration.getConfig().getProperty("cacertPassword"));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(Configuration.tempPath + File.separator + "variables.txt"), StandardCharsets.UTF_8))) {
            String property = reader.lines().collect(Collectors.joining("\n"));
            Properties variables = new Properties();
            variables.load(new StringReader(property.replace("\\", "\\\\")));

            for (String key : variables.stringPropertyNames()) {
                if (key.startsWith("\u001C")) {
                    key = key.substring(1);
                }
                Configuration.buildConfigFileMap.put(key, variables.getProperty(key));
            }
        } catch (Exception ex) {
            Alerts.exeptionDialog("Ошибка при открытии файла", "Не удалось прочитать файл с диска", ex.getMessage());
            logger.log(Level.SEVERE, "Exception.", ex.getMessage());
        }

        if (!Strings.isNullOrEmpty((String) Configuration.buildConfigFileMap.get(Configuration.Params.KERBEROS_KVN))) {
            try {
                FileSystemUtils.copyRecursively(new File(Configuration.tempPath + File.separator + Configuration.getConfig().getProperty("defaultKeystoreFolder")),
                    new File(Configuration.programPath + File.separator + Configuration.getConfig().getProperty("defaultName") + File.separator + Configuration.getConfig().getProperty("defaultFolder")));
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error while coping keystore. " + ex);
            }

            saveStage.setForm9(newStage.openNewStage("/fxml/Form9.fxml", saveStage.getForm9(), saveStage.getForm8()));

            saveStage.getForm9().show();
            saveStage.getForm8().hide();
        } else {
            bodyLabel.setText("Не удалось получить все параметры из ЕСК. Для повторения нажмите кнопку \"Повторить процесс\"");
            button4.setVisible(true);
            button4.setDisable(false);
            logger.log(Level.SEVERE, "Kerberos kvn is null");
            Alerts.exeptionDialog("Ошибка", "Не удалось получить все параметры из ЕСК", "Kerberos kvn is null");
        }
    }

    @FXML
    public void handleOnBackButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Back - %s", event.getSource()));

        saveStage.getForm7().setX(saveStage.getForm8().getX());
        saveStage.getForm7().setY(saveStage.getForm8().getY());
        saveStage.getForm7().show();

        saveStage.getForm8().hide();
    }

    @FXML
    public void handleOnRestartButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Restart - %s", event.getSource()));
        checkAdUser();
    }

    private void runProcess() {
        logger.log(Level.INFO, "Start AD process.");

        button4.setDisable(true);

        if (isExist) {
            Alerts.informationDialog(null, "Служебная УЗ \"RSauth\" уже существует в ЕСК. В случае продолжения, будет использоваться данная УЗ.");
        } else {
            if (isError) {
                return;
            } else {
                Alerts.informationDialog(null, "Служебная УЗ не найдена, создаем УЗ \"RSauth\" с необходимыми параметрами.");
            }
        }

        wids = new SinglePowerShellScript<>(saveStage.getForm8().getScene().getWindow(), "Подготовка служебной УЗ...");

        wids.addTaskEndNotification(result -> {
            wids = null;

            if (result != null) {
                logger.log(Level.INFO, () -> String.format("Create user in AD result - %s", result.getCommandOutput()));

                if (result.getCommandOutput().contains("[ERROR]") || result.isError()) {
                    bodyLabel.setText("Возникла ошибка при подготовке служебной УЗ в ЕСК. Для повторения нажмите кнопку \"Повторить процесс\"");
                    button4.setVisible(true);
                    button4.setDisable(false);
                    logger.log(Level.SEVERE, "Connection error", result.getCommandOutput());
                    Alerts.exeptionDialog("Ошибка", "Не удалось подготовить УЗ в ЕСК", result.getCommandOutput());
                } else {
                    Alerts.informationDialog(null, "Служебная УЗ \"RSauth\" успешно подготовлена.");

                    wids = new SinglePowerShellScript<>(saveStage.getForm8().getScene().getWindow(), "Создание сертификатов...");

                    wids.addTaskEndNotification(cert_result -> {
                        wids = null;
                        if (cert_result == null) {
                            logger.log(Level.SEVERE, "Certificate creation completed with errors");
                            Alerts.errorDialog("Ошибка", "Возникла ошибка при создании сертификатов");
                        } else {
                            if (result.isError()) {
                                logger.log(Level.SEVERE, "Certificate creation completed with errors", result);
                                Alerts.exeptionDialog(null, "Создание сертификатов завершено с ошибками", result.getCommandOutput());
                            }
                            logger.log(Level.INFO, () -> String.format("Create cert in AD result - %s", cert_result.getCommandOutput()));
                            Alerts.informationDialog(null, "Процесс подготовки УЗ \"RSauth\" и сертификатов завершен.");
                            bodyLabel.setText("Процесс подготовки служебной УЗ и сертификатов в ЕСК успешно завершен. Для продолжения нажмите кнопку \"Далее\"");
                            button1.setDisable(false);
                        }
                    });

                    String args = "-javapath" + " " + "\"" + Utils.javaHome + File.separator + "bin" + File.separator + "java.exe" + "\"" + " " +
                        "-keytoolpath" + " " + "\"" + Utils.javaHome + File.separator + "bin" + File.separator + "keytool.exe" + "\"" + " " +
                        "-storepass" + " " + "\"" + Configuration.getConfig().getProperty("cacertPassword") + "\"" + " " +
                        "-opensslpath" + " " + "\"" + Configuration.programPath + File.separator + Utils.defaultFolder + File.separator + Utils.openSSL + "\"" + " " +
                        "-CLUSTER_SSO" + " " + "\"" + Configuration.buildConfigFileMap.get(Configuration.Params.SSO_CLUSTER_FQDN) + "\"" + " " +
                        "-CLUSTER_APP" + " " + "\"" + Configuration.buildConfigFileMap.get(Configuration.Params.APP_CLUSTER_FQDN) + "\"" + " " +
                        "-CurrentWorkDirectory" + " " + "\"" + Configuration.tempPath + "\"";

                    wids.exec(ADMakeCerts, args);
                }
            }
        });

        String args = "-CLUSTER_SSO" + " " + "\"" + Configuration.buildConfigFileMap.get(Configuration.Params.SSO_CLUSTER_FQDN) + "\"" + " " +
            "-CLUSTER_APP" + " " + "\"" + Configuration.buildConfigFileMap.get(Configuration.Params.APP_CLUSTER_FQDN) + "\"" + " " +
            "-josso_password" + " " + "\"" + Configuration.getConfig().getProperty("jossoPassword") + "\"" + " " +
            "-CurrentWorkDirectory" + " " + "\"" + Configuration.tempPath + "\"";

        wids.exec(ADCreateUser, args);
    }

    private void checkAdUser() {
        logger.log(Level.INFO, "Check if exist RSauth user in AD");

        isError = false;
        isExist = false;

        Alerts.informationDialog(null, "Запущена проверка на наличие служебной УЗ \"RSauth\" в ЕСК.");

        wids = new SinglePowerShellScript<>(saveStage.getForm8().getScene().getWindow(), "Пожалуйста, подождите...");

        wids.addTaskEndNotification(result -> {
            wids = null;

            if (result != null) {
                logger.log(Level.INFO, () -> String.format("Check user in AD result - %s", result.getCommandOutput()));

                if (result.getCommandOutput().contains("[ERROR]")) {
                    Alerts.exeptionDialog("Ошибка!", "Не удалось соединиться с ЕСК. Устраните проблему и нажмите кнопку \"Повторить процесс\"", result.getCommandOutput());

                    bodyLabel.setText("Возникла ошибка при создании служебной УЗ в ЕСК. Для повторения нажмите кнопку \"Повторить процесс\"");

                    isError = true;
                    button4.setVisible(true);
                    button4.setDisable(false);
                } else {
                    List<String> resultList = powerShellResponseToList(result);
                    Configuration.jossoDn = resultList.get(resultList.size() - 1);
                    isExist = !"False".equals(Configuration.jossoDn) && !result.isError();
                }
            }

            runProcess();
        });

        wids.exec(ADCheckUser, "");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(this::checkAdUser);
    }
}