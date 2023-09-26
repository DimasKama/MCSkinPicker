package com.dimaskama.mcskinpicker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Options {

    public String lastDirectory = null;

    public Options() {
    }

    private static Options OPTIONS;

    public static void loadOrCreate() throws IOException {
        if (OPTIONS != null) return;
        File file = new File(Main.OPTIONS_FILE_PATH);
        if (file.exists()) {
            Gson gson = new Gson();
            OPTIONS = gson.fromJson(new FileReader(file), Options.class);
        } else {
            OPTIONS = new Options();
            OPTIONS.save();
        }
    }

    public static Options getInstance() {
        return OPTIONS;
    }

    public void save() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        File file = new File(Main.OPTIONS_FILE_PATH);
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs())
            throw new RuntimeException("Unable to save options file");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(gson.toJson(this));
        } catch (IOException e) {
            Main.LOGGER.error(e);
        }
    }
}
