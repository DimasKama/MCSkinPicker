package com.dimaskama.mcskinpicker.app;

import com.dimaskama.mcskinpicker.Main;
import com.dimaskama.mcskinpicker.NetworkUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.hycrafthd.minecraft_authenticator.login.Authenticator;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class AuthenticationController {

    @FXML
    private VBox box;

    @FXML
    private TextField authURL;

    @FXML
    private Label actionDisplay;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private void onLogin() {
        lock(true);
        progressBar.setVisible(true);
        Task<Boolean> task = new Task<>() {
            private final String URL = authURL.getText();

            @Override
            protected Boolean call() {
                String code = null;
                try {
                    String param = URL.split("\\?")[1].split("&")[0];
                    if (param.startsWith("code="))
                        code = param.split("=")[1];
                } catch (Exception ignored) {
                }
                if (code == null) {
                    return false;
                }
                Main.setAuth(
                        Authenticator.ofMicrosoft(code)
                                .shouldAuthenticate()
                                .build()
                );
                return Main.runAuth(state -> {
                    switch (state) {
                        case INITAL_FILE -> updateProgress(1, 7);
                        case LOGIN_MICOSOFT -> updateProgress(2, 7);
                        case XBL_TOKEN -> updateProgress(3, 7);
                        case XSTS_TOKEN -> updateProgress(4, 7);
                        case ACCESS_TOKEN -> updateProgress(5, 7);
                        case ENTITLEMENT -> updateProgress(6, 7);
                        case PROFILE -> updateProgress(7, 7);
                    }
                });
            }
        };
        task.setOnSucceeded(event -> {
            progressBar.setVisible(false);
            lock(false);
            NetworkUtils.updateProfileInfo();
            try {
                actionDisplay.setText(task.get() ? "Login complete" : "Login Failed");
                if (task.get())
                    ((Stage) progressBar.getScene().getWindow()).close();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        progressBar.progressProperty().bind(task.progressProperty());
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void onOpenBrowser() {
        try {
            Desktop.getDesktop().browse(Authenticator.microsoftLogin().toURI());
        } catch (URISyntaxException | IOException e) {
            Main.LOGGER.error(e);
        }
    }

    private void lock(boolean lock) {
        box.setDisable(lock);
    }

}