package ru.redsys.rsiam.installer.WorkIndicatorDialogs;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.redsys.rsiam.installer.Utils.PowerShellUtils.*;

public class MultiPowerShellScripts {

    private final ProgressIndicator progressIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
    private final Stage dialog = new Stage(StageStyle.UNDECORATED);
    private final Label label = new Label();
    private final Group root = new Group();
    private final Scene scene = new Scene(root, 300, 100);
    private final BorderPane mainPane = new BorderPane();
    private final VBox vbox = new VBox();
    private Task animationWorker;
    private ObservableList<Map<String, List<String>>> resultNotificationList = FXCollections.observableArrayList();

    private Map<String, List<String>> resultValue = new HashMap<>();

    public MultiPowerShellScripts(Window owner, String label) {
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.setResizable(false);
        this.label.setText(label);
    }

    public void addTaskEndNotification(Consumer<Map<String, List<String>>> c) {
        resultNotificationList.addListener((ListChangeListener<? super Map<String, List<String>>>) n -> {
            resultNotificationList.clear();
            c.accept(resultValue);
        });
    }

    public void exec() {
        setupDialog();
        setupAnimationThread();
        setupWorkerThread();
    }

    private void setupDialog() {
        root.getChildren().add(mainPane);

        String cssLayout = "-fx-border-color: gray;\n" +
            "-fx-border-insets: 1;\n" +
            "-fx-border-width: 1;\n" +
            "-fx-border-style: solid inside;\n";

        vbox.setStyle(cssLayout);

        vbox.setSpacing(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.setMinSize(300, 100);
        vbox.getChildren().addAll(label, progressIndicator);
        mainPane.setTop(vbox);
        dialog.setScene(scene);

        dialog.show();
    }

    private void setupAnimationThread() {
        animationWorker = new Task() {
            @Override
            protected Object call() {
                return true;
            }
        };

        progressIndicator.setProgress(0);
        progressIndicator.progressProperty().unbind();
        progressIndicator.progressProperty().bind(animationWorker.progressProperty());

        animationWorker.messageProperty().addListener((observable, oldValue, newValue) -> Logger.getLogger(MultiPowerShellScripts.class.getName()).log(Level.INFO, newValue));

        new Thread(animationWorker).start();
    }

    private void setupWorkerThread() {
        Task<Map<String, List<String>>> taskWorker = new Task<Map<String, List<String>>>() {
            @Override
            protected Map<String, List<String>> call() {
                Map<String, List<String>> result = new HashMap<>();
                result.put("datacenter", powerShellResponseToList(executePowerShellScript(VMGetDatacenter, "")));
                updateMessage("Get datacenter completed");
                updateProgress(16, 100);

                result.put("datastore", powerShellResponseToList(executePowerShellScript(VMGetDatastore, "")));
                updateMessage("Get datastore completed");
                updateProgress(32, 100);

                result.put("folder", powerShellResponseToList(executePowerShellScript(VMGetFolder, "")));
                updateMessage("Get folder completed");
                updateProgress(48, 100);

                result.put("guestOS", powerShellResponseToList(executePowerShellScript(VMGetGuestOS, "")));
                updateMessage("Get guestOS completed");
                updateProgress(64, 100);

                result.put("vmHost", powerShellResponseToList(executePowerShellScript(VMGetVMHost, "")));
                updateMessage("Get vmHost completed");
                updateProgress(80, 100);

                result.put("vdPortgroup", powerShellResponseToList(executePowerShellScript(VMGetVDPortgroup, "")));
                updateMessage("Get vdPortgroup completed");
                updateProgress(100, 100);

                return result;
            }
        };

        taskWorker.messageProperty().addListener((observable, oldValue, newValue) -> Logger.getLogger(MultiPowerShellScripts.class.getName()).log(Level.INFO, newValue));

        EventHandler<WorkerStateEvent> eh = event -> {
            animationWorker.cancel(true);
            progressIndicator.progressProperty().unbind();
            dialog.close();
            try {
                resultValue = taskWorker.get();
                resultNotificationList.add(resultValue);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        progressIndicator.progressProperty().bind(taskWorker.progressProperty());

        taskWorker.setOnSucceeded(eh);
        taskWorker.setOnFailed(eh);

        new Thread(taskWorker).start();
    }
}