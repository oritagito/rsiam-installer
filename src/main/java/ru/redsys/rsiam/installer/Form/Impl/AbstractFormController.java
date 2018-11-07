package ru.redsys.rsiam.installer.Form.Impl;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import ru.redsys.rsiam.installer.Form.FormController;
import ru.redsys.rsiam.installer.Utils.Configuration;
import ru.redsys.rsiam.installer.Utils.NewStage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class AbstractFormController implements FormController {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void handleOnNextButtonAction(ActionEvent event) {

    }

    @Override
    public void handleOnBackButtonAction(ActionEvent event) {

    }

    @Override
    public void handleOnCloseButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Close - %s", event.getSource()));

        if (Configuration.buildConfigFileMap != null) {
            String configPath = Configuration.programPath + File.separator + Configuration.getConfig().getProperty("defaultName") + File.separator + Configuration.getConfig().getProperty("defaultFolder") + File.separator + "bak.conf";
            try {
                File file = new File(configPath);
                CharSink sink = Files.asCharSink(file, Charsets.UTF_8);
                sink.writeLines(Configuration.getAllValuesFromMap(Configuration.buildConfigFileMap));
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Failed to save file", ex.getMessage());
            }
        }

        Platform.exit();
    }

    @Override
    public void handleOnHelpButtonAction(ActionEvent event) {
        logger.log(Level.INFO, () -> String.format("Help - %s", event.getSource()));
        new NewStage().openHelpStage();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}