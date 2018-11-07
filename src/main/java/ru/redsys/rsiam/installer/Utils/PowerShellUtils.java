package ru.redsys.rsiam.installer.Utils;

import com.google.common.base.Strings;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PowerShellUtils {

    public final static String ADMakeCerts = "AD_Make-Certs.ps1";
    public final static String ADCheckUser = "AD_Check-User.ps1";
    public final static String ADCreateDNSRecords = "AD_Create-DNS-Records.ps1";
    public final static String ADCreateUser = "AD_Create-User.ps1";
    public final static String VMGetDatastore = "VM_Get-Datastore.ps1";
    public final static String VMGetResourcePool = "VM_Get-ResourcePool.ps1";
    public final static String VMGetVMHost = "VM_Get-VMHost.ps1";
    public final static String VMGetVDPortgroup = "VM_Get-VDPortgroup.ps1";
    public final static String VMGetGuestOS = "VM_Get-GuestOS.ps1";
    public final static String VMGetDatacenter = "VM_Get-Datacenter.ps1";
    public final static String VMGetCluster = "VM_Get-Cluster.ps1";
    public final static String VMGetFolder = "VM_Get-Folder.ps1";
    public final static String VMGetLocationChildren = "VM_Get-Location-Children.ps1";
    public final static String VMSetLocation = "VM_Set-Location.ps1";
    public final static String VMMoveLocationChildren = "VM_Move-Location-Children.ps1";
    public final static String VMNewVM = "VM_New-VM.ps1";
    public final static String VMCopyISO = "VM_Copy-ISO.ps1";
    public final static String VMMountISO = "VM_Mount-ISO.ps1";

    private final static String MAX_WAIT_TIME = "150000";
    private final static String WAIT_PAUSE = "1500";
    private final static String VMGetConnection = "VM_Get-Connection.ps1";

    public static String server;
    public static String user;
    public static String password;

    public static PowerShell powerShell;

    public static PowerShellResponse getConnection() {
        return getConnection(server, user, password);
    }

    private static PowerShellResponse getConnection(String server, String user, String password) {
        Logger.getLogger(PowerShellUtils.class.getName()).log(Level.INFO, "Attempt to connect to VCenter.");

        if (Strings.isNullOrEmpty(server) || Strings.isNullOrEmpty(user) || Strings.isNullOrEmpty(password)) {
            return null;
        }

        if (powerShell != null) {
            powerShell.close();
        }

        powerShell = null;
        PowerShellResponse response = null;
        try {
            powerShell = PowerShell.openSession();

            Map<String, String> config = new HashMap<>();
            config.put("maxWait", MAX_WAIT_TIME);
            config.put("waitPause", WAIT_PAUSE);

            BufferedReader reader = new BufferedReader(new InputStreamReader(PowerShellUtils.class.getResourceAsStream("/scripts/" + VMGetConnection)));

            String args = "-Server" + " " + "\"" + server + "\"" + " " +
                "-User" + " " + "\"" + user + "\"" + " " +
                "-Password" + " " + "\"" + password + "\"";

            response = powerShell.configuration(config).executeScript(reader, args);
        } catch (Exception ex) {
            Logger.getLogger(PowerShellUtils.class.getName()).log(Level.SEVERE, "Exception.", ex.getStackTrace());
        }

        return response;
    }

    public static PowerShellResponse executePowerShellScript(String script, String args) {
        Logger.getLogger(PowerShellUtils.class.getName()).log(Level.INFO, "Execute powerShell script: " + script);

        if (powerShell == null) {
            if (server != null && user != null && password != null) {
                getConnection();
            } else {
                powerShell = PowerShell.openSession();

                Map<String, String> config = new HashMap<>();
                config.put("maxWait", MAX_WAIT_TIME);
                config.put("waitPause", WAIT_PAUSE);

                powerShell.configuration(config);
            }
        }

        PowerShellResponse response = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(PowerShellUtils.class.getResourceAsStream("/scripts/" + script)));
            response = powerShell.executeScript(reader, args);
        } catch (Exception ex) {
            Logger.getLogger(PowerShellUtils.class.getName()).log(Level.SEVERE, "Exception.", ex.getStackTrace());
        }

        Logger.getLogger(PowerShellUtils.class.getName()).log(Level.INFO, "result: " + response);
        return response;
    }

    public static List<String> powerShellResponseToList(PowerShellResponse response) {
        List<String> result = new ArrayList<>();

        if (response == null) {
            return result;
        }
        result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(response.getCommandOutput().replaceAll("%2f", "/")))) {
            result = reader.lines().collect(Collectors.toList());
            result.removeAll(Collections.singleton(null));
            result.removeAll(Collections.singleton(""));
        } catch (IOException ex) {
            Logger.getLogger(PowerShellUtils.class.getName()).log(Level.SEVERE, "Cannot collect result.", ex);
        } catch (Exception ex) {
            Logger.getLogger(PowerShellUtils.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
        return result;
    }
}