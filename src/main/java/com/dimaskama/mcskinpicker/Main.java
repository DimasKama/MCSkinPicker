package com.dimaskama.mcskinpicker;

import com.dimaskama.mcskinpicker.app.App;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile;
import net.hycrafthd.minecraft_authenticator.login.Authenticator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class Main {
    public static final String APP_FOLDER = System.getenv("LOCALAPPDATA") + "\\MCSkinPicker";
    public static final String OPTIONS_FILE_PATH = APP_FOLDER + "\\options.json";
    public static final String AUTH_FILE_PATH = APP_FOLDER + "\\auth.bin";
    public static final Logger LOGGER = LogManager.getLogger("Main");
    private static Authenticator authenticator = null;
    public static BufferedImage currentSkin = null;
    public static final ArrayList<Cape> capes = new ArrayList<>();

    public static void saveAuthFile(AuthenticationFile authFile) {
        Main.LOGGER.info("Trying save auth file...");
        File file = new File(Main.AUTH_FILE_PATH);
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs() || authFile == null) {
            Main.LOGGER.error("Unable to save authentication file");
            return;
        }
        try (OutputStream out = new FileOutputStream(file)) {
            authFile.writeCompressed(out);
        } catch (IOException e) {
            Main.LOGGER.error(e);
        }
    }

    public static boolean runAuth() {
        return runAuth(Authenticator.LoginStateCallback.NOOP);
    }

    public static boolean runAuth(Authenticator.LoginStateCallback callback) {
        if (authenticator == null) return false;
        try {
            authenticator.run(callback);
        } catch (AuthenticationException e) {
            LOGGER.error("Authentication failed! " + e);
            saveAuthFile(authenticator.getResultFile());
            return false;
        }
        saveAuthFile(authenticator.getResultFile());
        LOGGER.info("Authentication done");
        return true;
    }

    public static Authenticator getAuth() {
        return authenticator;
    }

    public static void setAuth(Authenticator authenticator) {
        Main.authenticator = authenticator;
    }

    public static void main(String[] args) {
        App.main(args);
    }

    public record Cape(String alias, String id, boolean active) {
    }
}
