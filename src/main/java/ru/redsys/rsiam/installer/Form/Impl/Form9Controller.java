package ru.redsys.rsiam.installer.Form.Impl;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import com.google.common.net.InetAddresses;
import com.profesorfalken.jpowershell.PowerShellResponse;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import ru.redsys.rsiam.installer.Utils.Alerts;
import ru.redsys.rsiam.installer.Utils.Configuration;
import ru.redsys.rsiam.installer.Utils.NewStage;
import ru.redsys.rsiam.installer.Utils.SaveStage;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.redsys.rsiam.installer.Utils.PowerShellUtils.ADCreateDNSRecords;
import static ru.redsys.rsiam.installer.Utils.PowerShellUtils.executePowerShellScript;

public class Form9Controller extends AbstractFormController {

    public TextArea textArea;

    private NewStage newStage = new NewStage();
    private SaveStage saveStage = new SaveStage();
    private Logger logger = Logger.getLogger(getClass().getName());

    @FXML
    public void handleOnNextButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Next - %s", event.getSource()));

        String configPath = Configuration.programPath + File.separator + Configuration.getConfig().getProperty("defaultName") + File.separator + Configuration.getConfig().getProperty("defaultFolder") + File.separator + Configuration.buildConfigFileMap.get(Configuration.Params.REGION_CODE) + "_env.conf";
        try {
            File file = new File(configPath);
            CharSink sink = Files.asCharSink(file, Charsets.UTF_8);
            sink.writeLines(Configuration.getAllValuesFromMap(Configuration.buildConfigFileMapSorted));
        } catch (IOException ex) {
            Alerts.errorDialog("Ошибка при создании файла", "Не удалось создать файл на диске: " + configPath);
            logger.log(Level.SEVERE, "Failed to save file", ex.getMessage());
        }

        configPath = Configuration.programPath + File.separator + Configuration.getConfig().getProperty("defaultName") + File.separator + Configuration.getConfig().getProperty("defaultFolder") + File.separator + "ansible" + File.separator + "group_vars" + File.separator + "cluster";
        try (FileInputStream fileInputStream = new FileInputStream(configPath)) {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setSplitLines(false);
            options.setPrettyFlow(true);
            options.setLineBreak(DumperOptions.LineBreak.UNIX);

            Yaml yaml = new Yaml(options);

            Map<String, Object> loaded = yaml.load(fileInputStream);

            loaded.replace(Configuration.Params.PRIMARY_ETH, "${" + Configuration.Params.PRIMARY_ETH + "}");
            loaded.replace(Configuration.Params.SECONDARY_ETH, "${" + Configuration.Params.SECONDARY_ETH + "}");

            String clusterIp = (String) Configuration.buildConfigFileMap.get(Configuration.Params.APPLICATION_CLUSTER_VIP);
            loaded.replace(Configuration.Params.ROUTER_ID, clusterIp.substring(clusterIp.lastIndexOf('.') + 1));

            Configuration.domain = (String) Configuration.buildConfigFileMap.get(Configuration.Params.DOMAIN);

            if (Configuration.webgateTargetSystem != null) {
                loaded.replace(Configuration.Params.WEBGATE_TARGET_SYSTEMS, Configuration.webgateTargetSystem.get(Configuration.Params.WEBGATE_TARGET_SYSTEMS));
            }

            if (Configuration.webgateFilters != null) {
                loaded.replace(Configuration.Params.WEBGATE_FILTERS, Configuration.webgateFilters.get(Configuration.Params.WEBGATE_FILTERS));
            }

            Configuration.buildConfigFileMap.forEach((key, value) -> {
                if (key.equals(Configuration.Params.MAIL_TO)) {
                    loaded.replace(Configuration.Params.MAIL_TO, value);
                }
                if (key.equals(Configuration.Params.MAIL_PORT)) {
                    loaded.replace(Configuration.Params.MAIL_PORT, value);
                }
                if (key.equals(Configuration.Params.MAIL_HOST)) {
                    loaded.replace(Configuration.Params.MAIL_HOST, value);
                }
                if (key.equals(Configuration.Params.APPLICATION_CLUSTER_VIP)) {
                    loaded.replace(Configuration.Params.APPLICATION_CLUSTER_VIP, value);
                }
                if (key.equals(Configuration.Params.APP_CLUSTER_FQDN)) {
                    loaded.replace(Configuration.Params.APP_CLUSTER_FQDN, value + "." + Configuration.domain);
                }
                if (key.equals(Configuration.Params.SSO_CLUSTER_FQDN)) {
                    loaded.replace(Configuration.Params.SSO_CLUSTER_FQDN, value + "." + Configuration.domain);
                }

                if (key.equals(Configuration.Params.IP_APP_0_0)) {
                    loaded.replace(Configuration.Params.APP_1_IP, value);
                    loaded.replace(Configuration.Params.HA_1_IP, value);
                    loaded.replace(Configuration.Params.SSO_1_IP, value);
                }
                if (key.equals(Configuration.Params.IP_APP_1_0)) {
                    loaded.replace(Configuration.Params.APP_2_IP, value);
                    loaded.replace(Configuration.Params.HA_2_IP, value);
                    loaded.replace(Configuration.Params.SSO_2_IP, value);
                }
                if (key.equals(Configuration.Params.IP_APP_0_1)) {
                    loaded.replace(Configuration.Params.APP_1_INTER_IP, value);
                    loaded.replace(Configuration.Params.HA_1_INTER_IP, value);
                    loaded.replace(Configuration.Params.SSO_1_INTER_IP, value);
                }
                if (key.equals(Configuration.Params.IP_APP_1_1)) {
                    loaded.replace(Configuration.Params.APP_2_INTER_IP, value);
                    loaded.replace(Configuration.Params.HA_2_INTER_IP, value);
                    loaded.replace(Configuration.Params.SSO_2_INTER_IP, value);
                }

                if (key.equals(Configuration.Params.HOSTNAME_APP_0_0)) {
                    loaded.replace(Configuration.Params.APP_1_FQDN, value + "." + Configuration.domain);
                    loaded.replace(Configuration.Params.APP_1_SHORT_FQDN, value);
                    loaded.replace(Configuration.Params.APP_1_INTER_FQDN, value + ".local");
                    loaded.replace(Configuration.Params.HA_1_FQDN, value + "." + Configuration.domain);
                    loaded.replace(Configuration.Params.HA_1_SHORT_FQDN, value);
                    loaded.replace(Configuration.Params.HA_1_INTER_FQDN, value + ".local");
                    loaded.replace(Configuration.Params.SSO_1_FQDN, value + "." + Configuration.domain);
                    loaded.replace(Configuration.Params.SSO_1_SHORT_FQDN, value);
                    loaded.replace(Configuration.Params.SSO_1_INTER_FQDN, value + ".local");
                }
                if (key.equals(Configuration.Params.HOSTNAME_APP_1_0)) {
                    loaded.replace(Configuration.Params.APP_2_FQDN, value + "." + Configuration.domain);
                    loaded.replace(Configuration.Params.APP_2_SHORT_FQDN, value);
                    loaded.replace(Configuration.Params.APP_2_INTER_FQDN, value + ".local");
                    loaded.replace(Configuration.Params.HA_2_FQDN, value + "." + Configuration.domain);
                    loaded.replace(Configuration.Params.HA_2_SHORT_FQDN, value);
                    loaded.replace(Configuration.Params.HA_2_INTER_FQDN, value + ".local");
                    loaded.replace(Configuration.Params.SSO_2_FQDN, value + "." + Configuration.domain);
                    loaded.replace(Configuration.Params.SSO_2_SHORT_FQDN, value);
                    loaded.replace(Configuration.Params.SSO_2_INTER_FQDN, value + ".local");
                }

                if (key.equals(Configuration.Params.IP_DB_0_0)) {
                    loaded.replace(Configuration.Params.DATABASE_IP, value);
                }
                if (key.equals(Configuration.Params.HOSTNAME_DB_0_0)) {
                    loaded.replace(Configuration.Params.DATABASE_FQDN, value + "." + Configuration.domain);
                }

                if (key.equals(Configuration.Params.JOSSO_WIN_DOMAIN)) {
                    loaded.replace(Configuration.Params.JOSSO_WIN_DOMAIN, value);
                }
                if (key.equals(Configuration.Params.JOSSO_DC_NAME)) {
                    loaded.replace(Configuration.Params.JOSSO_DC_NAME, value);
                }
                if (key.equals(Configuration.Params.KERBEROS_KVN)) {
                    loaded.replace(Configuration.Params.KERBEROS_KVN, value);
                }
                if (key.equals(Configuration.Params.KERBEROS_DC1_IP)) {
                    loaded.replace(Configuration.Params.KERBEROS_DC1_IP, value);
                }
                if (key.equals(Configuration.Params.KERBEROS_DC2_IP)) {
                    loaded.replace(Configuration.Params.KERBEROS_DC2_IP, value);
                }
                if (key.equals(Configuration.Params.JOSSO_PROVIDER_URL)) {
                    loaded.replace(Configuration.Params.JOSSO_PROVIDER_URL, value);
                }
                if (key.equals(Configuration.Params.JOSSO_SECURITY_PRINCIPAL)) {
                    loaded.replace(Configuration.Params.JOSSO_SECURITY_PRINCIPAL, value);
                }
                if (key.equals(Configuration.Params.JOSSO_SECURITY_CREDENTIAL)) {
                    loaded.replace(Configuration.Params.JOSSO_SECURITY_CREDENTIAL, value);
                }
                if (key.equals(Configuration.Params.JOSSO_USER_DN)) {
                    loaded.replace(Configuration.Params.JOSSO_USER_DN, value);
                }
                if (key.equals(Configuration.Params.JOSSO_ROLES_DN)) {
                    loaded.replace(Configuration.Params.JOSSO_ROLES_DN, value);
                }

                if (key.equals(Configuration.Params.OIM_DB_IP)) {
                    loaded.replace(Configuration.Params.OIM_DB_IP, value);
                }
                if (key.equals(Configuration.Params.OIM_DB_PORT)) {
                    loaded.replace(Configuration.Params.OIM_DB_PORT, value);
                }
                if (key.equals(Configuration.Params.OIM_DB_NAME)) {
                    loaded.replace(Configuration.Params.OIM_DB_NAME, value);
                }
                if (key.equals(Configuration.Params.OES_DB_PORT)) {
                    loaded.replace(Configuration.Params.OES_DB_PORT, value);
                }
                if (key.equals(Configuration.Params.OES_DB_NAME)) {
                    loaded.replace(Configuration.Params.OES_DB_NAME, value);
                }
                if (key.equals(Configuration.Params.OES_DB_PASS)) {
                    loaded.replace(Configuration.Params.OES_DB_PASS, value);
                }
                if (key.equals(Configuration.Params.OES_DB_USER)) {
                    loaded.replace(Configuration.Params.OES_DB_USER, value);
                }
                if (key.equals(Configuration.Params.OIM_DB_USER)) {
                    loaded.replace(Configuration.Params.OIM_DB_USER, value);
                }
                if (key.equals(Configuration.Params.OIM_DB_PASS)) {
                    loaded.replace(Configuration.Params.OIM_DB_PASS, value);
                }
                if (key.equals(Configuration.Params.OIM_DB_FULL_FQDN)) {
                    loaded.replace(Configuration.Params.OIM_DB_FULL_FQDN, value + "." + Configuration.domain);
                }
                if (key.equals(Configuration.Params.OIM_DB_SHORT_FQDN)) {
                    loaded.replace(Configuration.Params.OIM_DB_SHORT_FQDN, value);
                }
                if (key.equals(Configuration.Params.OIM_APP_IP)) {
                    loaded.replace(Configuration.Params.OIM_APP_IP, value);
                }
                if (key.equals(Configuration.Params.OIM_APP_FQDN)) {
                    loaded.replace(Configuration.Params.OIM_APP_FQDN, value + "." + Configuration.domain);
                }
                if (key.equals(Configuration.Params.OIM_APP_PORT)) {
                    loaded.replace(Configuration.Params.OIM_APP_PORT, value);
                }
                if (key.equals(Configuration.Params.OIM_APP_USER)) {
                    loaded.replace(Configuration.Params.OIM_APP_USER, value);
                }
                if (key.equals(Configuration.Params.OIM_APP_PASS)) {
                    loaded.replace(Configuration.Params.OIM_APP_PASS, value);
                }
                if (key.equals(Configuration.Params.SOA_DB_USER)) {
                    loaded.replace(Configuration.Params.SOA_DB_USER, value);
                }

                if (key.equals(Configuration.Params.APP_RKASV_FQDN)) {
                    if (InetAddresses.isInetAddress((String) value)) {
                        loaded.replace(Configuration.Params.APP_RKASV_FQDN, value);
                    } else {
                        loaded.replace(Configuration.Params.APP_RKASV_FQDN, value + "." + Configuration.domain);
                    }
                }

                if (key.equals(Configuration.Params.REGION_CODE)) {
                    loaded.replace(Configuration.Params.REGION_CODE, value.toString().length() > 1 ? value.toString().substring(1) : value);
                    loaded.replace(Configuration.Params.REGION_CODE_BIUD, value.toString().replaceFirst("^0+(?!$)", ""));
                }

                if (key.equals(Configuration.Params.LOTUS_CERTIFIERID_FILE)) {
                    loaded.replace(Configuration.Params.LOTUS_CERTIFIERID_FILE, value);
                }

                if (key.equals(Configuration.Params.LOTUS_MAIL_DOMAIN)) {
                    loaded.replace(Configuration.Params.LOTUS_MAIL_DOMAIN, value);
                }

                if (key.equals(Configuration.Params.LOTUS_INTERNET_ADDRESS_DOMAIN)) {
                    loaded.replace(Configuration.Params.LOTUS_INTERNET_ADDRESS_DOMAIN, value);
                }

                if (key.equals(Configuration.Params.LOTUS_ADMIN_NAME)) {
                    loaded.replace(Configuration.Params.LOTUS_ADMIN_NAME, value);
                }

                if (key.equals(Configuration.Params.LOTUS_DEFAULT_DIRECTORY_FOR_ID_FILES)) {
                    loaded.replace(Configuration.Params.LOTUS_DEFAULT_DIRECTORY_FOR_ID_FILES, value);
                }

                if (key.equals(Configuration.Params.FED_SPE_VIP_FQDN)) {
                    loaded.replace(Configuration.Params.FED_SPE_VIP_FQDN, value);
                }
                if (key.equals(Configuration.Params.FED_SPE_PORT)) {
                    loaded.replace(Configuration.Params.FED_SPE_PORT, value);
                }
                if (key.equals(Configuration.Params.FED_SOE_VIP_FQDN)) {
                    loaded.replace(Configuration.Params.FED_SOE_VIP_FQDN, value);
                }
                if (key.equals(Configuration.Params.FED_SOE_PORT)) {
                    loaded.replace(Configuration.Params.FED_SOE_PORT, value);
                }

                if (key.equals(Configuration.Params.KA_SUBNET_PREFIX)) {
                    loaded.replace(Configuration.Params.KA_SUBNET_PREFIX, value);
                }
            });

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(configPath), StandardCharsets.UTF_8)) {
                yaml.dump(loaded, writer);
            }

            // TODO Сохраняет null как строку
            File file = new File(configPath);
            List<String> lines = Files.readLines(file, Charsets.UTF_8);
            try (BufferedWriter writer = Files.newWriter(file, Charsets.UTF_8)) {
                for (String line : lines) {
                    writer.write(line.replaceAll(" null", "").replaceAll("\\$\\{" + Configuration.Params.APP_RKASV_FQDN + "}", Configuration.buildConfigFileMap.get(Configuration.Params.APP_RKASV_FQDN).toString()));
                    writer.newLine();
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Exception", ex.getMessage());
            }

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to write file. ", ex.getMessage());
        }

        Task<PowerShellResponse> task = new Task<PowerShellResponse>() {
            @Override
            protected PowerShellResponse call() {
                String args = "-DB_name" + " " + "\"" + Configuration.buildConfigFileMap.get(Configuration.Params.HOSTNAME_DB_0_0) + "\"" + " " +
                    "-DB_ip" + " " + "\"" + Configuration.buildConfigFileMap.get(Configuration.Params.IP_DB_0_0) + "\"" + " " +
                    "-APP1_name" + " " + "\"" + Configuration.buildConfigFileMap.get(Configuration.Params.HOSTNAME_APP_0_0) + "\"" + " " +
                    "-APP1_ip" + " " + "\"" + Configuration.buildConfigFileMap.get(Configuration.Params.IP_APP_0_0) + "\"" + " " +
                    "-APP2_name" + " " + "\"" + Configuration.buildConfigFileMap.get(Configuration.Params.HOSTNAME_APP_1_0) + "\"" + " " +
                    "-APP2_ip" + " " + "\"" + Configuration.buildConfigFileMap.get(Configuration.Params.IP_APP_1_0) + "\"" + " " +
                    "-CLUSTER_SSO_name" + " " + "\"" + Configuration.buildConfigFileMap.get(Configuration.Params.SSO_CLUSTER_FQDN) + "\"" + " " +
                    "-CLUSTER_SSO_ip" + " " + "\"" + Configuration.buildConfigFileMap.get(Configuration.Params.APPLICATION_CLUSTER_VIP) + "\"";

                logger.log(Level.INFO, args);

                return executePowerShellScript(ADCreateDNSRecords, args);
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue() == null) {
                logger.log(Level.SEVERE, "Fail to add DNS records");
            }

            logger.log(Level.INFO, task.getValue().getCommandOutput());

            if (task.getValue().isError()) {
                logger.log(Level.SEVERE, "Fail to add DNS records", task.getValue());
                Alerts.exeptionDialog(null, "Не добавить DNS записи в ЕСК", task.getValue().getCommandOutput());
                return;
            }
        });

        new Thread(task).start();

        saveStage.setForm10(newStage.openNewStage("/fxml/Form10.fxml", saveStage.getForm10(), saveStage.getForm9()));

        saveStage.getForm10().show();
        saveStage.getForm9().hide();
    }

    @FXML
    public void handleOnBackButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Back - %s", event.getSource()));

        saveStage.getForm8().setX(saveStage.getForm9().getX());
        saveStage.getForm8().setY(saveStage.getForm9().getY());
        saveStage.getForm8().show();

        saveStage.getForm9().hide();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Configuration.buildConfigFileMapSorted = new TreeMap<>(Configuration.buildConfigFileMap);

        Configuration.buildConfigFileMapSorted.forEach((key, value) -> {
            if (!key.equals(Configuration.Params.OIM_APP_PASS) &&
                !key.equals(Configuration.Params.OIM_DB_PASS) &&
                !key.equals(Configuration.Params.OES_DB_PASS) &&
                !key.equals(Configuration.Params.JOSSO_SECURITY_CREDENTIAL) &&
                !key.equals(Configuration.Params.STOREPASS)) {
                textArea.appendText(key + "=" + value);
            } else {
                textArea.appendText(key + "=******");
            }
            textArea.appendText(System.lineSeparator());
        });
    }
}