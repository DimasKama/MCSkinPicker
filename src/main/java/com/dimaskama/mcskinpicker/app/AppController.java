package com.dimaskama.mcskinpicker.app;

import com.dimaskama.mcskinpicker.Main;
import com.dimaskama.mcskinpicker.Options;
import com.dimaskama.mcskinpicker.renderer.IsometricRenderer;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import net.hycrafthd.minecraft_authenticator.login.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AppController {
    @FXML
    private VBox box;
    @FXML
    private Button loginButton;
    @FXML
    private Button directoryButton;
    @FXML
    private Button explorerButton;
    @FXML
    private ImageView loading;
    @FXML
    private Label currentAccount;
    @FXML
    private ImageView accountSkin;
    @FXML
    private Label currentDirectory;
    @FXML
    private VBox container;

    @FXML
    private void onLogin() {
        lock(true);
        try {
            new AuthenticationStage().showAndWait();
        } catch (IOException e) {
            Main.LOGGER.error(e);
        }
        updateScreen();
        lock(false);
    }

    @FXML
    private void onDirectory() {
        lock(true);
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose skin Directory");
        String option = Options.getInstance().lastDirectory;
        File lastDirectory = (option == null ? null : new File(option));
        if (lastDirectory != null && lastDirectory.exists())
            chooser.setInitialDirectory(lastDirectory);
        File file = chooser.showDialog(null);
        if (file != null && file.canRead()) {
            Options.getInstance().lastDirectory = file.getPath();
            Options.getInstance().save();
            updateScreen();
        }
        lock(false);
    }

    @FXML
    private void onExplorer() {
        File dir = new File(Options.getInstance().lastDirectory);
        try {
            if (dir.exists())
                Desktop.getDesktop().open(dir);
        } catch (IOException e) {
            Main.LOGGER.error(e);
        }
    }

    public void updateScreen() {
        try {
            Optional<User> optional = Main.getAuth().getUser();
            currentAccount.setText("Account: " + (optional.isPresent() ? optional.get().name() : "ERROR"));
            loginButton.setText(optional.isPresent() ? "Change account" : "Login to Microsoft");
        } catch (NullPointerException e) {
            currentAccount.setText("No account");
        }
        if (Main.currentSkin != null)
            accountSkin.setImage(SwingFXUtils.toFXImage(new IsometricRenderer().renderHead(Main.currentSkin, 64, true), null));
        String lastDirectory = Options.getInstance().lastDirectory;
        directoryButton.setText(lastDirectory == null ? "Open Directory" : "Change Directory");
        explorerButton.setVisible(lastDirectory != null);
        String[] path = lastDirectory == null ? new String[0] : lastDirectory.split("\\\\");
        currentDirectory.setText("Current Directory: " + (path.length > 0 ? path[path.length - 1] : "not selected"));

        updateSkins();
    }

    public void updateSkins() {
        if (Options.getInstance().lastDirectory == null) return;
        File directory = new File(Options.getInstance().lastDirectory);
        File[] files = directory.listFiles();
        if (files == null) return;
        List<File> skins = Arrays.stream(files).filter(file -> file.getPath().endsWith(".png")).toList();
        container.getChildren().clear();
        URL resource = getClass().getResource("skin-entry.fxml");
        for (File skin : skins) {
            BufferedImage src;
            try (FileInputStream in = new FileInputStream(skin)) {
                src = ImageIO.read(in);
            } catch (IOException e) {
                Main.LOGGER.error(e);
                continue;
            }
            if (src.getWidth() != 64) continue;
            try {
                FXMLLoader loader = new FXMLLoader(resource);
                AnchorPane pane = loader.load();
                SkinEntryController controller = loader.getController();
                controller.setImage(src);
                controller.setName(skin.getName().replaceFirst("[.][^.]+$", ""));
                container.getChildren().add(pane);
                container.getChildren().add(new Separator());
            } catch (IOException e) {
                Main.LOGGER.error(e);
            }
        }
    }

    public void setLoading(boolean visible) {
        loading.setVisible(visible);
    }

    public void lock(boolean lock) {
        box.setDisable(lock);
    }

    private static class AuthenticationStage extends Stage {
        public AuthenticationStage() throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("authentication-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 300);
            setMinWidth(320);
            setMinHeight(300);
            setTitle("Authentication");
            setScene(scene);
        }
    }
}
