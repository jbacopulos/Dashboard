module org.dashboard.dashboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires com.twelvemonkeys.imageio.core;
    requires com.twelvemonkeys.imageio.webp;

    requires java.desktop;
    requires io.github.cdimascio.dotenv.java;

    opens org.dashboard.dashboard to javafx.fxml;
    exports org.dashboard.dashboard;
}