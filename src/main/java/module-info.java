module com.dimaskama.mcskinpicker {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires net.hycrafthd.minecraft_authenticator;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpmime;

    exports com.dimaskama.mcskinpicker;
    exports com.dimaskama.mcskinpicker.app;
    opens com.dimaskama.mcskinpicker.app to javafx.fxml;
}