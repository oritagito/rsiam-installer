package ru.redsys.rsiam.installer.Form.Impl;

import javafx.scene.text.Text;
import ru.redsys.rsiam.installer.Utils.Configuration;

import java.net.URL;
import java.util.ResourceBundle;

public class FinalFormController extends AbstractFormController {

    public Text bodyLabel1;
    public Text bodyLabel2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if ("auto".equals(Configuration.typeOfInstall)) {
            bodyLabel1.setText("Процесс автоматической установки программы \"RS:Управление доступом\" инициирован.\n" +
                "Ожидаете сообщений о процессе установки на введенный Вами почтовый адрес.\n");
            bodyLabel2.setDisable(true);
        }
    }
}