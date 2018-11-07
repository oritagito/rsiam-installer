package ru.redsys.rsiam.installer.Utils;

import com.google.common.base.Strings;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Configuration {
    private static final String CONFIG_FILENAME = "constants.properties";
    public static int numOfServers = 3;
    public static String typeOfInstall = "manual";
    public static Map<Integer, List<String>> networkMacAddress = new HashMap<>();
    public static String jossoDn;
    public static String domain;
    public static boolean isTwoNetworksDb;
    public static boolean isTwoNetworksApp1;
    public static boolean isTwoNetworksApp2;
    public static String programPath;
    public static String tempPath;
    public static Map<String, Object> webgateTargetSystem = null;
    public static Map<String, Object> webgateFilters = null;
    public static Map<String, Object> buildConfigFileMap = new HashMap<>();
    public static TreeMap<String, Object> buildConfigFileMapSorted;
    public static Properties openConfigFileProperties = new Properties();

    public static Validator<String> ipValidatorNotNull = (control, s) -> {
        s = Strings.isNullOrEmpty(s) ? "" : s;
        ValidationResult nullOrEmpty = ValidationResult.fromErrorIf(control, "Обязательное поле", Strings.isNullOrEmpty(s));
        ValidationResult inetAddress = ValidationResult.fromErrorIf(control, "Не корректный IP-адрес", !InetAddresses.isInetAddress(s));
        return ValidationResult.fromResults(nullOrEmpty, inetAddress);
    };

    public static Validator<String> ipValidator = (control, s) -> {
        if (Strings.isNullOrEmpty(s)) {
            return ValidationResult.fromResults();
        }
        ValidationResult inetAddress = ValidationResult.fromErrorIf(control, "Не корректный IP-адрес", !InetAddresses.isInetAddress(s));
        return ValidationResult.fromResults(inetAddress);
    };

    public static Validator<String> macValidatorNotNull = (control, s) -> {
        s = Strings.isNullOrEmpty(s) ? "" : s;
        ValidationResult nullOrEmpty = ValidationResult.fromErrorIf(control, "Обязательное поле", Strings.isNullOrEmpty(s));
        ValidationResult macAddress = ValidationResult.fromErrorIf(control, "Не корректный MAC-адрес", !macValidatorNotNull(s));
        return ValidationResult.fromResults(nullOrEmpty, macAddress);
    };

    public static Validator<String> hostNameValidatorNotNull = (control, s) -> {
        s = Strings.isNullOrEmpty(s) ? "" : s;
        ValidationResult nullOrEmpty = ValidationResult.fromErrorIf(control, "Обязательное поле", Strings.isNullOrEmpty(s));
        ValidationResult hostAddress = ValidationResult.fromErrorIf(control, "Не корректное имя хоста", !InternetDomainName.isValid(s));
        return ValidationResult.fromResults(nullOrEmpty, hostAddress);
    };
    private static Properties config;

    public static void textFieldTrimAndLower(GridPane parentPane, boolean toLower) {
        parentPane.getChildren()
            .filtered(node -> node instanceof TextField)
            .forEach(node -> {
                if (!Strings.isNullOrEmpty(((TextField) node).getText())) {
                    if (toLower) {
                        ((TextField) node).setText(((TextField) node).getText().trim().toLowerCase());
                    } else {
                        ((TextField) node).setText(((TextField) node).getText().trim());
                    }
                }
            });
    }

    public static boolean macValidatorNotNull(String s) {
        if (!Strings.isNullOrEmpty(s)) {
            Pattern p = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
            Matcher m = p.matcher(s);
            return m.find();
        }
        return false;
    }

    public static int convertNetmaskToCidr(String netmask) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(netmask);
        byte[] netmaskBytes = inetAddress.getAddress();
        int cidr = 0;
        boolean zero = false;
        for (byte b : netmaskBytes) {
            int mask = 0x80;

            for (int i = 0; i < 8; i++) {
                int result = b & mask;
                if (result == 0) {
                    zero = true;
                } else if (zero) {
                    throw new IllegalArgumentException("Invalid netmask.");
                } else {
                    cidr++;
                }
                mask >>>= 1;
            }
        }
        return cidr;
    }

    public static boolean isInteger(String s) {
        char c = s.charAt(0);
        return c < '0' || c > '9';
    }

    public static List<String> getAllValuesFromMap(Map<String, Object> map) {
        List<String> result = new ArrayList<>();
        map.forEach((key, value) -> result.add(key + "=" + value));
        return result;
    }

    public static Properties getConfig() {
        if (config == null) {
            config = new Properties();
            try {
                config.load(Configuration.class.getClassLoader().getResourceAsStream(CONFIG_FILENAME));
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, "Cannot read config values from file:" + CONFIG_FILENAME, ex);
            }
        }
        return config;
    }

    public final class Params {
        // Main
        public static final String HOSTNAME_DB_0_0 = "hostname_db0_0";
        public static final String HOSTNAME_APP_0_0 = "hostname_app0_0";
        public static final String HOSTNAME_APP_1_0 = "hostname_app1_0";
        public static final String MAC_DB_0_0 = "mac_db0_0";
        public static final String MAC_APP_0_0 = "mac_app0_0";
        public static final String MAC_APP_1_0 = "mac_app1_0";
        public static final String IP_DB_0_0 = "ip_db0_0";
        public static final String IP_APP_0_0 = "ip_app0_0";
        public static final String IP_APP_1_0 = "ip_app1_0";
        public static final String IP_DB_0_1 = "ip_db0_1";
        public static final String IP_APP_0_1 = "ip_app0_1";
        public static final String IP_APP_1_1 = "ip_app1_1";
        public static final String GATEWAY = "gateway";
        public static final String NAMESERVER = "nameserver";
        public static final String NETMASK = "netmask";
        public static final String DOMAIN = "domain";

        // Other
        public static final String APPLICATION_CLUSTER_VIP = "application_cluster_vip";
        public static final String FED_SOE_VIP_FQDN = "fed_soe_vip_fqdn";
        public static final String FED_SPE_VIP_FQDN = "fed_spe_vip_fqdn";
        public static final String FED_SOE_PORT = "soe_port";
        public static final String FED_SPE_PORT = "spe_port";
        public static final String WEBGATE_TARGET_SYSTEMS = "webgate_target_systems";
        public static final String WEBGATE_FILTERS = "webgate_filters";
        public static final String APP_CLUSTER_FQDN = "app_cluster_fqdn";
        public static final String SSO_CLUSTER_FQDN = "sso_cluster_fqdn";
        public static final String MAIL_HOST = "MAIL_HOST";
        public static final String MAIL_PORT = "MAIL_PORT";
        public static final String MAIL_TO = "MAIL_TO";
        public static final String APP_RKASV_FQDN = "app_rkasv_fqdn";
        public static final String STOREPASS = "storepass";
        public static final String REGION_CODE = "region_code";
        public static final String REGION_CODE_BIUD = "region_code_biud";
        public static final String LOTUS_CERTIFIERID_FILE = "certifierIdFile";
        public static final String LOTUS_MAIL_DOMAIN = "mailDomain";
        public static final String LOTUS_INTERNET_ADDRESS_DOMAIN = "internetAddressDomain";
        public static final String LOTUS_ADMIN_NAME = "adminName_lotus";
        public static final String LOTUS_DEFAULT_DIRECTORY_FOR_ID_FILES = "defaultDirectoryForIDFiles";


        // Migration
        public static final String OIM_DB_IP = "oim_db_ip";
        public static final String OIM_DB_SHORT_FQDN = "oim_db_short_fqdn";
        public static final String OIM_DB_FULL_FQDN = "oim_db_full_fqdn";
        public static final String OIM_DB_NAME = "oim_db_name";
        public static final String OIM_DB_PASS = "oim_db_pass";
        public static final String OIM_DB_PORT = "oim_db_port";
        public static final String OIM_DB_USER = "oim_db_user";
        public static final String OES_DB_NAME = "oes_db_name";
        public static final String OES_DB_PASS = "oes_db_pass";
        public static final String OES_DB_PORT = "oes_db_port";
        public static final String SOA_DB_USER = "soa_db_user";
        public static final String OES_DB_USER = "oes_db_user";
        public static final String OIM_APP_IP = "oim_app_ip";
        public static final String OIM_APP_PORT = "oim_app_port";
        public static final String OIM_APP_USER = "oim_app_user";
        public static final String OIM_APP_PASS = "oim_app_pass";
        public static final String OIM_APP_FQDN = "oim_app_fqdn";

        // HA
        public static final String APP_1_IP = "app_1_ip";
        public static final String HA_1_IP = "ha_1_ip";
        public static final String SSO_1_IP = "sso_1_ip";
        public static final String APP_2_IP = "app_2_ip";
        public static final String HA_2_IP = "ha_2_ip";
        public static final String SSO_2_IP = "sso_2_ip";
        public static final String APP_1_INTER_IP = "app_1_inter_ip";
        public static final String HA_1_INTER_IP = "ha_1_inter_ip";
        public static final String SSO_1_INTER_IP = "sso_1_inter_ip";
        public static final String APP_2_INTER_IP = "app_2_inter_ip";
        public static final String HA_2_INTER_IP = "ha_2_inter_ip";
        public static final String SSO_2_INTER_IP = "sso_2_inter_ip";
        public static final String APP_1_FQDN = "app_1_fqdn";
        public static final String APP_1_SHORT_FQDN = "app_1_short_fqdn";
        public static final String APP_1_INTER_FQDN = "app_1_inter_fqdn";
        public static final String HA_1_FQDN = "ha_1_fqdn";
        public static final String HA_1_SHORT_FQDN = "ha_1_short_fqdn";
        public static final String HA_1_INTER_FQDN = "ha_1_inter_fqdn";
        public static final String SSO_1_FQDN = "sso_1_fqdn";
        public static final String SSO_1_SHORT_FQDN = "sso_1_short_fqdn";
        public static final String SSO_1_INTER_FQDN = "sso_1_inter_fqdn";
        public static final String APP_2_FQDN = "app_2_fqdn";
        public static final String APP_2_SHORT_FQDN = "app_2_short_fqdn";
        public static final String APP_2_INTER_FQDN = "app_2_inter_fqdn";
        public static final String HA_2_FQDN = "ha_2_fqdn";
        public static final String HA_2_SHORT_FQDN = "ha_2_short_fqdn";
        public static final String HA_2_INTER_FQDN = "ha_2_inter_fqdn";
        public static final String SSO_2_FQDN = "sso_2_fqdn";
        public static final String SSO_2_SHORT_FQDN = "sso_2_short_fqdn";
        public static final String SSO_2_INTER_FQDN = "sso_2_inter_fqdn";
        public static final String DATABASE_IP = "database_ip";
        public static final String DATABASE_FQDN = "database_fqdn";
        public static final String JOSSO_WIN_DOMAIN = "josso_win_domain";
        public static final String JOSSO_DC_NAME = "josso_dc_name";
        public static final String KERBEROS_KVN = "kerberos_kvn";
        public static final String KERBEROS_DC1_IP = "kerberos_dc1_ip";
        public static final String KERBEROS_DC2_IP = "kerberos_dc2_ip";
        public static final String JOSSO_PROVIDER_URL = "josso_provider_url";
        public static final String JOSSO_SECURITY_PRINCIPAL = "josso_security_principal";
        public static final String JOSSO_SECURITY_CREDENTIAL = "josso_security_credential";
        public static final String JOSSO_USER_DN = "josso_user_dn";
        public static final String JOSSO_ROLES_DN = "josso_roles_dn";
        public static final String PRIMARY_ETH = "primary_eth";
        public static final String SECONDARY_ETH = "secondary_eth";
        public static final String ROUTER_ID = "router_id";
        public static final String KA_SUBNET_PREFIX = "ka_subnet_prefix";
    }
}