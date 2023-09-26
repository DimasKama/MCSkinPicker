package com.dimaskama.mcskinpicker.app;

import com.dimaskama.mcskinpicker.Main;
import com.dimaskama.mcskinpicker.NetworkUtils;
import com.dimaskama.mcskinpicker.Options;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile;
import net.hycrafthd.minecraft_authenticator.login.Authenticator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class App extends Application {
    public static AppController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-screen.fxml"));
        primaryStage.setScene(new Scene(loader.load(), 800, 600));
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);
        controller = loader.getController();
        primaryStage.setTitle("MCSkinPicker");
        primaryStage.show();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    Options.loadOrCreate();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to load options: " + e);
                }
                try (InputStream in = new FileInputStream(Main.AUTH_FILE_PATH)) {
                    AuthenticationFile authFile = AuthenticationFile.readCompressed(in);
                    Main.setAuth(
                            Authenticator.of(authFile)
                                    .shouldAuthenticate()
                                    .build()
                    );
                    Main.runAuth();
                } catch (IOException e) {
                    Main.LOGGER.error(e);
                }

                NetworkUtils.updateProfileInfo();

                return null;
            }
        };
        task.setOnSucceeded(event -> {
            controller.setLoading(false);
            controller.lock(false);

            controller.updateScreen();
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public static void main(String[] args) {
        launch();
    }

}
