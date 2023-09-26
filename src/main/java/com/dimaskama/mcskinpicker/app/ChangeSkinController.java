package com.dimaskama.mcskinpicker.app;

import com.dimaskama.mcskinpicker.Main;
import com.dimaskama.mcskinpicker.NetworkUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ChangeSkinController {
    private BufferedImage src;
    @FXML
    private ImageView preview;
    @FXML
    private ChoiceBox<String> variantChoice;
    @FXML
    private ChoiceBox<String> capeChoice;
    @FXML
    private Label info;

    @FXML
    private void onUse() {
        Main.Cape cape = Main.capes.stream().filter(c -> c.alias().equalsIgnoreCase(capeChoice.getValue())).findAny().orElse(null);
        int result = NetworkUtils.changeSkin(src, variantChoice.getValue().equalsIgnoreCase("Slim"));
        if (cape == null) NetworkUtils.deleteCape();
        else if (!cape.active()) NetworkUtils.changeCape(cape.id());
        if (result == 200) {
            NetworkUtils.updateProfileInfo();
            App.controller.updateScreen();
            onBack();
            return;
        }
        info.setText("Failed upload skin");
    }

    @FXML
    private void onBack() {
        ((Stage) capeChoice.getScene().getWindow()).close();
    }

    public void init(BufferedImage src, Image preview) {
        this.src = src;
        this.preview.setImage(preview);
        ArrayList<String> aliases = new ArrayList<>(Main.capes.stream().map(Main.Cape::alias).toList());
        aliases.add("none");
        ObservableList<String> list = FXCollections.observableList(aliases);
        capeChoice.setItems(list);
        capeChoice.setValue("none");
        variantChoice.setItems(FXCollections.observableArrayList("Slim", "Classic"));
        variantChoice.setValue("Classic");
    }
}
