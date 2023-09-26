package com.dimaskama.mcskinpicker.app;

import com.dimaskama.mcskinpicker.Main;
import com.dimaskama.mcskinpicker.Options;
import com.dimaskama.mcskinpicker.renderer.IsometricRenderer;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class SkinEntryController {
    private BufferedImage src;
    @FXML
    private ImageView preview;
    @FXML
    private Label name;
    @FXML
    private TextField newName;

    @FXML
    private void onNewName() {
        newName.setVisible(false);
        if (!newName.getText().isEmpty()) {
            try {
                File file = new File(getPath());
                if (!file.exists() || !file.renameTo(new File(Options.getInstance().lastDirectory + "\\" + newName.getText() + ".png")))
                    throw new IOException();
            } catch (IOException e) {
                Main.LOGGER.error(e);
            }
            newName.setText("");
            App.controller.updateSkins();
        }
    }

    @FXML
    private void onRename() {
        if (newName.isVisible())
            onNewName();
        else
            newName.setVisible(true);
    }

    @FXML
    private void onDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(String.format("Confirm delete '%s'", name.getText()));
        alert.setHeaderText("");
        alert.setGraphic(null);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                File file = new File(getPath());
                if (!(file.exists() && file.canWrite() && file.delete()))
                    throw new IOException("Unable to delete skin");
            } catch (IOException e) {
                Main.LOGGER.error(e);
            }
            App.controller.updateSkins();
        }
    }

    @FXML
    private void onUse() {
        App.controller.lock(true);
        try {
            new ChangeSkinStage(src, preview.getImage()).showAndWait();
        } catch (IOException e) {
            Main.LOGGER.error(e);
        }
        App.controller.lock(false);
    }

    public void setImage(BufferedImage src) {
        this.src = src;
        preview.setImage(SwingFXUtils.toFXImage(new IsometricRenderer().renderHead(src, 128, true), null));
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public String getPath() {
        return Options.getInstance().lastDirectory + "\\" + name.getText() + ".png";
    }

    private static class ChangeSkinStage extends Stage {
        public ChangeSkinStage(BufferedImage src, Image preview) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("change-skin-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 300);
            setMinWidth(320);
            setMinHeight(300);
            setTitle("Change skin");
            getIcons().add(App.icon);
            ((ChangeSkinController) fxmlLoader.getController()).init(src, preview);
            setScene(scene);
        }
    }
}
