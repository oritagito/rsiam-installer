package ru.redsys.rsiam.installer.Form;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

public interface FormController extends Initializable {
    void handleOnNextButtonAction(ActionEvent event);

    void handleOnBackButtonAction(ActionEvent event);

    void handleOnCloseButtonAction(ActionEvent event);

    void handleOnHelpButtonAction(ActionEvent event);
}